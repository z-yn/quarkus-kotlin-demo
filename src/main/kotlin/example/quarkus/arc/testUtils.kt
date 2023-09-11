package example.quarkus.arc

import jakarta.enterprise.inject.spi.CDI

internal fun <T> Class<T>.asBean(): T = CDI.current().select(this).get()
