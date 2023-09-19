package example.quarkus.stork.resource

import example.quarkus.stork.service.MyService
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import org.eclipse.microprofile.rest.client.inject.RestClient


@Path("/stork")
class FrontendApi {

    @RestClient
    lateinit var service: MyService

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    operator fun invoke(): Any {
        return service.health()
    }
}