package example.quarkus.stork.service

import com.github.alex.quarkus.nacos.client.runtime.ServiceDiscovery
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient

@RegisterRestClient(baseUri = "stork://adapter-terraform")
@ServiceDiscovery
interface MyService {

    @GET
    @Path(("/actuator/health"))
    @Produces(MediaType.APPLICATION_JSON)
    fun health(): Any
}