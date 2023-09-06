package example.quarkus.resource

import example.quarkus.data.model.Fruit
import io.smallrye.mutiny.Multi
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.core.MediaType
import org.jboss.resteasy.reactive.RestStreamElementType

@Path("mutiny")
class MutinyDemoResource {
    @GET
    @Path("/transform")
    fun demand(): Multi<Fruit> {
        return Multi.createFrom()
            .range(0, 100)
            .onItem()
            .transform {
                Thread.sleep(10)//假装做了一些费时间的转换
                Fruit(it.toLong(), "fruit-$it")
            }
    }

}