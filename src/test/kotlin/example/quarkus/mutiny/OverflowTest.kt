package example.quarkus.mutiny

import example.quarkus.data.model.Fruit
import io.quarkus.test.junit.QuarkusTest
import io.smallrye.mutiny.Multi
import io.smallrye.mutiny.infrastructure.Infrastructure
import io.smallrye.mutiny.subscription.BackPressureFailure
import org.junit.jupiter.api.Test
import java.time.Duration


@QuarkusTest
class OverflowTest {

    @Test
    fun testBackPress() {
        Multi.createFrom().ticks()
            .every(Duration.ofMillis(10)).log()
            .emitOn(Infrastructure.getDefaultExecutor()).log()
            .map {
                Thread.sleep(1000)
                Fruit(it,"fruit-$it")
            }.log()
            .assertThat()
            .awaitFailure()
            .assertFailedWith(BackPressureFailure::class.java)
    }

    @Test
    fun testBuffer() {
        Multi.createFrom()
            .ticks().every(Duration.ofMillis(10)).log()
            .onOverflow().buffer(500).log()
            .emitOn(Infrastructure.getDefaultExecutor()).log()
            .map {
                Thread.sleep(1000)
                Fruit(it,"fruit-$it")
            }.log()
            .assertThat()
            .awaitFailure()
            //The overflow buffer is full, which is due to the upstream sending too many items w.r.t.
            // the downstream capacity and/or the downstream not consuming items fast enough
            .assertFailedWith(BackPressureFailure::class.java)
    }

//    @Test
    fun testDrop() {
        Multi.createFrom()
            .ticks().every(Duration.ofMillis(10)).log()
            .onOverflow().dropPreviousItems().log()
            .emitOn(Infrastructure.getDefaultExecutor()).log()
            .map {
                Thread.sleep(100)
                it
            }.log()
            .assertStart(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,166,44,55)
    }
}