package com.cyitce.util.redis.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author jianhongyu
 * @version 1.1
 * @date 2020/11/19 20:02
 * @see ResultCacheImpl
 * 该注解可以作用于任意被Spring容器管理的Bean类中的任意具有返回值（该返回值为基本数据类型及包装类、String、简单Bean类等）的方法上，给予该方法缓存的能力
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ResultCache {

    @AliasFor("key")
    String value() default "";

    /**
     * Redis缓存的Key，默认为：     类路径.方法名:参数1:参数2:...
     *
     * @return String
     */
    @AliasFor("value")
    String key() default "";

    /**
     * 方法的参数索引，将会根据参数在key后面进行拼接
     *
     * @return int[]
     */
    int[] params() default {};

    /**
     * 过期时间ms,默认30s
     *
     * @return long
     */
    long expire() default 30000;

    /**
     * 过期时间随机添加一定时间ms，用于防止缓存雪崩
     *
     * @return long[] 二维数组，long[0] < long[1]
     */
    long[] expireRandomAppend() default {0, 0};

    /**
     * 当存在缓存时，将进行回调，可对缓存值进行操作，回调函数为Object fun(Object o)
     *
     * @return 回调函数名 如 fun
     */
    String callbackMethod() default "";

    /**
     * 当未找到缓存，且即将保存的缓存value为null时，将null替换为指定值
     *
     * @return String
     */
    String nullSave() default "";

    /**
     * 是否开启分布式锁，当缓存不存在并保存缓存时，开启同步锁。
     *
     * @return boolean
     */
    boolean syncLock() default true;

    /**
     * 同步锁最大持有时间，当持有锁超过一定时间后自动释放。单位ms
     *
     * @return long，默认3分钟。
     */
    long maxLockTime() default 1800000;
}
