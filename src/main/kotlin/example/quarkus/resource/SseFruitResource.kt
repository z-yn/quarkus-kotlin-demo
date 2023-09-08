package example.quarkus.resource

import example.quarkus.data.model.Fruit
import io.smallrye.mutiny.Multi
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.core.MediaType
import org.jboss.resteasy.reactive.RestStreamElementType

@Path("sse")
class SseFruitResource {
    @GET
    @Path("/multi")
    @RestStreamElementType(MediaType.APPLICATION_JSON)
    fun demand(): Multi<Fruit> {
        return Multi.createFrom()
            .range(0, 100)
            .map {
                Thread.sleep(10)
                println("@${System.currentTimeMillis()} produced fruit-$it")
                Fruit(it.toLong(), "fruit-$it")
            }.onRequest().invoke { it ->
                println("@${System.currentTimeMillis()} >>> requested $it")
            }
    }
}