package example.quarkus.resource

import example.quarkus.data.model.Fruit
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Path("flow")
@Produces(MediaType.APPLICATION_JSON)
class FlowDemoResource {
    @GET
    fun flow(): Flow<Fruit> {
        return kotlinx.coroutines.flow.flow {
            repeat(100) {
                emit(it)
            }
        }.map {
            delay(100)
            Fruit(it.toLong(), "fruit-$it")
        }
    }
}