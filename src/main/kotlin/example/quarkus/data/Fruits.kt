package example.quarkus.data

import example.quarkus.data.model.Fruit
import io.smallrye.mutiny.Multi
import io.smallrye.mutiny.coroutines.awaitSuspending
import io.vertx.mutiny.pgclient.PgPool
import io.vertx.mutiny.sqlclient.Tuple
import jakarta.enterprise.inject.Default
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@Singleton
class Fruits {

    @Inject
    @field: Default
    lateinit var client: PgPool
    fun initDb() = runBlocking {
        client.query("DROP TABLE IF EXISTS fruits").execute().awaitSuspending()
        client.query("CREATE TABLE fruits (id SERIAL PRIMARY KEY, name TEXT NOT NULL)").execute().awaitSuspending()
        listOf("Apple", "Pear", "Orange", "Any").forEach {
            launch {
                client.preparedQuery("INSERT INTO fruits (name) VALUES ($1)").execute(Tuple.of(it)).awaitSuspending()
            }
        }
    }

    suspend fun listAll(): List<Fruit> {
        return client.query("SELECT id, name FROM fruits ORDER BY name ASC")
            .execute()
            .awaitSuspending()
            .map {
                Fruit(it.getLong("id"), it.getString("name"))
            }
    }

    fun listMulti(): Multi<Fruit> {
        return client.query("SELECT id, name FROM fruits ORDER BY name ASC")
            .execute()
            .onItem().transformToMulti { Multi.createFrom().iterable(it) }
            .onItem().transform {
                Fruit(it.getLong("id"), it.getString("name"))
            }
    }

    suspend fun listFlow(): Flow<Fruit> {
        return client.query("SELECT id, name FROM fruits ORDER BY name ASC")
            .execute()
            .awaitSuspending()
            .asFlow()
            .map {
                delay(500)
                Fruit(it.getLong("id"), it.getString("name"))
            }
    }

    suspend fun get(id: Int): Fruit {
        return client.preparedQuery("SELECT id, name FROM fruits WHERE id = $1")
            .execute(Tuple.of(id))
            .awaitSuspending()
            .map { Fruit(it.getLong("id"), it.getString("name")) }
            .first()
    }

    suspend fun add(fruit: Fruit): Long? {
        return client.preparedQuery("INSERT INTO fruits (name) VALUES ($1)")
            .execute(Tuple.of(fruit.name))
            .awaitSuspending()
            .map { it.getLong("id") }
            .firstOrNull()
    }
}