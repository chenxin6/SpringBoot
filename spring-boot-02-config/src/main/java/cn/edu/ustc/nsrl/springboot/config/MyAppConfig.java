package cn.edu.ustc.nsrl.springboot.config;

import cn.edu.ustc.nsrl.springboot.service.HelloService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Configuration：指明当前类是一个配置类，就是用来替代之前的Spring文件
 * */
@Configuration
public class MyAppConfig {
    // 这个注解的作用就是将方法的返回值添加到容器中，容器中这个组件默认的id就是方法名
    @Bean
    public HelloService helloService02() {
        System.out.println("配置类@Bean给容器中添加组件类");
        return new HelloService();
    }
}
