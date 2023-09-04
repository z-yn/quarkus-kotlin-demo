package example.quarkus

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured
import org.hamcrest.CoreMatchers
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test

@QuarkusTest
class ExtensionsResourceTest {
    @Test
    fun testExtensionsIdEndpoint() {
        RestAssured.given()
            .`when`()["/extension/id/io.quarkus:quarkus-rest-client-reactive"]
            .then()
            .statusCode(200)
            .body(
                "$.size()", CoreMatchers.`is`(1),
                "[0].id", CoreMatchers.`is`("io.quarkus:quarkus-rest-client-reactive"),
                "[0].name", CoreMatchers.`is`("REST Client Reactive"),
                "[0].keywords.size()", Matchers.greaterThan(1),
                "[0].keywords", CoreMatchers.hasItem("rest-client")
            )
    }

    @Test
    fun testExtensionIdAsyncEndpoint() {
        RestAssured.given()
            .`when`()["/extension/id-async/io.quarkus:quarkus-rest-client-reactive"]
            .then()
            .statusCode(200)
            .body(
                "$.size()", CoreMatchers.`is`(1),
                "[0].id", CoreMatchers.`is`("io.quarkus:quarkus-rest-client-reactive"),
                "[0].name", CoreMatchers.`is`("REST Client Reactive"),
                "[0].keywords.size()", Matchers.greaterThan(1),
                "[0].keywords", CoreMatchers.hasItem("rest-client")
            )
    }

    @Test
    fun testExtensionIdMutinyEndpoint() {
        RestAssured.given()
            .`when`()["/extension/id-uni/io.quarkus:quarkus-rest-client-reactive"]
            .then()
            .statusCode(200)
            .body(
                "$.size()", CoreMatchers.`is`(1),
                "[0].id", CoreMatchers.`is`("io.quarkus:quarkus-rest-client-reactive"),
                "[0].name", CoreMatchers.`is`("REST Client Reactive"),
                "[0].keywords.size()", Matchers.greaterThan(1),
                "[0].keywords", CoreMatchers.hasItem("rest-client")
            )
    }

    @Test
    fun testExtensionIdCoroutineEndpoint() {
        RestAssured.given()
            .`when`()["/extension/id-coroutine/io.quarkus:quarkus-rest-client-reactive"]
            .then()
            .statusCode(200)
            .body(
                "$.size()", CoreMatchers.`is`(1),
                "[0].id", CoreMatchers.`is`("io.quarkus:quarkus-rest-client-reactive"),
                "[0].name", CoreMatchers.`is`("REST Client Reactive"),
                "[0].keywords.size()", Matchers.greaterThan(1),
                "[0].keywords", CoreMatchers.hasItem("rest-client")
            )
    }
}