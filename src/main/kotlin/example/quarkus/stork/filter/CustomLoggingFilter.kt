package example.quarkus.stork.filter

import jakarta.ws.rs.ext.Provider
import org.jboss.logging.Logger
import org.jboss.resteasy.reactive.client.spi.ResteasyReactiveClientRequestContext

@Provider
class CustomLoggingFilter {
    private val LOG: Logger = Logger.getLogger(CustomLoggingFilter::class.java)

    fun filter(requestContext: ResteasyReactiveClientRequestContext) {
        LOG.infof("Resolved address by Stork: %s", requestContext.uri.toString())
    }
}