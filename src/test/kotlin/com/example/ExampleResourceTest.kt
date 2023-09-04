package com.example

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test

@QuarkusTest
class ExampleResourceTest {

    @Test
    fun testHelloEndpoint() {
        given()
            .`when`().get("/hello")
            .then()
            .statusCode(200)
            .body(`is`("Hello from RESTEasy Reactive"))
    }

    @Test
    fun testFruits() {
        given()
            .`when`()["/fruits"]
            .then()
            .statusCode(200)
            .body(
                "$.size()", `is`(4),
                "[*].name", `is`("io.quarkus:quarkus-rest-client-reactive"),
                "[0].name", `is`("REST Client Reactive"),
                "[0].keywords.size()", Matchers.greaterThan(1),
                "[0].keywords", CoreMatchers.hasItem("rest-client")
            )

    }

}