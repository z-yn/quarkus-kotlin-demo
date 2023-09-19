package example.quarkus.stork.registration

import io.quarkus.runtime.StartupEvent
import io.vertx.mutiny.core.Vertx
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.event.Observes
import org.eclipse.microprofile.config.inject.ConfigProperty


@ApplicationScoped
class BlueService {
    @ConfigProperty(name = "blue-service-port", defaultValue = "9001")
    lateinit var port: String
    fun init(@Observes ev: StartupEvent?, vertx: Vertx) {
        vertx.createHttpServer()
            .requestHandler { req -> req.response().endAndForget("Hello from Blue!") }
            .listenAndAwait(port.toInt())
    }
}