package cn.edu.ustc.nsrl.springboot;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringBoot03LoggingApplicationTests {

    // 记录器
    Logger logger = LoggerFactory.getLogger(getClass());
    @Test
    public void contextLoads() {
        // 日志的级别自上而下逐渐变高
        // 可以调整输出的日志级别：日志就只会在这个级别以后的高级别生效，默认情况下我们使用的是info级别
        logger.trace("这是trace日志");
        logger.debug("这是debug日志");
        // 默认情况下只会打印出以下这三个：这是因为如果没有指定级别的就会使用SpringBoot默认规定的级别（root级别），在默认情况下root级别就是info
        logger.info("这是info日志");
        logger.warn("这是warn日志");
        logger.error("这是error日志");
    }

}
