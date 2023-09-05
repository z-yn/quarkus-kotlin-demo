package example.quarkus.data.client

import example.quarkus.data.model.Fruit
import io.smallrye.mutiny.Multi
import io.smallrye.mutiny.Uni
import jakarta.enterprise.inject.Default
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import kotlinx.coroutines.flow.Flow
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient

@Path("/fruits")
@RegisterRestClient(configKey = "localhost-api")
@Default

interface FruitsClient {

    @GET
    @Path("/asMulti")
    @Produces(MediaType.APPLICATION_JSON)
    fun listMulti(): Multi<List<Fruit>>

    @GET
    @Path("")
    fun listAll(): Uni<List<Fruit>>

    @GET
    @Path("/asFlow")
    fun listFlow(): Flow<Fruit>
}