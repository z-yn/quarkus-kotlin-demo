package example.quarkus.jandex

import jakarta.ws.rs.HttpMethod
import jakarta.ws.rs.core.MediaType
import org.jboss.resteasy.reactive.RestStreamElementType


@Target(AnnotationTarget.TYPE, AnnotationTarget.PROPERTY, AnnotationTarget.FIELD)
@MustBeDocumented
annotation class Label(val value:String)

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention(AnnotationRetention.RUNTIME)
@HttpMethod("INTERNAL")
@RestStreamElementType(MediaType.APPLICATION_JSON)
@MustBeDocumented
annotation class ComposedAnnotation

class DeclarationsAndTypes {

    @ComposedAnnotation
    fun test() {

    }

}