# 声明周期

[refer](https://quarkus.io/guides/lifecycle)

## Main方法

默认情况下，Quarkus将自动生成一个main方法，该方法将引导Quarkus，然后等待关机启动。
如果需要修改启动行为。需要自定义一个main方法，可以实现一个`io.quarkus.runtime.QuarkusApplication`
然后启动它。

#### 命令行参数

使用@CommandLineArguments，在需要的时候注入

```
    @Inject
    @CommandLineArguments
    String[]args;
```

## 生命周期事件

`io.quarkus.runtime.StartupEvent`和`io.quarkus.runtime.ShutdownEvent`
可以使用`jakarta.enterprise.event.Observes`来关注启动事件和关闭事件

## [Quarkus启动三阶段](https://quarkus.io/guides/writing-extensions#bootstrap-three-phases)

#### 1.Augmentation

这是第一个阶段，由构建步骤处理器完成。 这些处理器可以访问Jandex注释信息，可以解析任何描述符并读取注释，但不应该尝试加载任何应用程序类。
这些构建步骤的输出是一些记录的字节码，使用ObjectWeb ASM项目的一个名为Gizmo(ext/ Gizmo)的扩展，用于在运行时实际引导应用程序。

依据于`@io.quarkus.deployment.annotations.Record`在启动时关联的`io.quarkus.deployment.annotations.ExecutionTime`值。
影响后续的两个步骤

#### 2.Static Init

如果字节码用`@Record(STATIC_INIT)`记录，那么它将从主类的静态初始化方法执行。
对于原生可执行构建，此代码作为原生构建过程的一部分在普通JVM中执行，并且在此阶段生成的任何保留的对象将通过映像映射文件直接序列化到本机可执行文件中。
这意味着，如果一个框架可以在这个阶段启动，那么它将把其启动状态直接写入映像，因此在启动映像时不需要执行启动代码。(
提前解析启动)

在这个阶段可以做的事情有一些限制，因为Substrate VM不允许在本机可执行文件中使用某些对象。
例如，在此阶段不应该尝试监听端口或启动线程。此外，在静态初始化期间不允许读取运行时配置。

在非原生纯JVM模式下，除了静态初始化总是首先执行之外，静态初始化和运行时初始化之间没有真正的区别。
他的模式受益于与原生模式相同的构建阶段增强，因为描述符解析和注释扫描是在构建时完成的，并且可以从构建输出jar中删除任何相关的类/框架依赖项。
在WildFly这样的服务器中，与部署相关的类(如XML解析器)会在应用程序的整个生命周期中一直存在，从而耗尽宝贵的内存。
Quarkus的目标是消除这种情况，这样只有在运行时加载的类才会在运行时实际使用。
例如，Quarkus应用程序应该加载XML解析器的唯一原因是用户在其应用程序中使用XML。任何配置的XML解析都应该在增强阶段完成。

#### 3.Runtime Init

如果字节码是用@Record(RUNTIME_INIT)记录的，那么它将从应用程序的主方法执行。
此代码将在原生可执行启动上运行。一般来说，在这个阶段应该执行尽可能少的代码，并且应该限制在编码中需要打开端口等。
尽可能多的推入@Record(STATIC_INIT)阶段允许两种不同的优化:

1. 在原生可执行和纯JVM模式下，这都允许应用程序尽可能快地启动，因为处理是在构建时完成的。
   这也将应用程序中所需的类/原生代码最小化为纯运行时相关行为。
2. 原生可执行模式的另一个好处是，Substrate可以更容易地消除不使用的特性。 如果特征是通过字节码直接初始化的，Substrate可以检测到一个方法从未被调用，并消除该方法。
   如果在运行时读取配置，则Substrate无法推断配置的内容，因此需要保留所有功能以备需要。
