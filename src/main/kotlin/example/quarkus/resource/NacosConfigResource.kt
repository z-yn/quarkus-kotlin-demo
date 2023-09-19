package example.quarkus.resource

import jakarta.enterprise.context.RequestScoped
import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import org.eclipse.microprofile.config.inject.ConfigProperty

@Path("nacos")
@RequestScoped
class NacosConfigResource {
    @Inject
    @ConfigProperty(name = "test.example.demo", defaultValue = "100")
    lateinit var value: jakarta.inject.Provider<Long>

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    fun value(): Long {
        return value.get();
    }
}