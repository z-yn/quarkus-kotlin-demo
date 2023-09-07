# Quarkus DI solution

基于[Jakarta Contexts and Dependency Injection 4.0](https://jakarta.ee/specifications/cdi/4.0/jakarta-cdi-spec-4.0.html)
规范。

## Bean发现

简单的bean发现机制：[使用注解的Bean发现](https://jakarta.ee/specifications/cdi/4.0/jakarta-cdi-spec-4.0.html#default_bean_discovery)
，并且没有可见性边界。
Bean包含

- 应用程序的类
- 包含beans.xml描述符
- jandex索引，META-INF/jandex.idx
- application.properties中的`quarkus.index-dependency`
