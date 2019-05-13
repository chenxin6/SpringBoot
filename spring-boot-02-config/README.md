# spring-boot-02-config
## 配置文件
SpringBoot使用一个全局的配置文件，配置文件名是固定的
1. application.properties
2. application.yml

它们的作用是：修改SpringBoot自动配置的默认值，使得SpringBoot在底层都给我们自动配置好 __（properties加载级别高，同配置项只会高级别的配置生效）__
## 标记语言
以前的配置文件，大多都使用的是xxx.xml文件，但是YAML文件以数据为中心，比json，xml等更适合做配置文件，配置例子如下：
```
server:
  port: 8081
```
同样的配置信息，则XML文件的配置如下：
```
<server>
    <port>8081</port>
</server>
```
### YAML语法
#### 基本语法
__k:(空格)v__：表示一对键值对，空格是必须有，并且以空格的缩进来控制层级关系，只要是左对齐的一列数据就都是一个层级的，例如：
```
server:
  port: 8081
  path: /hello
```
#### 值的写法
- 普通的字面量值，例如：数字，字符串，布尔
    + 字符串默认不用加上单引号和双引号
    + 双引号围起来的字符串表示不会转义字符串里面的特殊字符
    + 单引号围起来的字符串表示会转义字符串里面的特殊字符

- 对象和Map
    + 还是以键值对的方式写，只是说需要在这个对象的层级底下写上属性和值的关系（注意缩进），例如：
    ```
    friends:
        name: chenxin
        age: 20
    ```
    + 也可以使用行内写法
    ```
    friends: {name: chenxin,age: 20}
    ```
- 数组
    + 用-值表示数组中的一个元素
    ```
    pets:
     - cat
     - dog
     - pig     
    ```
    + 也可以使用行内写法
    ```
    pets: [cat,dog,pig]
    ```
#### 配置文件值的注入
配置文件：
```
person:
  lastName: chenxin
  age: 18
  boss: false
  Date: 2000/01/01
  maps: {k1: v1, k2: v2}
  lists:
    - zhangsan
    - lisi
    - wangwu
    - zhaoliu
  dog:
    name: xiaogou
    age: 2
```
javaBean：实体类为cn.edu.ustc.nsrl.springboot.bean.Person

我们可以导入配置文件处理器，以后编写配置就有提示了
```
<!--导入配置文件处理器，配置文件进行绑定就会有提示-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-configuration-processor</artifactId>
    <optional>true</optional>
</dependency>
```
## properties文件的使用
它的使用跟yml文件的类似，但是由于properties使用的ASCII编码，所以会出现乱码的情况，解决方法是：Preferences->Editor->File Encodings->勾选Transparent native-to-ascii conversion->OK，然后重新创建properties文件即可
## @ConfigurationProperties与@Value的区别
|  | @ConfigurationProperties | @Value |
| ------ | ------ | ------ |
| 功能 | 批量注入配置文件中的属性 | 一个个指定 |
| 松散绑定（松散语法） | 支持 | 不支持 |
| SpEL | 不支持 | 支持 |
| JSR303校验 | 支持 | 不支持 |
| 复杂类型封装（例如Map） | 支持 | 不支持 |

配置文件不管是yml还是properties，只要选择了这两个的一个注解就都能被获取到值：
- 如果说，我们只是在某个业务逻辑中需要获取一下配置文件中的某项值，则使用@Value
- 如果说，我们专门编写了一个javaBean来和配置文件进行映射，我们就直接使用@ConfigurationProperties
## @PropertySource和@ImportResource
- @PropertySource：默认是从全局配置文件中获取值，如果要从其他配置文件中获取则需要注解@PropertySource
- @ImportResource：导入Spring的配置文件，让配置文件里面的内容生效，由于SpringBoot里面没有Spring的配置文件，我们自己编写的配置文件也不能自动识别，如果想让Spring的配置文件生效，有两种方法
    + 将这个注解标注在配置类上让其加载进来，详见SpringBoot02ConfigApplication.java
    + （强烈推荐）使用SpringBoot中的配置类取代Spring的配置文件，即SpringBoot推荐使用全注解的方式（@Bean）详见MyAppConfig.java
## 配置文件的占位符
- 随机数，例如${random.value}、${random.int}、${random.long}、${random.int(10)}、${random.int[1024, 65535]}、${random.uuid}
- 占位符获取之前配置的值，如果没有可以使用冒号指定默认值，例如
```
person.last-name=李四${random.uuid}${hello:chenxin}
```
## Profile的使用
Profile是Spring对不同环境提供不同配置功能的支持，可以通过激活、指定参数等方式快速切换环境
- 多Profile文件，我们在主配置文件编写的时候，文件名可以是application-{profile}.properties/yml，默认情况下使用application.properties
- yml支持多文档块，详见application.yml
- 激活指定Profile有三种方法，优先级：第一种<第二种<第三种
    + 在application.yml配置文件中
    ```
    spring:
      profiles:
        active: dev
    ```
    + 在application.properties配置文件中spring.profiles.active=dev
    + 命令行运行的时候添加参数：--spring.profiles.active=dev
    + 虚拟机参数（不建议使用）
## 配置文件加载位置
配置文件通常会放到以下四个路径中，优先级至上而下逐渐降低，如果配置发生了重复则高优先级的配置会覆盖低优先级的配置
- file:./config/
- file:./
- classpath:/config/
- classpath:/

__项目打包好以后，我们可以使用命令行参数的形式，启动项目的时候来指定配置文件的新位置，指定配置文件的优先级最高且和默认加载的这些配置文件共同起作用形成互补配置__
## 自动配置原理
1. SpringBoot启动的时候加载主配置类，开启了自动配置功能@EnableAutoConfiguration
2. @EnableAutoConfiguration作用：利用@Import({AutoConfigurationImportSelector.class})给容器中导入一些组件
    - AutoConfigurationImportSelector的selectImports方法中有这么一句程序
    ```
    AutoConfigurationImportSelector.AutoConfigurationEntry autoConfigurationEntry = this.getAutoConfigurationEntry(autoConfigurationMetadata, annotationMetadata);
    ```
    - getAutoConfigurationEntry方法中有这么一句程序会获取候选的配置
    ```
    List<String> configurations = this.getCandidateConfigurations(annotationMetadata, attributes);
    ```
    - getCandidateConfigurations方法中有这么一句程序会扫描所有jar包类路径（META-INF/spring.factories）中的所有配置类，然后把扫描到的这些文件内容包装成properties对象，然后从properties中获取到EnableAutoConfiguration.class类（类名）对应的值，然后再添加到容器中
    ```
    // 简单来说就是该语句将类路径（META-INF/spring.factories）里面配置的所有EnableAutoConfiguration的值加载到了容器中
    List<String> configurations = SpringFactoriesLoader.loadFactoryNames(this.getSpringFactoriesLoaderFactoryClass(), this.getBeanClassLoader());
    ```
    - 加载到容器中的内容如下，每一个这样的xxxAutoConfiguration类都是容器中的组件，用他们来自动配置
    ```
    # Auto Configure
    org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
    org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration,\
    org.springframework.boot.autoconfigure.aop.AopAutoConfiguration,\
    org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration,\
    org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration,\
    org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration,\
    org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration,\
    org.springframework.boot.autoconfigure.cloud.CloudAutoConfiguration,\
    org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration,\
    org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration,\
    org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration,\
    org.springframework.boot.autoconfigure.couchbase.CouchbaseAutoConfiguration,\
    org.springframework.boot.autoconfigure.dao.PersistenceExceptionTranslationAutoConfiguration,\
    org.springframework.boot.autoconfigure.data.cassandra.CassandraDataAutoConfiguration,\
    org.springframework.boot.autoconfigure.data.cassandra.CassandraReactiveDataAutoConfiguration,\
    org.springframework.boot.autoconfigure.data.cassandra.CassandraReactiveRepositoriesAutoConfiguration,\
    org.springframework.boot.autoconfigure.data.cassandra.CassandraRepositoriesAutoConfiguration,\
    org.springframework.boot.autoconfigure.data.couchbase.CouchbaseDataAutoConfiguration,\
    org.springframework.boot.autoconfigure.data.couchbase.CouchbaseReactiveDataAutoConfiguration,\
    org.springframework.boot.autoconfigure.data.couchbase.CouchbaseReactiveRepositoriesAutoConfiguration,\
    org.springframework.boot.autoconfigure.data.couchbase.CouchbaseRepositoriesAutoConfiguration,\
    org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchAutoConfiguration,\
    org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration,\
    org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration,\
    org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration,\
    org.springframework.boot.autoconfigure.data.ldap.LdapDataAutoConfiguration,\
    org.springframework.boot.autoconfigure.data.ldap.LdapRepositoriesAutoConfiguration,\
    org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration,\
    org.springframework.boot.autoconfigure.data.mongo.MongoReactiveDataAutoConfiguration,\
    org.springframework.boot.autoconfigure.data.mongo.MongoReactiveRepositoriesAutoConfiguration,\
    org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration,\
    org.springframework.boot.autoconfigure.data.neo4j.Neo4jDataAutoConfiguration,\
    org.springframework.boot.autoconfigure.data.neo4j.Neo4jRepositoriesAutoConfiguration,\
    org.springframework.boot.autoconfigure.data.solr.SolrRepositoriesAutoConfiguration,\
    org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration,\
    org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration,\
    org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration,\
    org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration,\
    org.springframework.boot.autoconfigure.data.web.SpringDataWebAutoConfiguration,\
    org.springframework.boot.autoconfigure.elasticsearch.jest.JestAutoConfiguration,\
    org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration,\
    org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration,\
    org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration,\
    org.springframework.boot.autoconfigure.h2.H2ConsoleAutoConfiguration,\
    org.springframework.boot.autoconfigure.hateoas.HypermediaAutoConfiguration,\
    org.springframework.boot.autoconfigure.hazelcast.HazelcastAutoConfiguration,\
    org.springframework.boot.autoconfigure.hazelcast.HazelcastJpaDependencyAutoConfiguration,\
    org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration,\
    org.springframework.boot.autoconfigure.http.codec.CodecsAutoConfiguration,\
    org.springframework.boot.autoconfigure.influx.InfluxDbAutoConfiguration,\
    org.springframework.boot.autoconfigure.info.ProjectInfoAutoConfiguration,\
    org.springframework.boot.autoconfigure.integration.IntegrationAutoConfiguration,\
    org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration,\
    org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,\
    org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration,\
    org.springframework.boot.autoconfigure.jdbc.JndiDataSourceAutoConfiguration,\
    org.springframework.boot.autoconfigure.jdbc.XADataSourceAutoConfiguration,\
    org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration,\
    org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration,\
    org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration,\
    org.springframework.boot.autoconfigure.jms.JndiConnectionFactoryAutoConfiguration,\
    org.springframework.boot.autoconfigure.jms.activemq.ActiveMQAutoConfiguration,\
    org.springframework.boot.autoconfigure.jms.artemis.ArtemisAutoConfiguration,\
    org.springframework.boot.autoconfigure.groovy.template.GroovyTemplateAutoConfiguration,\
    org.springframework.boot.autoconfigure.jersey.JerseyAutoConfiguration,\
    org.springframework.boot.autoconfigure.jooq.JooqAutoConfiguration,\
    org.springframework.boot.autoconfigure.jsonb.JsonbAutoConfiguration,\
    org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration,\
    org.springframework.boot.autoconfigure.ldap.embedded.EmbeddedLdapAutoConfiguration,\
    org.springframework.boot.autoconfigure.ldap.LdapAutoConfiguration,\
    org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration,\
    org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration,\
    org.springframework.boot.autoconfigure.mail.MailSenderValidatorAutoConfiguration,\
    org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration,\
    org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration,\
    org.springframework.boot.autoconfigure.mongo.MongoReactiveAutoConfiguration,\
    org.springframework.boot.autoconfigure.mustache.MustacheAutoConfiguration,\
    org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,\
    org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration,\
    org.springframework.boot.autoconfigure.reactor.core.ReactorCoreAutoConfiguration,\
    org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration,\
    org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration,\
    org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration,\
    org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration,\
    org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration,\
    org.springframework.boot.autoconfigure.sendgrid.SendGridAutoConfiguration,\
    org.springframework.boot.autoconfigure.session.SessionAutoConfiguration,\
    org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientAutoConfiguration,\
    org.springframework.boot.autoconfigure.solr.SolrAutoConfiguration,\
    org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration,\
    org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration,\
    org.springframework.boot.autoconfigure.transaction.jta.JtaAutoConfiguration,\
    org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration,\
    org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration,\
    org.springframework.boot.autoconfigure.web.embedded.EmbeddedWebServerFactoryCustomizerAutoConfiguration,\
    org.springframework.boot.autoconfigure.web.reactive.HttpHandlerAutoConfiguration,\
    org.springframework.boot.autoconfigure.web.reactive.ReactiveWebServerFactoryAutoConfiguration,\
    org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration,\
    org.springframework.boot.autoconfigure.web.reactive.error.ErrorWebFluxAutoConfiguration,\
    org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientAutoConfiguration,\
    org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration,\
    org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration,\
    org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration,\
    org.springframework.boot.autoconfigure.web.servlet.HttpEncodingAutoConfiguration,\
    org.springframework.boot.autoconfigure.web.servlet.MultipartAutoConfiguration,\
    org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration,\
    org.springframework.boot.autoconfigure.websocket.reactive.WebSocketReactiveAutoConfiguration,\
    org.springframework.boot.autoconfigure.websocket.servlet.WebSocketServletAutoConfiguration,\
    org.springframework.boot.autoconfigure.websocket.servlet.WebSocketMessagingAutoConfiguration,\
    org.springframework.boot.autoconfigure.webservices.WebServicesAutoConfiguration
    ```
    - 每一个自动配置类进行自动配置功能，以HttpEncodingAutoConfiguration为例
        + 它具有如下注解，各自的功能如注释所示，总的来说是根据当前不同的条件判断决定这个配置类是否生效
        ```
        @Configuration // 表示这是一个配置类，以前编写的配置文件一样，也可以给容器中添加组件
        @EnableConfigurationProperties({HttpProperties.class}) // 启动指定类的ConfigurationProperties功能，将配置文件中对应的值和HttpEncodingProperties绑定起来，并把HttpProperties加入到ioc容器中
        // Spring底层@Conditional注解，如果我们满足指定的条件，整个配置类里面的配置就会生效，这个注解的条件就是判断当前应用是否是Web应用
        @ConditionalOnWebApplication(
            type = Type.SERVLET
        )
        @ConditionalOnClass({CharacterEncodingFilter.class}) // 判断当前项目有没有这个类"CharacterEncodingFilter"，这个类是SpringMVC中进行乱码解决的过滤器
        // 判断配置文件中是否存在这个配置spring.http.encoding.enabled，如果不配置spring.http.encoding.enabled=true也是默认生效的，注意是默认生效的
        @ConditionalOnProperty(
            prefix = "spring.http.encoding",
            value = {"enabled"},
            matchIfMissing = true
        )
        public class HttpEncodingAutoConfiguration {
            // filter在设置的时候会根据这个properties中的内容进行设置，这个properties已经和SpringBoot的配置文件映射了，观察这个properties中的成员变量可以知道我们可以配置哪些变量
            private final Encoding properties;
            
            // 由于只有一个有参构造器，所以参数的值会从容器中拿，即这个构造方法传入的参数HttpProperties properties就是注解@EnableConfigurationProperties({HttpProperties.class})传入到容器中的properties
            public HttpEncodingAutoConfiguration(HttpProperties properties) {
                this.properties = properties.getEncoding();
            }
            
            @Bean
            @ConditionalOnMissingBean
            public CharacterEncodingFilter characterEncodingFilter() {
                CharacterEncodingFilter filter = new OrderedCharacterEncodingFilter();
                filter.setEncoding(this.properties.getCharset().name());
                filter.setForceRequestEncoding(this.properties.shouldForce(org.springframework.boot.autoconfigure.http.HttpProperties.Encoding.Type.REQUEST));
                filter.setForceResponseEncoding(this.properties.shouldForce(org.springframework.boot.autoconfigure.http.HttpProperties.Encoding.Type.RESPONSE));
                return filter;
            }
        }         
        ```
        + HttpProperties.class中有这么一个注解
        ```
        // 从配置文件中获取指定的值和bean的属性进行绑定，所有在配置文件中能配置的属性都是在xxxProperties类中封装着
        @ConfigurationProperties(
            prefix = "spring.http"
        )
        ```
## 精髓
1. SpringBoot启动会加载大量的自动配置类并且要在一定的条件下才会生效，我们可以通过debug=true属性让控制台打印自动配置报告显示哪些自动配置类生效
2. 我们看我们需要的功能有没有SpringBoot默认写好的自动配置类
3. 我们再来看这个自动配置类中到底配置类哪些组件，只要我们要用的组件有，我们就不需要再来配置了
4. 给容器中自动配置类添加组件的时候，会从properties类中获取某些属性，我们就可以在配置文件中指定这些属性的值
5. 通常来说xxxAutoConfiguration和xxxProperties是成对出现的，前者给容器添加组件，后者封装配置文件中的相关属性