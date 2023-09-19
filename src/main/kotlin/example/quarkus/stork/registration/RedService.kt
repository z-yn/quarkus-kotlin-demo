package example.quarkus.stork.registration

import jakarta.enterprise.context.ApplicationScoped
import org.eclipse.microprofile.config.inject.ConfigProperty

@ApplicationScoped
class RedService {
    @ConfigProperty(name = "red-service-port", defaultValue = "9000")
    lateinit var port: String
//    fun init(@Observes ev: StartupEvent?, vertx: Vertx) {
//        vertx.createHttpServer()
//            .requestHandler { req -> req.response().endAndForget("Hello from Red!") }
//            .listenAndAwait(port.toInt())
//    }
}