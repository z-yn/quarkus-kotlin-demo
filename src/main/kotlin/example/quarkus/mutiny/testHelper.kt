package example.quarkus.mutiny

import io.smallrye.mutiny.Uni
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

data class Page(
    val page: Int,
    val list: String
) {
    fun hasNext(): Boolean {
        return page < 5
    }
}

class PaginatedApi {
    fun getPage(idx: Int): CompletableFuture<List<String>> {
        return CompletableFuture.supplyAsync {
            if (idx > 5) emptyList() else listOf("$idx")
        }
    }

    fun getPageUni(i: Int): Uni<Page> {
        return Uni.createFrom().item(
            Page(
                i,
                "$i"
            )
        )
    }
}

class PollableDataSource() {
    private val idx = AtomicInteger()
    fun poll(): String {
        return "polled-${idx.getAndIncrement()}"
    }
}

fun newSingleThreadExecutor(threadName: String): ExecutorService {
    return Executors.newSingleThreadExecutor {
        Thread(it).apply {
            this.name = threadName
        }
    }
}