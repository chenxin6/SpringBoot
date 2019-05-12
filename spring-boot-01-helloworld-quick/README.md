# spring-boot-01-helloworld-quick
## 使用IntelliJ支持的Spring Initializer快速创建SpringBoot工程 __（需要联网）__
Create New Project->Spring Initializer->如果没有绑定JDK环境，则需要绑定->Next->设置Group为“cn.edu.ustc.nsrl”；设置Artifact为“spring-boot-01-helloworld-quick” __（不能大写）__；设置Package为“cn.edu.ustc.nsrl.springboot”；其他的设置可根据实际情况自行决定->Next->这个时候会让你选择项目中需要的依赖，这里由于是演示我们就只选择Web的依赖即可->Next->确定完工程名和路径后点击“Finish”->进入工程后右下角会出现提示“Maven projects need to be imported”，选择Enable Auto-Import __（或者可以在Preferences->Build->Build Tools->Maven->Importing->勾选Import Maven projects automatically）__
## 特征分析
默认生成的SpringBoot工程具有以下特点
1. 主程序已经生成好了，我们只需要添加自己的逻辑代码
2. resources文件夹的目录结构
    - static：保存所有的静态资源例如js，css，images
    - templates：保存所有的模版页面（SpringBoot默认jar包使用嵌入式的Tomcat，默认不支持JSP页面，但可以使用模版引擎，例如freemarker和thymeleaf）
    - application.properties：SpringBoot应用的配置文件，在这里我们可以修改一些默认设置，例如端口号
