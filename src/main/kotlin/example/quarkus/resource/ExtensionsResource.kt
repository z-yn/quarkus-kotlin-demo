package example.quarkus.resource

import example.quarkus.data.client.ExtensionsService
import example.quarkus.data.client.FruitsClient
import example.quarkus.data.model.Extension
import io.smallrye.common.annotation.Blocking
import io.smallrye.mutiny.Uni
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import org.eclipse.microprofile.rest.client.inject.RestClient
import java.util.concurrent.CompletionStage

@Path("/extension")
@Produces(MediaType.APPLICATION_JSON)
class ExtensionsResource {
    @RestClient
    lateinit var extensionsService: ExtensionsService
    
    @GET
    @Path("/id/{id}")
    @Blocking
    fun id(id: String): Set<Extension> {
        return extensionsService.getById(id)
    }

    @GET
    @Path("/id-async/{id}")
    fun idAsync(id: String): CompletionStage<Set<Extension>> {
        return extensionsService.getByIdAsync(id)
    }

    @GET
    @Path("/id-uni/{id}")
    fun idUni(id: String): Uni<Set<Extension>> {
        return extensionsService.getByIdAsUni(id)
    }

    @GET
    @Path("/id-coroutine/{id}")
    suspend fun idCoroutine(id: String): Set<Extension> {
        return extensionsService.getByIdAsSuspended(id)
    }
}