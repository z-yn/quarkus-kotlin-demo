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
    @Produces(MediaType.TEXT_PLAIN)
    operator fun invoke(): String {
        return service.get()
    }
}