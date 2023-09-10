package example.quarkus.mutiny

import io.smallrye.mutiny.Context
import io.smallrye.mutiny.Multi
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.helpers.test.AssertSubscriber
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber
import java.time.Duration

fun <T> Uni<T>.assertThat(): UniAssertSubscriber<T> {
    return this.subscribe().withSubscriber(UniAssertSubscriber.create())
}

fun <T> Uni<T>.assertIs(expected: T): UniAssertSubscriber<T> {
    return this.subscribe().withSubscriber(UniAssertSubscriber.create())
        .awaitItem()
        .assertItem(expected)
}

fun <T> Uni<T>.assertFailed(): UniAssertSubscriber<T> {
    return this.subscribe()
        .withSubscriber(UniAssertSubscriber.create())
        .assertFailed()
}


fun <T> Multi<T>.assertThat(): AssertSubscriber<T> {
    return this.subscribe().withSubscriber(AssertSubscriber.create())
}

fun <T> Multi<T>.assertFail(): AssertSubscriber<T> {
    return this.subscribe().withSubscriber(AssertSubscriber.create()).awaitFailure()
}

fun <T> AssertSubscriber<T>.assertStart(vararg expected: T): AssertSubscriber<T> {
    return this.awaitNextItems(expected.size)
        .assertItems(*expected)
}


fun <T> Multi<T>.assertStart(vararg expected: T): AssertSubscriber<T> {
    return this.subscribe()
        .withSubscriber(AssertSubscriber.create())
        .assertStart(*expected)
}

fun <T> Multi<T>.assertEmpty() {
    assert(
        this.subscribe()
            .withSubscriber(AssertSubscriber.create())
            .request(Long.MAX_VALUE)
            .assertCompleted()
            .items.isEmpty()
    )
}

fun <T> Multi<T>.withCtx(init: Context.() -> Unit): AssertSubscriber<T> {
    return this.subscribe()
        .withSubscriber(AssertSubscriber.create<T>().apply {
            context().init()
        })
}

fun <T> AssertSubscriber<T>.assertIs(vararg expected: T): AssertSubscriber<T> {
    return this.request(Long.MAX_VALUE)
        .awaitCompletion()
        .assertItems(*expected)
}

fun <T> Multi<T>.assertIs(vararg expected: T): AssertSubscriber<T> {
    return this.subscribe()
        .withSubscriber(AssertSubscriber.create())
        .assertIs(*expected)
}

fun uniDelay(mills: Long): Uni<Unit> =
    Uni.createFrom().nullItem<Unit>().onItem().delayIt().by(Duration.ofMillis(mills))

object MutinyBuildException : RuntimeException("BOOM")