bassis
---------------------------

## 本分支为master，为bassis项目最稳定分支

### 目的在于开发出类似springboot使用方式的框架，同时加入其它特性，使其原生支持分布式，借此窥探java语言特性、jvm、spring实现方式，使技术广度和深度得到提升，对后续阅读理解spring、hibernate等开源框架有一个强大的基础，对jvm有一定的认知能力。
* 我是一个java开发者，而非spring开发者
* 技术为了形成系统，站到更高的层面而学习，而不是为了工作

### 说明

* bassis_tools 基于必须的jar支持重写一些工具方法
* bassis_bean  提供自动扫描、bean管理、全注解、ioc、aop、动态代理，全局事件等功能
* bassis_boot  嵌入tomcat8.5，增加main函数启动方式，需要bassis_bean的支持
* bassis_web   实现框架的web，提供类似springmvc的一些功能，例如控制器自动匹配、拦截器栈、及松耦合方式使用bassis_hibernate完成多数据源自动注入与切换等功能
* bassis_hibernate  基于hibernate5.2进行封装，支持多数据源切换
* bassis_jdbc  后续会进行此项目开发

## 注意

本框架为手写框架,基于jdk8开发与编译，在低于此版本的jre上运行可能会出现异常。
模块依赖关系为：
*  bassis_tools  基本第三方jar依赖
*  bassis_bean   bassis_tools 基础依赖
*  bassis_boot   bassis_bean 基础依赖
*  bassis_web    bassis_boot 基础依赖
*  bassis_hibernate bassis_tools 基础依赖
详细情况请参见具体的pom.xml

## 用到的第三方jar包：

* log4j 1.2.17
* gson 2.8.0
* cglib 3.2.12
* hibernate-core 5.2.10.Final
* mysql-connector-java 5.1.40
* servlet-api 4.0.0
* tomcat 8.5.35

## 目前进度(已完成)：
 
### bassis_tools
* 反射工具
* 基础、包装类型判定
* 自定义异常
* gc计数器
* json工具
* Properties文件读取器
* string工具
* 并发测试工具
* http请求工具
* log4j默认配置

### bassis_bean
* class扫描器
* @Autowired 实现
* @Component 实现
* @Aop 实现
* @Scope 实现
* 基于cglib与jdk动态代理
* 基于cglib的bean copy
* 自定义事件
* bean工厂
* 属性循环依赖注入

### bassis_boot
* main函数启动tomcat
* 默认基本启动配置
* 默认filter及编码filter
* 默认servlet容器

### bassis_web
* @Controller 实现
* 请求路径自动匹配bean实现
* @Interceptor及Interceptor栈实现
* @RequestMapping 实现
* 数据装配与解析返回基本实现

### bassis_hibernate
* 多hibernate配置启动多实例
* @Data 完成
* 基本db操作实现
