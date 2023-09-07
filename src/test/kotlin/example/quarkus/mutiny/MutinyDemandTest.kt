package example.quarkus.mutiny

import example.quarkus.data.client.SseFruitClient
import io.quarkus.test.junit.QuarkusTest
import io.smallrye.mutiny.Multi
import io.smallrye.mutiny.helpers.test.AssertSubscriber
import jakarta.inject.Inject
import org.junit.jupiter.api.Test

@QuarkusTest
class MutinyDemandTest {
    @Test
    fun testFixedDemand() {
        val sub1 = Multi.createFrom()
            .range(0, 100)
            .capDemandsTo(50L) //固定大小每次返回50个
            .onRequest().invoke { it -> println(it) }
            .subscribe()
            .withSubscriber(AssertSubscriber.create())
        sub1.request(75L).assertNotTerminated()
        assert(sub1.items.size == 50) //第一次请求返回50个
        sub1.request(25L).assertCompleted()
        assert(sub1.items.size == 100) //第二次请求返回100个
    }

    @Test
    fun testDemandUsing() {
        val sub2 = Multi.createFrom().range(0, 100)
            .capDemandsUsing { n: Long ->
                if (n > 1) {
                    return@capDemandsUsing (n.toDouble() * 0.75).toLong()
                } else {
                    return@capDemandsUsing n
                }
            }.subscribe()
            .withSubscriber(AssertSubscriber.create())
        sub2.request(100L).assertNotTerminated()
        assert(sub2.items.size == 75)
        sub2.request(1L).assertNotTerminated()
        assert(sub2.items.size == 94)
        sub2.request(Long.MAX_VALUE).assertCompleted()
        assert(sub2.items.size == 100)
    }

    @Inject
    lateinit var sseClient: SseFruitClient

    @Test
    fun testSse() {
        val subscriber = sseClient.listMulti().assertThat()
        subscriber.awaitItems(10)
        println(subscriber.items)
        subscriber.awaitItems(10)
        println(subscriber.items)
        subscriber.awaitItems(10)
        println(subscriber.items)

    }
}