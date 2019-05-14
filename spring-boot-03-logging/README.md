# spring-boot-03-logging
## 日志框架的使用
市面上的日志框架：JUL、JCL、Jboss-logging、Logback、Log4j、Logj2、SLF4j

| 日志门面（日志的抽象层） | 日志实现 |
| ------ | ------ |
| JCL、SLF4j、Jboss-logging | Log4j、Logj2、Logback |

左边选一个门面（抽象层），右边来选一个实现。通常情况下如下选择
- 日志门面：SLF4j
- 日志实现：Logback

Spring框架默认是用JCL而SpringBoot选用SLF4j和Logback
## SLF4j的使用
给系统里面导入SLF4j和Logback的jar包后，日志记录方法的调用，不应该来直接调用日志的实现类，而是调用日志抽象层里面的方法
```
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloWorld {
  public static void main(String[] args) {
    Logger logger = LoggerFactory.getLogger(HelloWorld.class);
    logger.info("Hello World");
  }
}
```  
每一个日志的实现框架都有自己的配置文件，使用SLF4j以后，配置文件还是做成日志实现框架自己本身的配置文件，例如你使用Log4j作为实现，则配置文件还是Log4j的配置文件
## 统一日志记录，将所有日志框架统一成使用SLF4j进行输出
1. 将系统中其他日志框架先排除出去
2. 用中间包来替换原有的日志框架
3. 导入SLF4j其他的实现
## SpringBoot日志关系
依赖关系链spring-boot-starter-web->spring-boot-starter->spring-boot-starter-logging

spring-boot-starter-logging又依赖以下三个依赖：
- logback-classic：使用logback记录日志
- log4j-to-slf4j：把其他日志框架转为slf4j，为导入日志抽象层铺垫
- jul-to-slf4j：把其他日志框架转为slf4j，为导入日志抽象层铺垫

总结：
1. SpringBoot底层也是使用SLF4j+Logback
2. SpringBoot也把其他的日志都替换成了SLF4j
3. 中间转换包
4. 如果我们要引入其他框架？一定要把这个框架的默认日志依赖移除掉吗？
    - Spring框架用的是commons-logging（JCL）所以在以前SpringBoot的版本中会使用exclusion去除掉commons-logging的依赖
5. SpringBoot能自动适配所有的日志，而且底层使用SLF4j+Logback的方式记录日志，引入其他框架的时候，只需要把这个框架依赖的日志排除掉
## 日志的使用
### 日志的级别
```
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
```
可以在文件application.properties中修改默认的值，其中logging.file和logging.path的差别如下（logging.file比logging.path优先级高）：

| logging.file | logging.path | Example | Description |
| ------ | ------ | ------ | ------ |
| None | None |  | 只在控制台输出 |
| 指定文件名 | None | my.log | 输出日志到my.log文件 |
| None | 指定文件名 | /var/log | 输出到指定目录的spring.log文件中 |
### 日志输出格式
```
#控制台输出的日志格式 
#%d：日期
#%thread：线程号 
#%-5level：靠左 级别 
#%logger{50}：全类名50字符限制,否则按照句号分割
#%msg：消息+换行
#%n：换行
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n
```
### 指定配置
给类路径下放上每个日志框架自己的配置文件即可，这样SpringBoot就不实用它默认的配置了

| logging System | Customization |
| ------ | ------ |
| Logback |	logback-spring.xml ,logback-spring.groovy,logback.xml or logback.groovy |
| Log4J2 | log4j2-spring.xml or log4j2.xml |
| JDK(Java Util Logging) | logging.properties |

__注意：如果选择logback.xml则会被日志框架直接识别，如果选择logback-spring.xml日志框架就不直接加载配置项，由SpringBoot解析日志配置，这就可以通过在文件中加入如下配置使得其可以根据不同的环境选择不同的日志配置了__
```
<springProfile name="dev">
	<!-- 可以指定某段配置只在某个环境下生效 -->
</springProfile>
<springProfile name!="dev">
	<!-- 可以指定某段配置只在某个环境下生效 -->
</springProfile>
```
## 切换日志框架（例如我们不使用Logback而是使用Log4j，__通常不这么做__）
可以按照SLF4j的日志适配图进行相关的切换，SLF4j+Log4j的方式：
```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <exclusions>
        <exclusion>
            <artifactId>logback-classic</artifactId>
            <groupId>ch.qos.logback</groupId>
        </exclusion>
    </exclusions>
</dependency>

<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-log4j12</artifactId>
</dependency>
```
另一个演示切换为Log4j2
```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <exclusions>
        <exclusion>
            <artifactId>spring-boot-starter-logging</artifactId>
            <groupId>org.springframework.boot</groupId>
        </exclusion>
    </exclusions>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-log4j2</artifactId>
</dependency>
```