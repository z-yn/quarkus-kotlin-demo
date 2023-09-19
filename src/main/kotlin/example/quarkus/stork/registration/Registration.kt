package example.quarkus.stork.registration

import io.quarkus.runtime.StartupEvent
import io.vertx.ext.consul.ConsulClientOptions
import io.vertx.ext.consul.ServiceOptions
import io.vertx.mutiny.core.Vertx
import io.vertx.mutiny.ext.consul.ConsulClient
import jakarta.enterprise.event.Observes
import org.eclipse.microprofile.config.inject.ConfigProperty


class Registration {

    @ConfigProperty(name = "consul.host")
    lateinit var host: String

    @ConfigProperty(name = "consul.port")
    lateinit var port: String

    @ConfigProperty(name = "red-service-port", defaultValue = "9000")
    lateinit var red: String

    @ConfigProperty(name = "blue-service-port", defaultValue = "9001")
    lateinit var blue: String

    /**
     * Register our two services in Consul.
     *
     * Note: this method is called on a worker thread, and so it is allowed to block.
     */
    fun init(@Observes ev: StartupEvent, vertx: Vertx) {
        val client: ConsulClient = ConsulClient.create(vertx, ConsulClientOptions().setHost(host).setPort(port.toInt()))
        client.registerServiceAndAwait(
            ServiceOptions().setPort(red.toInt()).setAddress("localhost").setName("my-service").setId("red")
        )
        client.registerServiceAndAwait(
            ServiceOptions().setPort(blue.toInt()).setAddress("localhost").setName("my-service").setId("blue")
        )
    }
}