package example.quarkus.mutiny

import io.quarkus.test.junit.QuarkusTest
import io.smallrye.mutiny.Multi
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.subscription.UniEmitter
import org.junit.jupiter.api.Test
import java.time.Duration
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread

//Uni<T>代表一个只能发出一个元素或一个失败事件的流
//Uni<T>非常适合表示异步操作，例如远程过程调用、HTTP请求或产生单个结果的操作。
@QuarkusTest
class MutinyUniTest {

    @Test
    fun testUniBuilder() {
        //支持从元素创建
        Uni.createFrom().item(1)
            .assertIs(1)

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

    @Test
    fun testTransform() {
        Uni.createFrom().item("hello")
            .onItem().transform { it.uppercase() } // shortcut --> map {}
            .map { "$it-WORLD" }
            .assertIs("HELLO-WORLD")

        Uni.createFrom().item("hello")
            .onItem().transformToUni { it ->  // shortcut --> flatmap {} or chain {}
                Uni.createFrom().item(it).map { it.uppercase() }
            }.flatMap { Uni.createFrom().item("$it-WORLD") }
            .assertIs("HELLO-WORLD")

        Uni.createFrom().item("hello")
            .onItem().transformToMulti {
                Multi.createFrom().iterable(it.toCharArray().asIterable())
            }.assertIs('h', 'e', 'l', 'l', 'o')
    }

    @Test
    fun testNull() {
        Uni.createFrom().nullItem<String>()
            .onItem().ifNull().continueWith("hello") //如果是空则返回Hello
            .assertIs("hello")

        Uni.createFrom().nullItem<String>()
            .onItem().ifNull().switchTo { Uni.createFrom().item("hello") }
            .onItem().ifNotNull().transform { it.uppercase() }
            .assertIs("HELLO")
        Uni.createFrom().nullItem<String>()
            .onItem().ifNull().failWith {NullPointerException("Boom!") }
            .assertFailed().assertFailedWith(NullPointerException::class.java)
    }

    @Test
    fun testDelay() {
       Uni.createFrom().item("hello")
            .onItem().delayIt().by(Duration.ofMillis(10))
           .assertIs("hello")

       Uni.createFrom().item("hello")
            .onItem().delayIt().until{
               uniDelay(100)
            }.assertIs("hello")
    }

    @Test
    fun testEmitter() {
        //emitOn接收来自上游的事件(项目、完成、失败)，并在给定执行器的下游线程上重播它们。
        // 因此，它会影响后续操作符的执行位置(直到使用另一个emitOn):
      Uni.createFrom().emitter { emitter ->
            thread(name = "Emitter") {
                emitter.complete(
                    "hello from"
                )
            }
        }.map { "$it ${Thread.currentThread().name}" }
          .assertIs("hello from Emitter")

        Uni.createFrom().item {
            "hello from"
        }.emitOn(newSingleThreadExecutor("Emitter"))
            .map { "$it ${Thread.currentThread().name}" }
            .assertIs("hello from Emitter")

     //runSubscriptionOn应用于订阅进程。
    // 它请求上游在给定执行器的线程上运行它的订阅(在自己的上游调用subscribe方法):
        Uni.createFrom().item {
            "hello from"
        }.emitOn(newSingleThreadExecutor("Emitter"))
            .runSubscriptionOn(newSingleThreadExecutor("Subscribe"))
            .map { "$it ${Thread.currentThread().name}" }
            .assertIs("hello from Emitter")
    }

}