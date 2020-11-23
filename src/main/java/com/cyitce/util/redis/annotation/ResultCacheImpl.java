package com.cyitce.util.redis.annotation;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * @author jianhongyu
 * @version 1.0
 * @date 2020/11/19 20:11
 * @see ResultCache
 * 该类为ResultCache具体实现类，给予Spring-Aop实现。
 */
@Aspect
@Component
public class ResultCacheImpl {

    private final RedisTemplate redisTemplate;
    private final Logger logger = Logger.getLogger(ResultCacheImpl.class.getName());
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public ResultCacheImpl(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
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
        Object cache = redisTemplate.opsForValue().get(cacheKey);
        if (cache != null) {
            result = cache;
            long end = System.currentTimeMillis();
            logger.info(methodName + " - use cache, used time " + (end - start) + "ms");
            if (!resultCache.callbackMethod().isEmpty())
                result = toCallback(methodName, joinPoint.getTarget(), resultCache.callbackMethod(), result);

        } else {
            // 当缓存不存在，或者过期时，开启一个锁，改锁最多持续3分钟
            if (redisTemplate.opsForValue().setIfAbsent(cacheKey + ":multi", "multi", 3, TimeUnit.MINUTES)) {
                try {
                    result = joinPoint.proceed(joinPoint.getArgs());
                    long expire = 0;
                    if (resultCache.expire() > 0) {
                        expire = resultCache.expire();
                        long[] expireRandomAppend = resultCache.expireRandomAppend();
                        if (expireRandomAppend.length == 2 && expireRandomAppend[0] <= expireRandomAppend[1])
                            expire += expireRandomAppend[0] + (long) ((expireRandomAppend[1] - expireRandomAppend[0]) * Math.random());
                    }
                    result = (result == null ? resultCache.nullSave() : result);
                    if (expire > 0) {
                        redisTemplate.opsForValue().set(cacheKey, result, expire, TimeUnit.MILLISECONDS);
                    } else {
                        redisTemplate.opsForValue().set(cacheKey, result);
                    }
                    if (!redisTemplate.delete(cacheKey + ":multi")) {
                        logger.warning(methodName + " - delete multi failed");
                    }
                    long end = System.currentTimeMillis();
                    logger.info(methodName + " - save cache, used time " + (end - start) + "ms");
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            } else {
                // 等待拿锁的进程结束
                for (int i = 0; i < 20; i++) { // 最多等待20次
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if ((result = redisTemplate.opsForValue().get(cacheKey)) != null) {
                        break;
                    }
                }
                long end = System.currentTimeMillis();
                if (result == null) {
                    logger.warning(methodName + " - wait cache failed, used time " + (end - start) + "ms");
                } else {
                    logger.info(methodName + " - wait cache success, used time " + (end - start) + "ms");
                }
                if (!resultCache.callbackMethod().isEmpty())
                    result = toCallback(methodName, joinPoint.getTarget(), resultCache.callbackMethod(), result);
            }
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
