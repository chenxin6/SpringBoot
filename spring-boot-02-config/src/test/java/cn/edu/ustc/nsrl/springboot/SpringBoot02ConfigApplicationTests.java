package cn.edu.ustc.nsrl.springboot;

import cn.edu.ustc.nsrl.springboot.bean.Person;
import cn.edu.ustc.nsrl.springboot.service.HelloService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * SpringBoot单元测试
 * 可以在测试期间很方便的类似编码一样进行自动注入等容器的功能
 * */

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringBoot02ConfigApplicationTests {

    @Autowired
    Person person;

    @Autowired
    ApplicationContext ioc;

    @Test
    public void testHelloService() {
//        boolean flag = ioc.containsBean("helloService");
        boolean flag = ioc.containsBean("helloService02");
        System.out.println(flag);
    }

    @Test
    public void contextLoads() {
        System.out.println(person);
    }

}
