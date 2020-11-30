package com.cyitce.util.redis.annotation;

import com.cyitce.util.redis.RedisUtil;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * @author jianhongyu
 * @version 1.1
 * @date 2020/11/19 20:11
 * @see ResultCache
 * 该类为ResultCache注解的具体实现类，基于Spring-Aop实现。
 */
@Aspect
@Component
public class ResultCacheImpl {

    public static final int WAIT_TIMES = 20;
    public static final int EXPIRE_RANDOM_LENGTH = 2;
    private final Logger logger = Logger.getLogger(ResultCacheImpl.class.getName());
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RedisUtil redisUtil;

    @Autowired
    public ResultCacheImpl(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
    }

    @Pointcut("@annotation(com.cyitce.util.redis.annotation.ResultCache)")
    public void reqCachePointcut() {
    }

    @Around("reqCachePointcut() && @annotation(resultCache)")
    public Object dealCache(ProceedingJoinPoint joinPoint, ResultCache resultCache) {
        String methodName = "ResultCache:" + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
        long start = System.currentTimeMillis();
        Object result = null;
        StringBuilder keyBuilder = new StringBuilder(resultCache.key().isEmpty() ? methodName : resultCache.key());
        for (int param : resultCache.params()) {
            try {
                keyBuilder.append(':').append(objectMapper.writeValueAsString(joinPoint.getArgs()[param]));
            } catch (JsonProcessingException e) {
                keyBuilder.append(joinPoint.getArgs()[param].toString());
            }
        }
        String cacheKey = keyBuilder.toString();
        logger.info(methodName + " - cache key: " + cacheKey);
        Object cache = redisUtil.get(cacheKey);
        if (cache != null) {
            result = cache;
            long end = System.currentTimeMillis();
            logger.info(methodName + " - use cache, used time " + (end - start) + "ms");
            if (!resultCache.callbackMethod().isEmpty()) {
                result = toCallback(methodName, joinPoint.getTarget(), resultCache.callbackMethod(), result);
            }

        } else {
            if (resultCache.syncLock()) {
                // 当缓存不存在，或者过期时，开启一个锁
                if (redisUtil.lock(cacheKey, resultCache.maxLockTime(), TimeUnit.MILLISECONDS)) {
                    // redisTemplate.opsForValue().setIfAbsent(cacheKey + LOCK, LOCK/*, resultCache.maxLockTime(), TimeUnit.MILLISECONDS*/)) {
                    // redisTemplate.expire(cacheKey + LOCK,resultCache.maxLockTime(), TimeUnit.MILLISECONDS);
                    logger.info(methodName + " - set lock success");
                    result = doSaveCache(joinPoint, resultCache, cacheKey);
                    if (!redisUtil.unlock(cacheKey)) {
                        logger.warning(methodName + " - unlock failed");
                    }
                    long end = System.currentTimeMillis();
                    logger.info(methodName + " - save cache has lock, used time " + (end - start) + "ms");
                } else {
                    // 等待拿锁的进程结束
                    // 最多等待20次
                    for (int i = 0; i < WAIT_TIMES; i++) {
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if ((result = redisUtil.get(cacheKey)) != null) {
                            break;
                        }
                    }
                    long end = System.currentTimeMillis();
                    if (result == null) {
                        logger.warning(methodName + " - wait cache failed, used time " + (end - start) + "ms");
                    } else {
                        logger.info(methodName + " - wait cache success, used time " + (end - start) + "ms");
                    }
                    if (!resultCache.callbackMethod().isEmpty()) {
                        result = toCallback(methodName, joinPoint.getTarget(), resultCache.callbackMethod(), result);
                    }
                }
            } else {

                result = doSaveCache(joinPoint, resultCache, cacheKey);
                long end = System.currentTimeMillis();
                logger.info(methodName + " - save cache no lock, used time " + (end - start) + "ms");
            }
        }
        // 返回类型不统一，返回空
        if (result != null && !((MethodSignature) joinPoint.getSignature()).getReturnType().equals(result.getClass())) {
            result = null;
        }
        return result;
    }

    private Object doSaveCache(ProceedingJoinPoint joinPoint, ResultCache resultCache, String cacheKey) {
        Object result = null;
        try {
            result = joinPoint.proceed(joinPoint.getArgs());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        long expire = 0;
        if (resultCache.expire() > 0) {
            expire = resultCache.expire();
            long[] expireRandomAppend = resultCache.expireRandomAppend();
            if (expireRandomAppend.length == EXPIRE_RANDOM_LENGTH && expireRandomAppend[0] <= expireRandomAppend[1]) {
                expire += expireRandomAppend[0] + (long) ((expireRandomAppend[1] - expireRandomAppend[0]) * Math.random());
            }
        }
        result = (result == null ? resultCache.nullSave() : result);
        if (expire > 0) {
            redisUtil.set(cacheKey, result, expire, TimeUnit.MILLISECONDS);
        } else {
            redisUtil.set(cacheKey, result);
        }
        return result;
    }

    private Object toCallback(String methodName, Object target, String callbackMethod, Object result) {
        logger.info(methodName + " - call " + callbackMethod + "(java.lang.Object);");
        try {
            Method method = target.getClass().getDeclaredMethod(callbackMethod, Object.class);
            result = method.invoke(target, result);
            logger.info(methodName + " - callback finished");
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            logger.warning(methodName + " - callback failed: ");
            e.printStackTrace();
        }
        return result;
    }
}
