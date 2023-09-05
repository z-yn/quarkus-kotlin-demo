package example.quarkus.data.client

import example.quarkus.data.model.Fruit
import io.smallrye.mutiny.Multi
import io.smallrye.mutiny.Uni
import jakarta.enterprise.inject.Default
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient
import org.jboss.resteasy.reactive.RestStreamElementType

@Path("/fruits")
@RegisterRestClient(configKey = "localhost-api")
@Default
@Produces(MediaType.APPLICATION_JSON)
interface FruitsClient {

    @GET
    @RestStreamElementType(MediaType.APPLICATION_JSON)
    @Path("/asMulti")
    fun listMulti(): Multi<Fruit>

    @GET
    @Path("")
    fun listAll(): Uni<List<Fruit>>

    @GET
    @Path("/asFlow")
    suspend fun listFlow(): List<Fruit>
}