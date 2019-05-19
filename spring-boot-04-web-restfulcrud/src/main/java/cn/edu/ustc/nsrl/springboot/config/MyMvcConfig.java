package cn.edu.ustc.nsrl.springboot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// 使用WebMvcConfigurer可以扩展SpringMVC的功能
// 不要全面接管SpringMVC
//@EnableWebMvc
@Configuration
public class MyMvcConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 浏览器发送/chenxin请求也是来到success
        registry.addViewController("/chenxin").setViewName("success");
    }
}
