package com.cyitce.util.redis;

import com.cyitce.util.redis.annotation.ResultCache;
import com.cyitce.util.redis.beans.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

/**
 * @author jianhongyu
 * @version 1.0
 * @date 2020/11/20 13:37
 */
@Component
public class ResultCacheTest {
    Logger logger = Logger.getLogger(ResultCacheTest.class.getName());

    @Autowired
    RedisUtil redisUtil;

    @ResultCache(callbackMethod = "callback")
    public Object doTest() {
        return redisUtil.get("test");
    }

    public Object callback(Object o) {
        return o;
    }
}
