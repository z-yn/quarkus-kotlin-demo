package example.quarkus.mutiny

import io.smallrye.mutiny.Multi
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.helpers.test.AssertSubscriber
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber

fun <T> Uni<T>.assertThat(): UniAssertSubscriber<T> {
    return this.subscribe().withSubscriber(UniAssertSubscriber.create())
}

fun <T> Uni<T>.assertIs(expected: T): UniAssertSubscriber<T> {
    return this.subscribe().withSubscriber(UniAssertSubscriber.create()).awaitItem()
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

fun <T> Multi<T>.assertStart(vararg expected: T): AssertSubscriber<T> {
    return this.subscribe()
        .withSubscriber(AssertSubscriber.create())
        .awaitNextItems(expected.size)
        .assertItems(*expected)
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

fun <T> Multi<T>.assertIs(vararg expected: T): AssertSubscriber<T> {
    return this.subscribe()
        .withSubscriber(AssertSubscriber.create())
        .request(Long.MAX_VALUE)
        .assertItems(*expected)
}

object MutinyBuildException : RuntimeException("BOOM")