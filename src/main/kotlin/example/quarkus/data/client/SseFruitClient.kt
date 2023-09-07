package example.quarkus.data.client

import example.quarkus.data.model.Fruit
import io.smallrye.mutiny.Multi
import jakarta.enterprise.inject.Default
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.core.MediaType
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient
import org.jboss.resteasy.reactive.RestStreamElementType

@Path("/sse")
@RegisterRestClient(configKey = "localhost-api")
@Default
interface SseFruitClient {
    @GET
    @Path("/multi")
    @RestStreamElementType(MediaType.APPLICATION_JSON)
    fun listMulti(): Multi<Fruit>

}