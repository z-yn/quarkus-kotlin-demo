package example.quarkus.annotation

import jakarta.ws.rs.HttpMethod

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention(AnnotationRetention.RUNTIME)
@HttpMethod("INTERNAL")
@MustBeDocumented
annotation class INTERNAL
