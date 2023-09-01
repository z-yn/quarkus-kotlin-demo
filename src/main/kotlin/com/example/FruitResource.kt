package com.example

import com.example.dto.Fruit
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam


@Path("fruits")
class FruitResource(private val fruits: Fruits) {
    @GET
    suspend fun listAll(): List<Fruit> = fruits.listAll()

    @GET
    @Path("{id}")
    suspend fun get(@PathParam("id") id: Int): Fruit = fruits.get(id)

    @POST
    suspend fun add(fruit: Fruit): Long? = fruits.add(fruit)

}