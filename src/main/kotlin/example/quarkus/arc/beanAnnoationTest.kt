package example.quarkus.arc

import io.quarkus.runtime.StartupEvent
import jakarta.enterprise.context.RequestScoped
import jakarta.enterprise.event.Observes
import jakarta.inject.Singleton


class HasNotAnnotationBean {

    //即使声明类没有使用bean定义注释注释。
    // 也可以发现生产者方法、字段和观察者方法。
    fun startup(@Observes event: StartupEvent) {

    }
}

class NotBean {
}

var lazyBeanNormalInit: Boolean = false
var lazyBeanPseudoInit: Boolean = false

//@Unremovable注解保证Bean不会被优化掉。
// 默认情况是会移除无用的Bean
@RequestScoped
internal class LazyBeanNormalScope {
    init {
        lazyBeanNormalInit = true
        println("LazyBeanNormalScope: inited")
    }

    fun invoke() {
        println("LazyBeanNormalScope: call invoke")
    }
}


@Singleton
internal class LazyBeanPseudoScope {
    init {
        lazyBeanPseudoInit = true
        println("LazyBeanPseudoScope: inited")
    }

    fun invoke() {
        println("LazyBeanPseudoScope: call invoke")
    }
}
