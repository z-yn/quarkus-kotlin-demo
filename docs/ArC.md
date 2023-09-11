# Quarkus DI solution

基于[Jakarta Contexts and Dependency Injection 4.0](https://jakarta.ee/specifications/cdi/4.0/jakarta-cdi-spec-4.0.html)
规范。

## Bean发现

简单的bean发现机制：[使用注解的Bean发现](https://jakarta.ee/specifications/cdi/4.0/jakarta-cdi-spec-4.0.html#default_bean_discovery)
，并且没有可见性边界。
Bean包含:

- 应用程序的类
- 包含beans.xml描述符
- jandex索引，META-INF/jandex.idx
- application.properties中的`quarkus.index-dependency`

## 实例初始化逻辑

#### 没有Bean定义注释的Bean类也可能会被发现

- CDI规定：没有Bean定义注释的Bean类不会被发现，
- 但是，即使声明类没有使用bean定义注释，也可以发现生产者方法、字段和观察者方法。
- 实际上，声明bean类被认为带有@Dependent注释

#### 默认CDI的Bean是懒加载的，仅当被需要的时候实例化。

- normal scoped bean如(@ApplicationScoped, @RequestScoped)在一个函数调用的时候会初始化， 所以仅仅注入依赖是不够的，因为会使用客户端代理
- pseudo-scope bean，注入的时候就会初始化。

#### Startup事件可以帮助启动时初始化
- 
