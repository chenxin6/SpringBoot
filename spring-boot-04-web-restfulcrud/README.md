# spring-boot-04-web-restfulcrud
## Web开发
1. 创建SpringBoot应用，选中我们需要的模块（Web、MyBatis、MongoDB等）
2. SpringBoot已经默认将这些场景配置好了，只需要在配置文件中指定少量配置就可以运行起来（详见自动配置的章节）
3. 自己编写业务代码
## SpringBoot对静态资源的映射实现
查看spring-boot-autoconfigure中的WebMvcAutoConfiguration的文件内容
```
public void addResourceHandlers(ResourceHandlerRegistry registry) {
    if (!this.resourceProperties.isAddMappings()) {
        logger.debug("Default resource handling disabled");
    } else {
        Duration cachePeriod = this.resourceProperties.getCache().getPeriod();
        CacheControl cacheControl = this.resourceProperties.getCache().getCachecontrol().toHttpCacheControl();
        if (!registry.hasMappingForPattern("/webjars/**")) {
            this.customizeResourceHandlerRegistration(registry.addResourceHandler(new String[]{"/webjars/**"}).addResourceLocations(new String[]{"classpath:/META-INF/resources/webjars/"}).setCachePeriod(this.getSeconds(cachePeriod)).setCacheControl(cacheControl));
        }
        String staticPathPattern = this.mvcProperties.getStaticPathPattern();
        if (!registry.hasMappingForPattern(staticPathPattern)) {
            this.customizeResourceHandlerRegistration(registry.addResourceHandler(new String[]{staticPathPattern}).addResourceLocations(getResourceLocations(this.resourceProperties.getStaticLocations())).setCachePeriod(this.getSeconds(cachePeriod)).setCacheControl(cacheControl));
        }
    }
}
```
1. 所有/webjars/**都去classpath:/META-INF/resources/webjars/找资源
    - webjars：以jar包的方式引入静态资源
    - 可以到这个网站寻找资源http://www.webjars.org/
    - 以jquery为例，导包后浏览器输入http://127.0.0.1:8080/webjars/jquery/3.3.1/jquery.js
    ```
    <!-- 引入jquery-webjar -->
    <dependency>
        <groupId>org.webjars</groupId>
        <artifactId>jquery</artifactId>
        <version>3.3.1</version>
    </dependency>
    ```
2. /**访问当前项目的任何资源（默认情况下静态资源文件夹是以下四个），访问顺序至上而下直到找到为止
```
"classpath:/META-INF/resources/"
"classpath:/resources/"
"classpath:/static/"
"classpath:/public/"
"/":当前项目的根路径也就是那个src/main/resources/
```
- http://127.0.0.1:8080/abc意味着去上面的静态资源文件夹中找abc
- 页面的欢迎页即http://127.0.0.1:8080返回的是静态资源文件夹下所有的index.html __（注意访问顺序）__
- 每次这些静态资源文件夹中的文件发生改变时建议mvn clean下
- 所有的favicon.ico都是在静态资源文件夹下找
## 模版引擎
SpringBoot推荐的默认模版引擎是Thymeleaf，它具有语法简单，功能强大的特点
### 引入Thymeleaf
引入新的包的时候Maven本地仓库里面可能没有，虽然IntelliJ可以自动下载，但是有时候会失效，所以如果包没有下载下来的话建议使用Maven命令mvn verify
```
<!--thymeleaf 3的导入-->
<thymeleaf.version>3.0.11.RELEASE</thymeleaf.version>
<!--布局功能支持 同时支持thymeleaf3主程序 layout2.0以上版本  -->
<!--布局功能支持 同时支持thymeleaf2主程序 layout1.0以上版本  -->
<thymeleaf-layout-dialect.version>2.2.2</thymeleaf-layout-dialect.version>
```
```
<!-- 引入thymeleaf -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
```
### Thymeleaf使用&语法
查看文件ThymeleafProperties得知以下内容：
```
public class ThymeleafProperties {
    private static final Charset DEFAULT_ENCODING;
    public static final String DEFAULT_PREFIX = "classpath:/templates/";
    public static final String DEFAULT_SUFFIX = ".html";
    // 这两个配置的意思是只要我们把HTML页面放在classpath:/templates/下，thymeleaf就能自动渲染
    // 例如http://127.0.0.1:8080/success就会访问classpath:/templates/success.html
```
官方文档连接：https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.pdf
#### 导入thymeleaf的名称空间
```
<html lang="en" xmlns:th="http://www.thymeleaf.org">    
```
#### 使用thymeleaf语法
详见classpath:/templates/success.html
#### 语法规则
- th:text（会将div里面的文本内容覆盖）
- th:任意html属性（会替换原生属性值）
- 表达式语法，有很多的表达式就不一一讲解了
- inline写法
    ```
    [[]] --> th:text
    [()] --> th:utext
    ```
#### SpringMVC自动配置
详见文件WebMvcAutoConfiguration、WebMvcProperties和[Spring框架](https://docs.spring.io/spring-boot/docs/2.1.4.RELEASE/reference/htmlsingle/#boot-features-developing-web-applications)
- org.springframework.boot.autoconfigure.web中有web配置的所有自动场景
- 扩展SpringMVC的配置
    + 编写一个配置类（@Configuration）要求类型要继承WebMvcConfigurerAdapter(2.1.2以后是WebMvcConfigurer，并且是使用implements关键字，不仅如此，该实现通过default实现空方法代替了适配器的方式直接implements)且不能用注解@EnableWebMvc，这么做就能即保留所有的自动配置，又能用我们自己的拓展配置了
        ```
        // 使用WebMvcConfigurer可以扩展SpringMVC的功能
        @Configuration
        public class MyMvcConfig implements WebMvcConfigurer {
            @Override
            public void addViewControllers(ViewControllerRegistry registry) {
                // 浏览器发送/chenxin请求也是来到success
                registry.addViewController("/chenxin").setViewName("success");
            }
        }
        ```
        原理分析：
        * WebMvcAutoConfiguration是SpringMVC的自动配置类
        * 在做其他自动配置时会导入@Import({WebMvcAutoConfiguration.EnableWebMvcConfiguration.class})
        ```
        @Configuration
        public static class EnableWebMvcConfiguration extends DelegatingWebMvcConfiguration {
            private final WebMvcProperties mvcProperties;
            private final ListableBeanFactory beanFactory;
            private final WebMvcRegistrations mvcRegistrations;
            
            public EnableWebMvcConfiguration(ObjectProvider<WebMvcProperties> mvcPropertiesProvider, ObjectProvider<WebMvcRegistrations> mvcRegistrationsProvider, ListableBeanFactory beanFactory) {
                this.mvcProperties = (WebMvcProperties)mvcPropertiesProvider.getIfAvailable();
                this.mvcRegistrations = (WebMvcRegistrations)mvcRegistrationsProvider.getIfUnique();
                this.beanFactory = beanFactory;
            }        
        ```
        * 上面程序表面SpringBoot会获取所有的配置类，包括我们自己写的配置类
        * 最终效果就是SpringMVC的自动配置和我们的扩展配置都会起作用
    + 全面接管SpringMVC
        * SpringBoot对SpringMVC的自动配置不需要了，所有都是我们自己配置
        * 我们需要在配置类中添加注解@EnableWebMvc，这样所有的SpringMVC的自动配置都失效了，连那个静态资源文件都不能访问了
    + 为什么加了注解@EnableWebMvc自动配置就失效了
        * EnableWebMvc的核心代码
        ```
        @Import({DelegatingWebMvcConfiguration.class})
        public @interface EnableWebMvc {
        }
        ```
        ```
        @Configuration
        public class DelegatingWebMvcConfiguration extends WebMvcConfigurationSupport {
        ```
        ```
        @Configuration
        @ConditionalOnWebApplication(
            type = Type.SERVLET
        )
        @ConditionalOnClass({Servlet.class, DispatcherServlet.class, WebMvcConfigurer.class})
        // 判断容器中没有这个组件的时候，这个自动配置类才生效
        @ConditionalOnMissingBean({WebMvcConfigurationSupport.class})
        @AutoConfigureOrder(-2147483638)
        @AutoConfigureAfter({DispatcherServletAutoConfiguration.class, TaskExecutionAutoConfiguration.class, ValidationAutoConfiguration.class})
        public class WebMvcAutoConfiguration {
        ```
        * 有上面代码可知@EnableWebMvc将WebMvcConfigurationSupport组件导入进来，由注释知这个组件导入后自动配置类就失效了
        * 注意：导入的WebMvcConfigurationSupport只是SpringMVC最基本的功能
#### 如何修改SpringBoot的默认配置
- SpringBoot在自动配置很多组件的时候，先看容器中有没有用户自己配置的（@Bean、@Component），如果有就用用户配置的，如果没有才自动配置，如果有些组件可以有多个（ViewResolver）则将用户配置的和自己的默认配置组合起来
- 在SpringBoot中会有非常多的xxxConfigurer帮助我们进行扩展配置
## 网页制作
### 网页能够实现中英文切换
1. 编写国际化配置文件，抽取页面需要显示的国际化消息（在i18n文件夹中，有三份文件，分别针对中文、英文和默认）
2. 使用ResourceBundleMessageSource编辑管理国际化资源文件
3. 设置基础名
    ```
    # 设置基础名
    spring.messages.basename=i18n.login
    ```
4. 在页面使用fmt:message取出国际化内容
5. 由于浏览器的语言默认配置是中文，所以这里会使用中文的而不是默认的
6. 如果希望网页中的按钮进行切换语言
    - 查看spring-boot-autoconfigure中的WebMvcAutoConfiguration的文件内容，其中国际化Locale（区域信息的对象），LocalResolver（获取区域信息的对象）
    ```
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(
        prefix = "spring.mvc",
        name = {"locale"}
    )    
    public LocaleResolver localeResolver() {
        if (this.mvcProperties.getLocaleResolver() == org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties.LocaleResolver.FIXED) {
            return new FixedLocaleResolver(this.mvcProperties.getLocale());
        } else {
            AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
            localeResolver.setDefaultLocale(this.mvcProperties.getLocale());
            return localeResolver;
        }
    }
    ```
    - 由以上代码知默认情况下是根据请求头带来的区域信息获取Locale进行国际化的
    - 为了实现我们的功能，我们需要自己写一个获取区域信息的对象也叫做区域信息解析器（component.MyLocaleResolver）
    - 这里我们根据访问路径中附带的参数进行判断，代码如下：
    ```
    /**
     * 在连接上携带区域信息
     * */
    public class MyLocaleResolver implements LocaleResolver {
        @Override
        public Locale resolveLocale(HttpServletRequest httpServletRequest) {
            String l = httpServletRequest.getParameter("l");
            // 如果没有附带参数则返回默认的
            Locale locale = Locale.getDefault();
            if (!StringUtils.isEmpty("l")) {
                String[] strList = l.split("_");
                locale = new Locale(strList[0], strList[1]);
            }
            return locale;
        }
    
        @Override
        public void setLocale(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Locale locale) {
    
        }
    }
    ```
    - 最后将其添加到容器中，详见config.MyMvcConfig
    ```
    @Bean
    public LocaleResolver localeResolver() {
        return new MyLocaleResolver();
    }    
    ```
    - 然后在web按钮中添加超链接，详见login.html
    
    
    