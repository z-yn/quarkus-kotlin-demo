package example.quarkus

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import org.hamcrest.CoreMatchers.*
import org.hamcrest.Matchers.greaterThan
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
                "$.size()", greaterThan(1),
                "[0].name", anyOf(
                    equalTo("Apple"),
                    equalTo("Pear"),
                    equalTo("Orange"),
                    equalTo("Any"),
                )
            )

    }

}