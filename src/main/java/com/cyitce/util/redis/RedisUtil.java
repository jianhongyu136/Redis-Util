package com.cyitce.util.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * @author jianhongyu
 * @version 1.0
 * @date 2020/11/10 15:56
 * Redis访问工具类，Key默认为String类型，Value默认为Object，Value已开启JSON转换。
 */
@Component
public class RedisUtil {

    private static final Logger logger = Logger.getLogger(RedisUtil.class.getName());
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public RedisUtil(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        redisTemplate.setEnableTransactionSupport(true);
        logger.info("RedisTemplate已初始化.");
    }

    public RedisTemplate<String, Object> template() {
        return redisTemplate;
    }

    /**
     * 获取字符操作类
     *
     * @return ValueOperations<String, String>
     */
    public ValueOperations<String, Object> opsForValue() {
        return redisTemplate.opsForValue();
    }

    /**
     * 获取Hash操作类
     *
     * @return HashOperations<String, Object, Object>
     */
    public HashOperations<String, Object, Object> opsForHash() {
        return redisTemplate.opsForHash();
    }

    /**
     * 获取List操作类
     *
     * @return ListOperations<String, Object>
     */
    public ListOperations<String, Object> opsForList() {
        return redisTemplate.opsForList();
    }

    /**
     * 获取Set操作类
     *
     * @return SetOperations<String, Object>
     */
    public SetOperations<String, Object> opsForSet() {
        return redisTemplate.opsForSet();
    }

    /**
     * 获取Geo地理操作类
     *
     * @return GeoOperations<String, Object>
     */
    public GeoOperations<String, Object> opsForGeo() {
        return redisTemplate.opsForGeo();
    }

    /**
     * 获取HyperLogLog操作类
     *
     * @return HyperLogLogOperations<String, Object>
     */
    public HyperLogLogOperations<String, Object> opsForHyperLogLog() {
        return redisTemplate.opsForHyperLogLog();
    }

    /**
     * 获取ZSet操作类
     *
     * @return ZSetOperations<String, Object>
     */
    public ZSetOperations<String, Object> opsForZSet() {
        return redisTemplate.opsForZSet();
    }

    /**
     * 开启事务
     */
    public void multi() {
        redisTemplate.multi();
    }


    /**
     * 执行事务
     */
    public List<Object> exec() {
        return redisTemplate.exec();
    }

    /**
     * 取消事务
     */
    public void discard() {
        redisTemplate.discard();
    }

    /**
     * 用Key直接获取Value
     *
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 设置过期时间
     */
    public Boolean expire(String key, long time, TimeUnit timeUnit) {
        return redisTemplate.expire(key, time, timeUnit);
    }

    public void set(String key, Object value) {
        opsForValue().set(key, value);
    }

    public void set(String key, Object value, long time, TimeUnit timeUnit) {
        opsForValue().set(key, value, time, timeUnit);
    }

}
