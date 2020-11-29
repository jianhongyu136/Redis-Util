package com.cyitce.util.redis;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.logging.Logger;

/**
 * @author jianhongyu
 * @version 1.0
 * @date 2020/11/11 15:56
 */
@SpringBootTest
public class RedisUtilTest {

    private final Logger logger = Logger.getLogger(RedisUtilTest.class.getName());

    @Autowired
    ResultCacheMethod resultCacheTest;

    @Test
    public void getTest() throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                logger.info(Thread.currentThread().getName() + ":test: " + resultCacheTest.doTest());
            }).start();
        }
        Thread.sleep(5000);
    }


}
