# SpringBoot01HelloWorld
## 安装IntelliJ后配置Maven
1. Preferences->Build->Build Tools->Maven->修改User settings file后点击OK
2. Preferences->Build->Build Tools->Maven->修改Maven home directory
## 创建Maven工程
Create New Project->Maven->如果没有绑定JDK环境，则需要绑定->Next->设置好Maven的gav->Next->设置好路径->Finish->这个时候右下角会出现提示“Maven projects need to be imported”，选择Enable Auto-Import __（或者可以在Preferences->Build->Build Tools->Maven->Importing->勾选Import Maven projects automatically）__
## 导入依赖SpringBoot相关的依赖
编辑pom.xml文件输入如下内容
```
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.1.4.RELEASE</version>
    <relativePath/> <!-- lookup parent from repository -->
</parent>
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
</dependencies>
```
## 编写个主程序
```
package cn.edu.ustc.nsrl.maven;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @SpringBootApplication 来标注一个主程序类，说明这是一个Spring Boot应用
 * */
@SpringBootApplication
public class HelloWorldMainApplication {
    public static void main(String[] args) {
        // Spring应用启动起来
        SpringApplication.run(HelloWorldMainApplication.class, args);
    }
}
```
## 编写相应的Controller
```
package cn.edu.ustc.nsrl.maven.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @RequestMapping("/hello") 127.0.0.1:8080/hello
 * */
@Controller
public class HelloController {
    @ResponseBody
    @RequestMapping("/hello")
    public String hello() {
        return "Hello World";
    }
}
```
## 测试运行主程序
访问 127.0.0.1:8080/hello
## 修改pom.xml使Maven生成可执行的jar包
```
<!--  这个插件，可以将应用打包成一个可执行的jar，否则的话不能通过java -jar执行jar包文件  -->
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```
## pom.xml文件的深度理解
### 父项目
这个工程的父项目信息如下
```
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.1.4.RELEASE</version>
    <relativePath/> <!-- lookup parent from repository -->
</parent>
```
这个父项目spring-boot-starter-parent的父项目信息如下
```
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-dependencies</artifactId>
    <version>2.1.4.RELEASE</version>
    <relativePath>../../spring-boot-dependencies</relativePath>
</parent>
```
这个祖父项目是SpringBoot的版本仲裁中心，它的 _properties_ 标签定义了各个模块的版本并且会自动导入对应的版本，不需要我们自己手动导入依赖。__（注意：如果涉及到没有定义的依赖则还是需要手动声明版本号）__
### 启动器
```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```
spring-boot-starter是SpringBoot场景启动器，这里我们要进行web开发所以帮我们导入web模块正常运行所依赖的组件

SpringBoot将所有的功能场景都抽取出来，做成一个个的starter（启动器），只需要在项目里面引入这些starter相关场景，则其的所有依赖都会导入进来。要用到什么功能就导入什么场景的启动器
## 主程序类和主入口类的深度理解
```
@SpringBootApplication
public class HelloWorldMainApplication {
    public static void main(String[] args) {
        // Spring应用启动起来
        SpringApplication.run(HelloWorldMainApplication.class, args);
    }
}
```
@SpringBootApplication：SpringBoot应用标注在某个类上说明这个类是SpringBoot的主配置类，SpringBoot就应该运行这个类的main方法来启动SpringBoot应用。进一步深入这个注解我们可以看到
```
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(
    excludeFilters = {@Filter(
    type = FilterType.CUSTOM,
    classes = {TypeExcludeFilter.class}
), @Filter(
    type = FilterType.CUSTOM,
    classes = {AutoConfigurationExcludeFilter.class}
)}
)
public @interface SpringBootApplication {
```
- @SpringBootConfiguration：标注在某个类上说明这个类是SpringBoot的配置类，类似于Spring中的@Configuration。__（配置类也是容器中的一个组件）__
- @EnableAutoConfiguration：告诉SpringBoot开启自动配置功能，免去了我们手动编写配置注入功能组件等的工作
    + @AutoConfigurationPackage：自动配置包
        * @Import({Registrar.class})：Spring的底层注解@Import，给容器中导入一个组件，导入的组件由Registrar.class决定也就是将主配置类 __（@SpringBootApplication标注的类）__ 的所在包及下面所有子包里面的所有组件扫描到Spring容器中
    + @Import({AutoConfigurationImportSelector.class})：决定导入哪些组件的选择器，会将所有需要导入的组件的全类名以数组的方式返回，从而使得这些组件被添加到容器中，会给容器中导入非常多的自动配置类（xxxAutoConfiguration），这些自动配置类给容器中导入这个场景需要的所有组件并配置好这些组件
    + AutoConfigurationImportSelector的原理：发现这个类里有如下代码
        ```
        List<String> configurations = this.getCandidateConfigurations(annotationMetadata, attributes);
        ```
        继续深入发现getCandidateConfigurations中有这一行代码
        ```
        List<String> configurations = SpringFactoriesLoader.loadFactoryNames(this.getSpringFactoriesLoaderFactoryClass(), this.getBeanClassLoader());
        ```
        其中第一个参数是
        ```
        protected Class<?> getSpringFactoriesLoaderFactoryClass() {
            return EnableAutoConfiguration.class;
        }
        ```
        第二个参数是对应的类加载器，由此我们发现loadFactoryNames函数中会将第一个参数的类路径下的 _META-INF/spring.factories_ 中获取EnableAutoConfiguration指定的值，然后SpringBoot将这些值作为自动配置类导入到容器中，由此开始自动配置类就生效了，完成了自动配置的工作
    + J2EE的整体整合解决方案和自动配置都在spring-boot-autoconfigure-2.1.4.RELEASE.jar