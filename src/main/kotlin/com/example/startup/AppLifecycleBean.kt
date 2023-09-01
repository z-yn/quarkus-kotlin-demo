package com.example.startup

import com.example.Fruits
import io.quarkus.runtime.ShutdownEvent
import io.quarkus.runtime.StartupEvent
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.event.Observes
import java.util.logging.Logger

@ApplicationScoped
class AppLifecycleBean(private val fruit: Fruits) {
    fun onStart(@Observes ev: StartupEvent?) {
        LOGGER.info("The application is starting...")
        fruit.initDb();
    }

    fun onStop(@Observes ev: ShutdownEvent?) {
        LOGGER.info("The application is stopping...")
    }

    companion object {
        private val LOGGER = Logger.getLogger("ListenerBean")
    }
}