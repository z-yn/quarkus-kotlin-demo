package example.quarkus

import io.quarkus.test.junit.QuarkusTest
import io.smallrye.mutiny.Multi
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.helpers.test.AssertSubscriber
import org.junit.jupiter.api.Test
import java.util.*


@QuarkusTest
class MutinyTest {
    @Test
    fun testBuilder() {
        val uni1 = Uni.createFrom().item("hello")
        val uni2 = uni1.onItem().transform { item: String -> "$item mutiny" }
        val uni3 = uni2.onItem().transform { obj: String ->
            obj.uppercase(Locale.getDefault())
        }
        uni3.subscribe().with {
            println(it)
        }
    }

    @Test
    fun testDemand() {
        val fixSubscriber = Multi.createFrom().range(0, 100)
            .capDemandsTo(50L)
            .subscribe().withSubscriber(AssertSubscriber.create())
        fixSubscriber.request(75L).assertNotTerminated()
        assert(fixSubscriber.items.size == 50)
        fixSubscriber.request(25L).assertCompleted()
        assert(fixSubscriber.items.size == 100)

        val customizedSubscriber = Multi.createFrom().range(0, 100)
            .capDemandsUsing { n: Long ->
                if (n > 1) {
                    return@capDemandsUsing (n.toDouble() * 0.75).toLong()
                } else {
                    return@capDemandsUsing n
                }
            }.subscribe()
            .withSubscriber(AssertSubscriber.create())
        customizedSubscriber.request(100L).assertNotTerminated()
        assert(customizedSubscriber.items.size == 75)
        customizedSubscriber.request(1L).assertNotTerminated()
        assert(customizedSubscriber.items.size == 94)
        customizedSubscriber.request(Long.MAX_VALUE).assertCompleted()
        assert(customizedSubscriber.items.size == 100)
    }
}