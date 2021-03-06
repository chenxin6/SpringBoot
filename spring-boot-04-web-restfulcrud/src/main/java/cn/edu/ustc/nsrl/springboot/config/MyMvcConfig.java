package cn.edu.ustc.nsrl.springboot.config;

import cn.edu.ustc.nsrl.springboot.component.MyLocaleResolver;
import org.apache.tomcat.jni.Local;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
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
        // 浏览器发送/请求也是来到login
        registry.addViewController("/").setViewName("login");
        // 浏览器发送/index.html请求也是来到login
        registry.addViewController("/index.html").setViewName("login");

    }
    @Bean
    public LocaleResolver localeResolver() {
        return new MyLocaleResolver();
    }
}
