bassis
---------------------------
### 当前项目为最新位置，旧地址请转到 [liuchengts/bassis](https://github.com/liuchengts/bassis)

### 目的在于开发出类似springboot使用方式的框架，同时加入其它特性，使其原生支持分布式，借此窥探java语言特性、jvm、spring实现方式，使技术广度和深度得到提升，对后续阅读理解spring、hibernate等开源框架有一个强大的基础，对jvm有一定的认知能力。

* 我是一个`java`开发者，而非`spring`开发者
* 技术为了形成系统，站到更高的层面而学习，而不是单纯为了工作

### 更新说明
- 2023-01-28
  - 增加`boot`下关于`http`请求处理和响应的若干功能
    - `http`的请求头默认约定
    - `http`的请求携带参数的方式
    - `@RequestMapping` 支持自动检测请求方式进行方法入参的转换处理（不需要显示声明入参是`json`）
    - 保留返回`html`页面的功能，但不完全实现它
    - 在`@RequestMapping`工作前做出路由到方法参数特征标记
  - 修复`boot`下`http`的若干`bug`
    - `json`参数的异步处理`bug`
    - 表单参数的特征判断处理
- 2023-01-27
  - 实现`@Listener`
  - 去掉`boot`下的`test`代码，测试全部由`bassis-test`完成
  - 修复若干`beanFactory`中的`bug`
- 2023-01-13
  - 更新`jdk`依赖为`11`,后续不再支持`jdk8`
  - 更新`asm api`为`asm5`
  - 重构基于`vertx`开发的`bassis_boot`，并通过测试
  - 删除`bassis_boot`中对`tomcat`的所有支持和依赖
  - 重构`http`请求方式支持，允许一个路由同时存在多种请求方式
  - 丢弃`http`拦截器与过滤器，全部采用`aop`直接实现
  - 增加`http`快速寻址与快速建立`vertx http`路由的算法
  - 启动模式完全符合`springBoot`的使用习惯（启动类上不需要任何注解）
  - 增强`aop`功能中可以追加任意参数及方法本身的入参进入`aop`方法供其使用

- 2021-02-01 
  - 更新日志框架为 ```slf4j``` 使用```logback```实现
    - 增加```logback```集成到当前框架
    - 在```bassis_bean``` 下对日志框架进行测试
  - 计划重构 ```bassis_bean``` 部分代码
    - 重写循环依赖注入解决的问题
  - 计划重写 ```bassis_boot``` 部分代码
    - 将```tomcat```去掉，改由```vertx```实现
    - 将基于```vertx```作为本框架```io```核心，以及后期的分布式基础支撑
  - 计划暂时停止维护```bassis_data```，主力开发前两项
   
### 说明

* `bassis_tools` 基于必须的jar支持重写一些工具方法
* `bassis_bean`  提供自动扫描、bean管理、全注解、ioc、aop、动态代理，全局事件等功能
* ~~bassis_boot  嵌入tomcat8.5，增加main函数启动方式，需要bassis_bean的支持，实现框架的web，提供类似springmvc的一些功能，例如控制器自动匹配、拦截器栈、及松耦合方式使用bassis_hibernate完成多数据源自动注入与切换等功能~~
* `bassis_boot` 使用`vertx`用于网络通信，增加`main`函数启动方式，需要`bassis_bean`的支持，实现框架的`web`，提供类似`springBoot`的一些功能，例如控制器自动匹配、及松耦合方式使用`bassis_hibernate`完成多数据源自动注入与切换等功能
* `bassis_data`  前期基于`jdbc`进行开发

# 项目演示
### 演示 web 功能
* 直接运行 `bassis-test` 或 `bassis-boot`下的`test`部分 即可
### 演示 bean 功能(只启动框架核心的bean部分)
* 可以配置启动参数 `bassis.start.schema=core` 或者直接运行 `bassis-bean`下的`test`部分

## 注意

本框架为手写框架,基于jdk8开发与编译，在低于此版本的jre上运行可能会出现异常。
模块依赖关系为：
*  `bassis_tools ->`   基本第三方jar依赖
*  `bassis_bean ->`   `bassis_tools` 基础依赖
*  `bassis_boot ->`   `bassis_bean` 基础依赖
*  `bassis_data ->`   `bassis_bean` 基础依赖
*  详细情况请参见具体的pom.xml

## 用到的第三方jar包：

* ~~log4j 1.2.17~~
* `gson 2.8.0`
* `cglib 3.2.12`
* `mysql-connector-java 5.1.40`
* ~~servlet-api 4.0.0~~
* ~~tomcat 8.5.35~~
* `logback 1.3.0-alpha5`
* `slf4j-api 2.0.0-alpha1`
* `vertx-web 4.2.3`

## 目前进度(已完成)：
 
### bassis_tools
* 反射工具
* 基础、包装类型判定
* 自定义异常
* `gc`计数器
* `json`工具
* `Properties`文件读取器
* `string`工具
* 并发测试工具
* `http`请求工具
* ~~log4j默认配置~~
* `logback`默认配置

### bassis_bean
* `class`扫描器
* `@Autowired` 实现
* `@Component` 实现
* `@Aop` 实现
* `@Scope` 实现
* 基于`cglib`与`jdk`动态代理
* 基于`cglib`的`bean copy`
* 自定义事件
* `bean`工厂
* 属性循环依赖注入
* 接口到实现类转换注入
* `@Listener` 实现

### bassis_boot
* ~~main函数启动tomcat~~
* 静态`main`函数启动（无需注解）
* 默认基本启动配置
* ~~默认filter及编码filter~~
* ~~默认servlet容器~~
* `@Controller` 实现
* 请求路径自动匹配bean实现
* ~~@Interceptor及Interceptor栈实现~~
* `@RequestMapping` 实现
* 数据装配与解析返回基本实现
* `vertx`集成封装，作为基础通信组件

### bassis_data
* 封装`jdbc`

## 目前进度(需要调整及未完成的功能)：

### bassis_tools
* 无，按需求适当增加

### bassis_bean
* `@Autowired` 需要根据反射实现自动获取注入对象 去掉`aclass`参数 -- 已完成
* `@Aop` 需要根据反射实现自动获取注入对象 去掉`aclass`参数 -- 已完成
* `@Scope` 需要针对多实例模式下的`bean`做`copy`或者重新创建操作 -- 已完成

### bassis_boot
* 默认基本启动配置 需要支持个性化配置参数 -- 已完成
* ~~默认servlet容器 需要作为bassis_web基础依赖入口 -- 已完成~~
* `@Controller` 需要重写`ioc`逻辑 要与`@Component`保持一致 -- 已完成
* 请求路径自动匹配`bean`实现 需要优化路径存储已经寻址算法 -- 已完成
* ~~@Interceptor及Interceptor栈实现 需要调试来兼容最新的aop功能 -- 舍弃~~
* `@RequestMapping` 实现,需要配合路径自动匹配 -- 已完成
* 数据装配与解析返回基本实现,需要重写定义大部分返回逻辑，抽离页面与数据的耦合 -- 已完成
* 增加`http`的认证（`cookie、token`等） -- 未完成

### bassis_hibernate
* 多数据源`jdbc`
* 事务分组支持提交、回滚等功能

### 其他问题
* 编译及打包报错，增加`maven`运行参数 `-Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true`
