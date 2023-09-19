package example.quarkus.nacos_test

import io.smallrye.mutiny.Uni
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient

@Path("terraform")
@RegisterRestClient(baseUri = "stork://adapter-terraform")
interface NacosClientTest {

    @GET
    @Path("/dataModel")
    fun getDataModel(): Uni<Any?>
}