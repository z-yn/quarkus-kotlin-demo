package example.quarkus.lifecycle

import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.context.Destroyed
import jakarta.enterprise.context.Initialized
import jakarta.inject.Singleton

@Singleton
class InitializedTest {
    @Initialized(ApplicationScoped::class)
    fun beforeStartup() {
        println("=== before startup event")
    }

    @Destroyed(ApplicationScoped::class)
    fun beforeDestroy() {
        println("=== before shutdown event")

    }

}
