package example.quarkus.resource

import example.quarkus.data.Fruits
import example.quarkus.data.client.FruitsClient
import example.quarkus.data.model.Fruit
import io.smallrye.mutiny.Multi
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import kotlinx.coroutines.flow.Flow
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.jboss.resteasy.reactive.RestStreamElementType


@Path("fruits")
@Produces(MediaType.APPLICATION_JSON)
class FruitResource(private val fruits: Fruits) {

    @RestClient
    lateinit var fruitClient: FruitsClient

    @GET
    suspend fun listAll(): List<Fruit> = fruits.listAll()

    @GET
    @Path("/asMulti")
    @RestStreamElementType(MediaType.APPLICATION_JSON)
    fun listMulti(): Multi<Fruit> = fruits.listMulti()

    @GET
    @Path("/asFlow")
    suspend fun listFlow(): Flow<Fruit> = fruits.listFlow()

    @GET
    @Path("{id}")
    suspend fun get(@PathParam("id") id: Int): Fruit = fruits.get(id)

    @POST
    suspend fun add(fruit: Fruit): Long? = fruits.add(fruit)

    @GET
    @Path("/byClient/asMulti")
    fun fromRestClient(): Multi<Fruit> = fruitClient.listMulti()

    @GET
    @Path("/byClient/asFlow")
    suspend fun fromRestClientFlow(): List<Fruit> = fruitClient.listFlow()

}