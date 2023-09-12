package example.quarkus.mutiny

import io.quarkus.test.junit.QuarkusTest
import io.smallrye.mutiny.Multi
import io.smallrye.mutiny.helpers.spies.Spy
import org.junit.jupiter.api.Test


/**
 * 当你需要跟踪哪些事件流入Uni或Multi时，间谍是有用的。
 * spy可以跟踪来自诸如onItem()， onFailure()，onSubscribe()
 *
 * 在Multi上跟踪onItem()事件需要将所有项目存储到一个列表中，这可能会在大流中产生内存不足的异常。
 * 在这种情况下，请考虑使用Spy。
 * onItem(multi, false)来获取一个不存储项目的间谍，但它仍然可以报告数据，
 * 例如接收到的事件的数量(参见spy. invocationcount())。
 */
@QuarkusTest
internal class SpyOnEventTest {
    @Test
    fun testSpy() {
        val multi = Multi.createFrom().items(1, 2, 3)
        val requestSpy = Spy.onRequest(multi)
        val itemSpy = Spy.onItem(multi)
        val completionSpy = Spy.onCompletion(requestSpy)
        completionSpy.subscribe().with { x: Int? -> println(x) }
        println("Number of requests: " + requestSpy.requestedCount())
        println("Completed? " + completionSpy.invoked())
    }

    @Test
    fun testSpyAll() {
        val multi = Multi.createFrom().items(1, 2, 3)
        val spy = Spy.globally(multi)

        spy.subscribe().with { x: Int? -> println(x) }

        println("Number of requests: " + spy.onRequestSpy().requestedCount())
        println("Cancelled? " + spy.onCancellationSpy().isCancelled)
        println("Failure? " + spy.onFailureSpy().lastFailure())
        println("Items: " + spy.onItemSpy().items())
    }
}