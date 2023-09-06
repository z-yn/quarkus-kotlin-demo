package example.quarkus.data.client

import example.quarkus.annotation.INTERNAL
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
import org.jboss.resteasy.reactive.RestStreamElementType

@Path("/fruits")
@RegisterRestClient(configKey = "localhost-api")
@Default
interface FruitsClient {
    @INTERNAL
    @Path("/asFlow")
    @RestStreamElementType(MediaType.APPLICATION_JSON)
    fun listMulti(): Multi<Fruit>

    @GET
    @Path("")
    fun listAll(): Uni<List<Fruit>>
    @INTERNAL
    @Path("/asFlow")
    @RestStreamElementType(MediaType.APPLICATION_JSON)
    suspend fun listFlow(): Flow<Fruit>
}