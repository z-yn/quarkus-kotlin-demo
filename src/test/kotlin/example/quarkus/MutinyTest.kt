package example.quarkus

import example.quarkus.mutiny.buildUni
import io.quarkus.test.junit.QuarkusTest
import io.smallrye.mutiny.Multi
import io.smallrye.mutiny.helpers.test.AssertSubscriber
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber
import org.junit.jupiter.api.Test


@QuarkusTest
class MutinyTest {

    fun testUni() {
        val subscriber = buildUni().subscribe().withSubscriber(UniAssertSubscriber.create())
        subscriber.assertCompleted().item
    }

    @Test
    fun testFixedDemand() {
        val sub1 = Multi.createFrom().range(0, 100)
            .capDemandsTo(50L) //固定大小每次返回50个
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
}