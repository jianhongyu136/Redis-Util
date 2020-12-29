package com.cyitce.util.redis;

import com.cyitce.util.redis.annotation.ResultCache;
import com.cyitce.util.redis.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

/**
 * @author jianhongyu
 * @version 1.0
 * @date 2020/11/20 13:37
 */
@Component
public class ResultCacheMethod {
    Logger logger = Logger.getLogger(ResultCacheMethod.class.getName());

    @Autowired
    RedisUtil redisUtil;

    @ResultCache(callbackMethod = "callback")
    public User doTest() {
//        return redisUtil.get("test");
        return new User("jhy", "n");
    }


    public Object callback(Object o) {
        logger.info("run callback");
        return o;
    }
}
