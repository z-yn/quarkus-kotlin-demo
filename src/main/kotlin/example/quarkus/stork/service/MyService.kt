package example.quarkus.stork.service

import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient

@RegisterRestClient(baseUri = "stork://my-service")
interface MyService {

    @GET
    @Path((""))
    @Produces(MediaType.TEXT_PLAIN)
    fun get(): String

    @GET
    @Path(("/actuator/health"))
    @Produces(MediaType.APPLICATION_JSON)
    fun health(): Any
}