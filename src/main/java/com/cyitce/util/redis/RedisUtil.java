package com.cyitce.util.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * @author jianhongyu
 * @version 1.2
 * @date 2020/11/10 15:56
 * @see com.cyitce.util.redis.configs.RedisConfig
 * RedisTemplate已配置,Redis访问工具类，Key默认为String类型，Value默认为Object，Value已开启JSON转换。
 */
@Component
public class RedisUtil {


    public static final String LOCK = ":lock";
    private static final Logger logger = Logger.getLogger(RedisUtil.class.getName());
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public RedisUtil(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        logger.info("redisTemplate init.");
    }

    /**
     * 获取RedisTemplate
     *
     * @return RedisTemplate<String, Object>
     */
    public RedisTemplate<String, Object> redisTemplate() {
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

    ////////////////////////////////////事务/////////////////////////////////////

    /**
     * 获取ZSet操作类
     *
     * @return ZSetOperations<String, Object>
     */
    public ZSetOperations<String, Object> opsForZset() {
        return redisTemplate.opsForZSet();
    }

    /**
     * 开启事务
     */
    public void multi() {
        redisTemplate.setEnableTransactionSupport(true);
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

    ////////////////////////////////通常////////////////////////////////////////

    /**
     * 设置过期时间
     */
    public Boolean expire(String key, long time, TimeUnit timeUnit) {
        return redisTemplate.expire(key, time, timeUnit);
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
     * 截取字符串
     *
     * @param key   键
     * @param start 开始索引
     * @param end   结束索引
     * @return 截取后的字符串
     */
    public String getRange(String key, int start, int end) {
        return redisTemplate.opsForValue().get(key, start, end);
    }

    /**
     * 设置kv
     *
     * @param key   键
     * @param value 值
     */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 设置kv
     *
     * @param key      键
     * @param value    值
     * @param time     过期时间
     * @param timeUnit 过期时间单位
     */
    public void set(String key, Object value, long time, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, time, timeUnit);
    }

    /**
     * 删除键
     *
     * @param key 键
     * @return 是否成功
     */
    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    /**
     * 删除多个键
     *
     * @param keys 键数组
     * @return Long
     */
    public Long delete(String... keys) {
        return redisTemplate.delete(Arrays.asList(keys));
    }

    /**
     * 删除多个键
     *
     * @param keys 键集合
     * @return Long
     */
    public Long delete(Collection<String> keys) {
        return redisTemplate.delete(keys);
    }

    /**
     * 指定位置替换
     *
     * @param key    键
     * @param offset 替换开始位置
     * @param value  替换的值
     */
    public void setRange(String key, int offset, String value) {
        redisTemplate.opsForValue().set(key, value, offset);
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return 是否存在
     */
    public Boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 向Value后追加字符
     *
     * @param key   键
     * @param value 值
     * @return 追加后字符长度
     */
    public Integer append(String key, String value) {
        return redisTemplate.opsForValue().append(key, value);
    }

    /**
     * 获取Value长度
     *
     * @param key 键
     * @return 长度
     */
    public Long strLen(String key) {
        return redisTemplate.opsForValue().size(key);
    }

    /**
     * 自增
     *
     * @param key 键
     * @return 自增后的值
     */
    public Long incr(String key) {
        return redisTemplate.opsForValue().increment(key);
    }

    /**
     * 自减
     *
     * @param key 键
     * @return 自减后的值
     */
    public Long decr(String key) {
        return redisTemplate.opsForValue().decrement(key);
    }

    /**
     * 步长自增
     *
     * @param key   值
     * @param delta 步长
     * @return 自增后的值
     */
    public Long incrBy(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 步长自减
     *
     * @param key   键
     * @param delta 值
     * @return 自减后的值
     */
    public Long decrBy(String key, long delta) {
        return redisTemplate.opsForValue().decrement(key, delta);
    }

    /**
     * 如果键不存在就设置，否则不设置
     *
     * @param key   键
     * @param value 值
     * @return 是否成功
     */
    public Boolean setnx(String key, Object value) {
        return redisTemplate.opsForValue().setIfAbsent(key, value);
    }

    /**
     * 如果键不存在就设置，否则不设置
     *
     * @param key     键
     * @param value   值
     * @param timeout 过期时间
     * @param unit    过期时间单位
     * @return 是否成功
     */
    public Boolean setnx(String key, Object value, long timeout, TimeUnit unit) {
        return redisTemplate.opsForValue().setIfAbsent(key, value, timeout, unit);
    }

    /**
     * 获取过期时间
     *
     * @param key 键
     * @return 过期时间
     */
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key);
    }

    /**
     * 获取过期时间
     *
     * @param key  键
     * @param unit 时间单位
     * @return 过期时间
     */
    public Long getExpire(String key, TimeUnit unit) {
        return redisTemplate.getExpire(key, unit);
    }

    /**
     * 批量获取Value
     *
     * @param key 键数组
     * @return 值集合
     */
    public List<Object> mget(String... key) {
        return redisTemplate.opsForValue().multiGet(Arrays.asList(key));
    }

    /**
     * 批量获取Value
     *
     * @param key 键集合
     * @return 值集合
     */
    public List<Object> mget(Collection<String> key) {
        return redisTemplate.opsForValue().multiGet(key);
    }

    /**
     * 获取key
     *
     * @param pattern 匹配字符
     * @return 键集合
     */
    public Set<String> keys(String pattern) {
        return redisTemplate.keys(pattern);
    }

    /////////////////////////////////////Lock////////////////////////////////////

    /**
     * 非阻塞加锁，默认最大持有锁时间3分钟
     *
     * @param lockId 锁ID
     * @return 是否成功
     */
    public Boolean lock(String lockId) {
        return lock(lockId, 3, TimeUnit.MINUTES);
    }

    /**
     * 非阻塞加锁
     *
     * @param lockId      锁ID
     * @param maxLockTime 最大持有锁时间
     * @param timeUnit    时间单位
     * @return 是否成功
     */
    public Boolean lock(String lockId, long maxLockTime, TimeUnit timeUnit) {
        return redisTemplate.execute((RedisCallback<Boolean>) connection -> {
            Boolean b = connection.hashCommands().hSetNX((lockId + LOCK).getBytes(), "threadID".getBytes(), String.valueOf(Thread.currentThread().getId()).getBytes());
            if (b != null && b) {
                connection.expire((lockId + LOCK).getBytes(), timeUnit.toSeconds(maxLockTime));
                connection.hashCommands().hSet((lockId + LOCK).getBytes(), "enterCount".getBytes(), "1".getBytes());
            } else if (String.valueOf(Thread.currentThread().getId()).equals(new String(connection.hashCommands().hGet((lockId + LOCK).getBytes(), "threadID".getBytes())))) {
                connection.expire((lockId + LOCK).getBytes(), timeUnit.toSeconds(maxLockTime));
                connection.hashCommands().hIncrBy((lockId + LOCK).getBytes(), "enterCount".getBytes(), 1);
                b = true;
            }
            return b;
        });
    }

    /**
     * 阻塞加锁，默认最大持有锁时间3分钟
     *
     * @param lockId      锁ID
     * @param waitMaxTime 等待超时时间
     * @return 是否成功
     */
    public Boolean lockBlock(String lockId, long waitMaxTime, TimeUnit timeUnit) {
        return lockBlock(lockId, waitMaxTime, 3, timeUnit);
    }

    /**
     * 阻塞加锁
     *
     * @param lockId      锁ID
     * @param waitMaxTime 等待超时时间
     * @param maxLockTime 最大持有锁时间
     * @param timeUnit    时间单位
     * @return 是否成功
     */
    public Boolean lockBlock(String lockId, long waitMaxTime, long maxLockTime, TimeUnit timeUnit) {
        long startTime = System.currentTimeMillis();
        long maxTimeMillis = timeUnit.toMillis(waitMaxTime);
        while (true) {
            if (lock(lockId, maxLockTime, TimeUnit.MINUTES)) {
                return true;
            }
            try {
                Thread.sleep(5);
            } catch (InterruptedException ignored) {
            }
            if (System.currentTimeMillis() - startTime >= maxTimeMillis) {
                return false;
            }
        }
    }

    /**
     * 设置锁最大持有时间
     *
     * @param lockId      锁ID
     * @param maxLockTime 最大持有锁时间
     * @param timeUnit    时间单位
     * @return 是否成功
     */
    public Boolean resetMaxLockTime(String lockId, long maxLockTime, TimeUnit timeUnit) {
        /*if (String.valueOf(Thread.currentThread().getId()).equals(String.valueOf(redisTemplate.opsForHash().get(lockId + LOCK, "threadID")))) {
            return redisTemplate.expire(lockId + LOCK, maxLockTime, timeUnit);
        }
        return false;*/
        return redisTemplate.execute((RedisCallback<Boolean>) connection -> {
            if (String.valueOf(Thread.currentThread().getId()).equals(new String(connection.hashCommands().hGet((lockId + LOCK).getBytes(), "threadID".getBytes())))) {
                return connection.expire((lockId + LOCK).getBytes(), timeUnit.toSeconds(maxLockTime));
            }
            return false;
        });
    }


    /**
     * 释放锁
     *
     * @param lockId 锁ID
     * @return 是否成功
     */
    public Boolean unlock(String lockId) {
        /*if (String.valueOf(Thread.currentThread().getId()).equals(String.valueOf(redisTemplate.opsForHash().get(lockId + LOCK, "threadID")))) {
            if (redisTemplate.opsForHash().increment(lockId + LOCK, "enterCount", -1) <= 0) {
                return redisTemplate.delete(lockId + LOCK);
            }
            return true;
        }
        return false;*/
        return redisTemplate.execute((RedisCallback<Boolean>) connection -> {
            if (String.valueOf(Thread.currentThread().getId()).equals(new String(connection.hashCommands().hGet((lockId + LOCK).getBytes(), "threadID".getBytes())))) {
                if (connection.hashCommands().hIncrBy((lockId + LOCK).getBytes(), "enterCount".getBytes(), -1) <= 0) {
                    Long l = connection.del((lockId + LOCK).getBytes());
                    return l != null && l == 1;
                }
                return true;
            }
            return false;
        });
    }

    /////////////////////////////////////List////////////////////////////////////


    /**
     * List,将一个值插入到list头部
     *
     * @param key   键
     * @param value 值
     * @return 结果
     */
    public Long lPush(String key, Object value) {
        return redisTemplate.opsForList().leftPush(key, value);
    }

    /**
     * List,将一个值插入到指定值前
     *
     * @param key   键
     * @param pivot 指定值
     * @param value 值
     * @return 结果
     */
    public Long lPush(String key, Object pivot, Object value) {
        return redisTemplate.opsForList().leftPush(key, pivot, value);
    }

    /**
     * List,如果值不存在，则左插入一个值，否则失败
     *
     * @param key   键
     * @param value 值
     * @return 结果
     */
    public Long lPushNx(String key, Object value) {
        return redisTemplate.opsForList().leftPushIfPresent(key, value);
    }

    /**
     * List头部插入多条
     *
     * @param key   键
     * @param value 数组
     * @return 结果
     */
    public Long lPushAll(String key, Object... value) {
        return redisTemplate.opsForList().leftPushAll(key, value);
    }

    /**
     * List头部插入多条
     *
     * @param key   键
     * @param value 集合
     * @return 结果
     */
    public Long lPushAll(String key, Collection<Object> value) {
        return redisTemplate.opsForList().leftPushAll(key, value);
    }


    /**
     * List左边出栈
     *
     * @param key 键
     * @return 值
     */
    public Object lPop(String key) {
        return redisTemplate.opsForList().leftPop(key);
    }


    /**
     * List区间获取元素
     *
     * @param key   键
     * @param start 开始索引
     * @param end   结束索引
     * @return 值
     */
    public List<Object> lRange(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }


    /**
     * List右边添加值
     *
     * @param key   键
     * @param value 值
     * @return 结果
     */
    public Long rPush(String key, Object value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }

    /**
     * List，将一个值插入到指定值后
     *
     * @param key   键
     * @param pivot 指定值
     * @param value 值
     * @return 结果
     */
    public Long rPush(String key, Object pivot, Object value) {
        return redisTemplate.opsForList().rightPush(key, pivot, value);
    }

    /**
     * List,如果值不存在，则右插入一个值，否则失败
     *
     * @param key   键
     * @param value 值
     * @return 结果
     */
    public Long rPushNx(String key, Object value) {
        return redisTemplate.opsForList().rightPushIfPresent(key, value);
    }

    /**
     * List,尾部插入多条
     *
     * @param key   键
     * @param value 数组
     * @return 结果
     */
    public Long rPushAll(String key, Object... value) {
        return redisTemplate.opsForList().rightPushAll(key, value);
    }

    /**
     * List尾部插入多条
     *
     * @param key   键
     * @param value 集合
     * @return 结果
     */
    public Long rPushAll(String key, Collection<Object> value) {
        return redisTemplate.opsForList().rightPushAll(key, value);
    }


    /**
     * List右边出栈
     *
     * @param key 键
     * @return 值
     */
    public Object rPop(String key) {
        return redisTemplate.opsForList().rightPop(key);
    }

    /**
     * List,根据下标获取值
     *
     * @param key   键
     * @param index 索引
     * @return 值
     */
    public Object lIndex(String key, long index) {
        return redisTemplate.opsForList().index(key, index);
    }

    /**
     * List,获取List元素个数
     *
     * @param key 键
     * @return 个数
     */
    public Long lLen(String key) {
        return redisTemplate.opsForList().size(key);
    }


    /**
     * List，移除多个值
     *
     * @param key   键
     * @param count 个数
     * @param value 移除的值
     * @return 结果
     */
    public Long lRem(String key, long count, Object value) {
        return redisTemplate.opsForList().remove(key, count, value);
    }

    /**
     * List,根据索引设置值
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     */
    public void lSet(String key, long index, Object value) {
        redisTemplate.opsForList().set(key, index, value);
    }

    /**
     * List,通过下标截取指定的长度，List会被改变
     *
     * @param key   键
     * @param start 开始索引
     * @param end   结束索引
     */
    public void lTrim(String key, long start, long end) {
        redisTemplate.opsForList().trim(key, start, end);
    }

    /**
     * List，移除一个列表中的最后一个元素，添加到另外一个列表左边
     *
     * @param key   键
     * @param toKey 另外的键
     */
    public Object rPopLpush(String key, String toKey) {
        return redisTemplate.opsForList().rightPopAndLeftPush(key, toKey);
    }

    /////////////////////////////////////Set////////////////////////////////////

    /**
     * Set,添加元素
     *
     * @param key   键
     * @param value 值数组
     * @return 结果
     */
    public Long sAdd(String key, Object... value) {
        return redisTemplate.opsForSet().add(key, value);
    }

    /**
     * Set,获取所有元素
     *
     * @param key 键
     * @return 值Set
     */
    public Set<Object> sMembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * Set，元素是否存在
     *
     * @param key   键
     * @param value 值
     * @return 是否存在
     */
    public Boolean sIsMember(String key, Object value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    /**
     * Set，获取元素个数
     *
     * @param key 键
     * @return 个数
     */
    public Long sCard(String key) {
        return redisTemplate.opsForSet().size(key);
    }

    /**
     * Set，移除指定元素
     *
     * @param key   键
     * @param value 值数组
     * @return 结果
     */
    public Long sRem(String key, Object... value) {
        return redisTemplate.opsForSet().remove(key, value);
    }

    /**
     * Set，随机获取一个元素
     *
     * @param key 键
     * @return 值
     */
    public Object sRandomMember(String key) {
        return redisTemplate.opsForSet().randomMember(key);
    }

    /**
     * Set，随机获取指定个数元素
     *
     * @param key   键
     * @param count 个数
     * @return 值列表
     */
    public List<Object> sRandomMembers(String key, long count) {
        return redisTemplate.opsForSet().randomMembers(key, count);
    }


    /**
     * Set，随机移除一个元素并返回
     *
     * @param key 键
     * @return 值
     */
    public Object sPop(String key) {
        return redisTemplate.opsForSet().pop(key);
    }

    /**
     * Set，随机移除指定个数的元素并返回
     *
     * @param key   键
     * @param count 个数
     * @return 值列表
     */
    public List<Object> sPop(String key, long count) {
        return redisTemplate.opsForSet().pop(key, count);
    }


    /**
     * Set，移动一个元素到另外一个Set中
     *
     * @param key   键
     * @param value 值
     * @param toKey 移动到的键
     * @return 是否成功
     */
    public Boolean sMove(String key, Object value, String toKey) {
        return redisTemplate.opsForSet().move(key, value, toKey);
    }

    /**
     * Set，两个Set差集
     *
     * @param key      键
     * @param otherKey 另外的键
     * @return 差集
     */
    public Set<Object> sDiff(String key, String otherKey) {
        return redisTemplate.opsForSet().difference(key, otherKey);
    }

    /**
     * Set，两个Set的交集
     *
     * @param key      键
     * @param otherKey 另外的键
     * @return 交集
     */
    public Set<Object> sInter(String key, String otherKey) {
        return redisTemplate.opsForSet().intersect(key, otherKey);
    }

    /**
     * Set，两个Set的并集
     *
     * @param key      键
     * @param otherKey 另外的键
     * @return 并集
     */
    public Set<Object> sUnion(String key, String otherKey) {
        return redisTemplate.opsForSet().union(key, otherKey);
    }

    /////////////////////////////////////Hash////////////////////////////////////


    /**
     * Hash，添加hash
     *
     * @param key     键
     * @param hashKey hash键
     * @param value   值
     */
    public void hset(String key, String hashKey, Object value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    /**
     * Hash，获取hash
     *
     * @param key     键
     * @param hashKey hash键
     */
    public void hget(String key, String hashKey) {
        redisTemplate.opsForHash().get(key, hashKey);
    }

    /**
     * Hash，批量添加hash
     *
     * @param key 键
     * @param map map集合
     */
    public void hset(String key, Map<String, Object> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    /**
     * Hash，批量获取hash
     *
     * @param key      键
     * @param hashKeys hash键集合
     */
    public List<Object> hget(String key, Collection<Object> hashKeys) {
        return redisTemplate.opsForHash().multiGet(key, hashKeys);
    }

    /**
     * Hash，获取全部value
     *
     * @param key 键
     * @return 值列表
     */
    public List<Object> hgetAll(String key) {
        return redisTemplate.opsForHash().values(key);
    }

    /**
     * Hash，删除Hash键
     *
     * @param key      键
     * @param hashKeys Hash键数组
     * @return 结果
     */
    public Long hdel(String key, Object... hashKeys) {
        return redisTemplate.opsForHash().delete(key, hashKeys);
    }

    /**
     * Hash，获取Hash键个数
     *
     * @param key 键
     * @return Hash键个数
     */
    public Long hlen(String key) {
        return redisTemplate.opsForHash().size(key);
    }

    /**
     * Hash，键是否存在
     *
     * @param key     键
     * @param hashKey Hash键
     * @return 是否存在
     */
    public Boolean hExist(String key, Object hashKey) {
        return redisTemplate.opsForHash().hasKey(key, hashKey);
    }

    /**
     * Hash，获取所有Hash键
     *
     * @param key 键
     * @return Hash键Set
     */
    public Set<Object> hKeys(String key) {
        return redisTemplate.opsForHash().keys(key);
    }

    /**
     * Hash，增加
     *
     * @param key     键
     * @param hashKey hash键
     * @param delta   增加的值
     * @return 结果
     */
    public Long hIncrBy(String key, Object hashKey, long delta) {
        return redisTemplate.opsForHash().increment(key, hashKey, delta);
    }

    /**
     * Hash，增加
     *
     * @param key     键
     * @param hashKey hash键
     * @param delta   增加的值
     * @return 结果
     */
    public Double hIncrBy(String key, Object hashKey, double delta) {
        return redisTemplate.opsForHash().increment(key, hashKey, delta);
    }

    /**
     * Hash，如果不存在则添加，否则失败
     *
     * @param key     键
     * @param hashKey hash键
     * @param value   值
     * @return 是否成功
     */
    public Boolean hSetNx(String key, Object hashKey, Object value) {
        return redisTemplate.opsForHash().putIfAbsent(key, hashKey, value);
    }

    /////////////////////////////////////ZSet////////////////////////////////////

    /**
     * ZSet，添加
     *
     * @param key   键
     * @param value 值
     * @param score 分数
     * @return 是否成功
     */
    public Boolean zAdd(String key, Object value, double score) {
        return redisTemplate.opsForZSet().add(key, value, score);
    }


    /**
     * ZSet，根据分数区间获取元素个数
     *
     * @param key 键
     * @param min 最小分数
     * @param max 最大分数
     * @return 个数
     */
    public Long zCount(String key, double min, double max) {
        return redisTemplate.opsForZSet().count(key, min, max);
    }

    /**
     * ZSet，根据分数排序并获取
     *
     * @param key 键
     * @param min 最小分数
     * @param max 最大分数
     * @return 值集合
     */
    public Set<Object> zRangeByScore(String key, double min, double max) {
        return redisTemplate.opsForZSet().rangeByScore(key, min, max);
    }

    /**
     * ZSet，根据分数排序并获取
     *
     * @param key    键
     * @param min    最小分数
     * @param max    最大分数
     * @param offset 偏移
     * @param count  个数
     * @return 值集合
     */
    public Set<Object> zRangeByScore(String key, double min, double max, long offset, long count) {
        return redisTemplate.opsForZSet().rangeByScore(key, min, max, offset, count);
    }

    /**
     * ZSet，排序并带分数获取
     *
     * @param key   键
     * @param start 开始
     * @param end   结束
     * @return 结果
     */
    public Set<ZSetOperations.TypedTuple<Object>> zRangeWithScores(String key, long start, long end) {
        return redisTemplate.opsForZSet().rangeWithScores(key, start, end);
    }

    /**
     * ZSet，直接获取元素
     *
     * @param key   键
     * @param start 开始索引
     * @param end   结束索引
     * @return 结果集
     */
    public Set<Object> zRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().range(key, start, end);
    }

    /**
     * ZSet，移除元素
     *
     * @param key    键
     * @param values 值数组
     * @return 结果
     */
    public Long zRem(String key, Object... values) {
        return redisTemplate.opsForZSet().remove(key, values);
    }

    /**
     * ZSet，索引区间移除元素
     *
     * @param key   键
     * @param start 开始索引
     * @param end   结束索引
     * @return 结果
     */
    public Long zRemRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().removeRange(key, start, end);
    }

    /**
     * ZSet，根据分数区间移除元素
     *
     * @param key 键
     * @param min 最小分数
     * @param max 最大分数
     * @return 结果
     */
    public Long zRemRangeByScore(String key, double min, double max) {
        return redisTemplate.opsForZSet().removeRangeByScore(key, min, max);
    }

    /**
     * ZSet,获取元素的个数
     *
     * @param key 键
     * @return 个数
     */
    public Long zCard(String key) {
        return redisTemplate.opsForZSet().zCard(key);
    }

    /**
     * ZSet，根据分数倒序并获取
     *
     * @param key 键
     * @param min 最小分数
     * @param max 最大分数
     * @return 值集合
     */
    public Set<Object> zReverseRangeByScore(String key, double min, double max) {
        return redisTemplate.opsForZSet().reverseRangeByScore(key, min, max);
    }

    /**
     * ZSet，根据分数倒序并获取
     *
     * @param key    键
     * @param min    最小分数
     * @param max    最大分数
     * @param offset 偏移
     * @param count  个数
     * @return 值集合
     */
    public Set<Object> zReverseRangeByScore(String key, double min, double max, long offset, long count) {
        return redisTemplate.opsForZSet().reverseRangeByScore(key, min, max, offset, count);
    }

    /**
     * ZSet，分数从高到低获取元素
     *
     * @param key   键
     * @param start 开始索引
     * @param end   结束索引
     * @return 结果集
     */
    public Set<Object> zReverseRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().reverseRange(key, start, end);
    }

    /**
     * ZSet, 返回指定成员区间内的成员，按字典正序排列, 分数必须相同。
     *
     * @param key   键
     * @param range range
     * @return 结果集
     */
    public Set<Object> zRangeByLex(String key, RedisZSetCommands.Range range) {
        return redisTemplate.opsForZSet().rangeByLex(key, range);
    }

    /**
     * ZSet, 返回指定成员区间内的成员，按字典正序排列, 分数必须相同。
     *
     * @param key   键
     * @param range range
     * @param limit limit
     * @return 结果集
     */
    public Set<Object> zRangeByLex(String key, RedisZSetCommands.Range range, RedisZSetCommands.Limit limit) {
        return redisTemplate.opsForZSet().rangeByLex(key, range, limit);
    }

    /////////////////////////////////////GEO////////////////////////////////////


    /**
     * GEO,添加一个或多个地理空间位置到sorted set
     *
     * @param key    键
     * @param point  点
     * @param member 值
     * @return 结果
     */
    public Long geoAdd(String key, Point point, Object member) {
        return redisTemplate.opsForGeo().add(key, point, member);
    }

    /**
     * GEO,添加一个或多个地理空间位置到sorted set
     *
     * @param key      键
     * @param location 位置
     * @return 结果
     */
    public Long geoAdd(String key, RedisGeoCommands.GeoLocation<Object> location) {
        return redisTemplate.opsForGeo().add(key, location);
    }

    /**
     * GEO,添加一个或多个地理空间位置到sorted set
     *
     * @param key       键
     * @param locations 位置集
     * @return 结果
     */
    public Long geoAdd(String key, Iterable<RedisGeoCommands.GeoLocation<Object>> locations) {
        return redisTemplate.opsForGeo().add(key, locations);
    }


    /**
     * GEO，移除元素
     *
     * @param key     键
     * @param members 成员数组
     * @return 结果
     */
    public Long geoRem(String key, Object... members) {
        return redisTemplate.opsForGeo().remove(key, members);
    }

    /**
     * GEO,返回一个标准的地理空间的Geohash字符串
     *
     * @param key     键
     * @param members 成员数组
     * @return 结果
     */
    public List<String> geoHash(String key, Object... members) {
        return redisTemplate.opsForGeo().hash(key, members);
    }


    /**
     * GEO，返回地理空间的经纬度
     *
     * @param key     键
     * @param members 成员数组
     * @return 结果
     */
    public List<Point> geoPos(String key, Object... members) {
        return redisTemplate.opsForGeo().position(key, members);
    }

    /**
     * GEO，返回两个地理空间之间的距离
     *
     * @param key     键
     * @param member1 成员1
     * @param member2 成员2
     * @return 距离
     */
    public Distance geoDist(String key, Object member1, Object member2) {
        return redisTemplate.opsForGeo().distance(key, member1, member2);
    }


    /**
     * GEO，查询指定半径内所有的地理空间元素的集合。
     *
     * @param key    键
     * @param member 成员
     * @param radius 半径
     * @return 结果集
     */
    public GeoResults<RedisGeoCommands.GeoLocation<Object>> geoRadius(String key, Object member, double radius) {
        return redisTemplate.opsForGeo().radius(key, member, radius);
    }

    /**
     * GEO，查询指定半径内所有的地理空间元素的集合。
     *
     * @param key      键
     * @param member   成员
     * @param distance 距离
     * @return 结果集
     */
    public GeoResults<RedisGeoCommands.GeoLocation<Object>> geoRadius(String key, Object member, Distance distance) {
        return redisTemplate.opsForGeo().radius(key, member, distance);
    }

    /////////////////////////////////////HyperLogLog////////////////////////////////////

    /**
     * HyperLogLog，将指定元素添加到HyperLogLog
     *
     * @param key    键
     * @param values 值数组
     * @return 结果
     */
    public Long pfAdd(String key, Object... values) {
        return redisTemplate.opsForHyperLogLog().add(key, values);
    }

    /**
     * HyperLogLog，Return the approximated cardinality of the set(s) observed by the HyperLogLog at key(s).
     *
     * @param keys 键数组
     * @return 结果
     */
    public Long pfCount(String... keys) {
        return redisTemplate.opsForHyperLogLog().size(keys);
    }

    /**
     * HyperLogLog，删除
     *
     * @param key 键
     */
    public void pfDelete(String key) {
        redisTemplate.opsForHyperLogLog().delete(key);
    }

    /**
     * HyperLogLog,Merge N different HyperLogLogs into a single one.
     *
     * @param destination key of HyperLogLog to move source keys into.
     * @param sourceKeys  must not be {@literal null} or {@literal empty}.
     * @return 结果
     */
    public Long pfDelete(String destination, String... sourceKeys) {
        return redisTemplate.opsForHyperLogLog().union(destination, sourceKeys);
    }

    /////////////////////////////////////Bitmap////////////////////////////////////

    /**
     * Bitmap,返回位的值存储在关键的字符串值的偏移量。
     *
     * @param key    键
     * @param offset 偏移
     * @return 结果
     */
    public Boolean getBit(String key, long offset) {
        return redisTemplate.opsForValue().getBit(key, offset);
    }

    /**
     * Bitmap,设置存储在关键的字符串值的偏移量。
     *
     * @param key    键
     * @param offset 偏移
     * @param value  值
     * @return 结果
     */
    public Boolean setBit(String key, long offset, boolean value) {
        return redisTemplate.opsForValue().setBit(key, offset, value);
    }

}
