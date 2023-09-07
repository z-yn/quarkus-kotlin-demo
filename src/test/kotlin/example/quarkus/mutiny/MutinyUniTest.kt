package example.quarkus.mutiny

import io.quarkus.test.junit.QuarkusTest
import io.smallrye.mutiny.Uni
import org.junit.jupiter.api.Test
import java.util.concurrent.atomic.AtomicInteger

//Uni<T>代表一个只能发出一个元素或一个失败事件的流
//Uni<T>非常适合表示异步操作，例如远程过程调用、HTTP请求或产生单个结果的操作。
@QuarkusTest
class MutinyUniTest {

    @Test
    fun testUniBuilder() {
        //支持从元素创建
        Uni.createFrom().item(1)
            .onItem().transform { "hello-$it" }
            .assertIs("hello-1")

        //支持从supplier创建
        val counter = AtomicInteger()
        Uni.createFrom()
            .item { counter.getAndIncrement() }
            .assertIs(0)

        //支持直接创建一个抛出异常的uni
        Uni.createFrom().failure<Int>(Exception("boom"))
            .assertFailed()

        //支持创建一个Void
        Uni.createFrom().nullItem<Unit>()
            .assertIs(null)

        //支持使用emitter创建
        Uni.createFrom().emitter { it.complete(1) }.assertIs(1)

    }

}