package example.quarkus.arc

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured
import jakarta.enterprise.inject.spi.CDI
import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import org.hamcrest.CoreMatchers
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

@QuarkusTest
class BeanDiscoveryTest {

    //当前的CDI的Bean管理为Arc实现。可见Jakarta EE的CDI的标准的实现是插件化的
    //代码看起来是CDI.setCDIProvider()来设置CDI配置的
    @Test
    fun testBeanManager() {
        val beanContainer = CDI.current().beanContainer
        assertTrue(beanContainer.javaClass == io.quarkus.arc.impl.BeanManagerImpl::class.java)
        val beanManager = CDI.current().beanManager
        assertTrue(beanManager.javaClass == io.quarkus.arc.impl.BeanManagerImpl::class.java)
    }

    //@Note: 与CDI中定义的行为不同：
    // 即使声明类没有使用bean定义注释注释。 也可以发现生产者方法、字段和观察者方法。
    @Test
    fun testBeanDiscovery() {
        val beanManager = CDI.current().beanManager
        val beans = beanManager.getBeans(HasNotAnnotationBean::class.java)
        assertEquals(1, beans.size)
        val notBean = beanManager.getBeans(NotBean::class.java)
        assertTrue(notBean.isEmpty())
    }

    @Path("bean-discovery-test")
    internal class BeanResource {
        //注入一个普通的作用域bean是不够的，因为注入的是客户机代理而不是bean的上下文实例。
        @Inject
        lateinit var lazyBeanNormalScope: LazyBeanNormalScope

        //注入一个普通的作用域bean是不够的，因为注入的是客户机代理而不是bean的上下文实例。
        @Inject
        lateinit var lazyBeanPseudoScope: LazyBeanPseudoScope

        @GET
        @Path("lazy-bean-normal")
        @Produces(MediaType.TEXT_PLAIN)
        fun normal(): String {
            lazyBeanNormalScope.invoke()
            return "hello lazyBeanNormalScope"
        }

        @GET
        @Path("lazy-bean-pseudo")
        @Produces(MediaType.TEXT_PLAIN)
        fun pseudo(): String {
            lazyBeanPseudoScope.invoke()
            return "hello LazyBeanPseudoScope"
        }
    }


    // Bean默认都是懒加载的,在需要的时候进行加载
    @Test
    fun testBeanLazy() {
        //启动时没有调用所以BeanResource没有初始化，包含的两个@Inject也不会初始化
        assertFalse(lazyBeanNormalInit)
        assertFalse(lazyBeanPseudoInit)

        //调用一个空的类调用。会导致BeanResource初始化
        BeanResource::class.java.asBean()

        //Pre-udo的bean是会初始化的
        assertTrue(lazyBeanPseudoInit)


        //normal-scoped的bean只有方法调用时才会初始化
        //@Note:经过测试，发现虽然每次调用方法时会重新初始化。但是实例化的时候也会初始化一次,导致测试失败
        //assertFalse(lazyBeanNormalInit)

        //调用时会初始化
        RestAssured.given()
            .`when`().get("/bean-discovery-test/lazy-bean-normal")
            .then()
            .statusCode(200)
            .body(CoreMatchers.`is`("hello lazyBeanNormalScope"))

        assertTrue(lazyBeanNormalInit)
    }

    // Bean默认都是懒加载的,在需要的时候进行加载
//    @Test
    fun testBeanEager() {
//        assertFalse(lazyBeanNormalInit)
        assertTrue(lazyBeanPseudoInit)
        //直接初始化Bean
//        LazyBeanNormalScope::class.java.asBean()
        RestAssured.given()
            .`when`().get("/bean-discovery-test/lazy-bean-test")
            .then()
            .statusCode(200)
            .body(CoreMatchers.`is`("hello lazyBeanNormalScope"))
        assertTrue(lazyBeanNormalInit)
    }

}