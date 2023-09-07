package example.quarkus

import example.quarkus.jandex.*
import io.quarkus.test.junit.QuarkusTest
import org.jboss.jandex.*
import org.jboss.jandex.AnnotationTarget
import org.jboss.resteasy.reactive.RestStreamElementType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*


@QuarkusTest
internal class JandexTest {
    @Test
    fun indexClass() {
        val index = Index.of(MutableMap::class.java) //索引类Map
        val clazz1 = index.getClassByName(DotName.createSimple("java.util.Map")) //可以从索引中找到类
        assertTrue(clazz1 != null)
        assertTrue(clazz1.methods().isNotEmpty())
        clazz1.methods().forEach { //
            println(it)
        }
        val clazz2 = index.getClassByName(DotName.createSimple("java.util.Set")) //没有索引所以找不到
        assertTrue(clazz2 == null)
    }

    @Test
    fun searchAnnotations() {
        val indexer = Indexer().apply {
            indexClass(Thread::class.java)
            indexClass(String::class.java)
        }
        val index = indexer.complete()

        val annotations = index.getAnnotations(DotName.createSimple("java.lang.Deprecated")) //根据索引查找注解
        assertTrue(annotations.isNotEmpty())
        for (annotation in annotations) {
            when (annotation.target().kind()) {
                AnnotationTarget.Kind.METHOD -> println("Deprecated Method: ${annotation.target()}")
                AnnotationTarget.Kind.CLASS -> println("Deprecated Class: ${annotation.target()}")
                AnnotationTarget.Kind.FIELD -> println("Deprecated Field: ${annotation.target()}")
                AnnotationTarget.Kind.METHOD_PARAMETER -> println("Deprecated Method parameter: ${annotation.target()}")
                AnnotationTarget.Kind.TYPE -> println("Deprecated Type: ${annotation.target()}")
                AnnotationTarget.Kind.RECORD_COMPONENT -> println("Deprecated Record component: ${annotation.target()}")
                null -> println("null")
            }
        }
    }

    @Test
    fun generics() {
        val index = Index.of(Collections::class.java)

        val clazz = index.getClassByName(DotName.createSimple("java.util.Collections"))

        val listType = Type.create(DotName.createSimple("java.util.List"), Type.Kind.CLASS)
        val sort = clazz.method("sort", listType) //查找method Collections.sort(java.util.List)

        val type = sort.parameterTypes()[0]// void sort(java.util.List<T> list)
            .asParameterizedType() // List<T extends Comparable<? super T>>
            .arguments()[0].asTypeVariable() // T extends Comparable<? super T>
            .bounds()[0] // Comparable<? super T>
        println(type)

        val b: Type = type.asParameterizedType() // Comparable<? super T>
            .arguments()[0].asWildcardType() // ? super T
            .superBound() // T
        println(b)
    }

    @Test
    fun typeAnnotationJavaDemo() {
        val indexer = Indexer()
        indexer.indexClass(JandexJavaDemo::class.java)
        indexer.indexClass(Label::class.java)
        val index = indexer.complete()
        val field = index.getClassByName(JandexJavaDemo::class.java).field("names")
        val nameValue = field.type().asParameterizedType() // Map<Integer, List<@Label("Name") String>>
            .arguments()[1].asParameterizedType() // List<@Label("Name") String>
            .arguments()[0] // @Label("Name") String
            .annotations()[0] // @Label("Name")
            .value()
        assertEquals("Name", nameValue.value());
        val label = DotName.createSimple("example.quarkus.jandex.Label")
        val annotations = index.getAnnotations(label)
        assertEquals(1, annotations.size)
        for (annotation in annotations) {
            if (annotation.target().kind() == AnnotationTarget.Kind.TYPE) {
                val typeTarget = annotation.target().asType()
                println("Type usage is located within: " + typeTarget.enclosingTarget())
                println("Usage type: " + typeTarget.usage())
                println("Target type: " + typeTarget.target())
                println(
                    "Expected target? " + (typeTarget.enclosingTarget().asField().type()
                        .asParameterizedType().arguments()[1]
                        .asParameterizedType().arguments()[0]
                            === typeTarget.target())
                )
            }
        }
    }

    @Test
    //与Kotlin不兼容,不支持type annotation
    //https://github.com/smallrye/jandex/issues/284
    fun typeAnnotations() {
        val indexer = Indexer()
        indexer.indexClass(JandexKotlinDemo::class.java)
        indexer.indexClass(Label::class.java)
        val index = indexer.complete()
        val field = index.getClassByName(JandexKotlinDemo::class.java).field("names")
        val stringField = field.type()
            .asParameterizedType() // Map<Integer, ? extends List<? extend @Label("Name") String>>
            .arguments()[1].asWildcardType() // ? extends List<? extends @Label("Name") String>
            .extendsBound().asParameterizedType()// List<? extends @Label("Name") String>
            .arguments()[0] // @Label("Name") String

        assertTrue(stringField.annotations().isEmpty())

//        index.printAnnotations()
//        index.printSubclasses()
        val annotations = index.getAnnotations(DotName.createSimple("example.quarkus.jandex.Label"))
        assertTrue(annotations.isNotEmpty())
        for (annotation in annotations) {
            if (annotation.target().kind() == AnnotationTarget.Kind.TYPE) {
                val typeTarget = annotation.target().asType()
                println("Type usage is located within: " + typeTarget.enclosingTarget())
                println("Usage type: " + typeTarget.usage())
                println("Target type: " + typeTarget.target())
                println(
                    "Expected target? " + (typeTarget.enclosingTarget().asField().type()
                        .asParameterizedType().arguments()[1]
                        .asParameterizedType().arguments()[0]
                            === typeTarget.target())
                )
            }
        }
    }

    @Test
    fun indexToFile() {
        FileOutputStream("index.idx").use { out ->
            val writer = IndexWriter(out)
            writer.write(Indexer().apply {
                indexClass(MutableMap::class.java)
            }.complete())
        }

        var index: Index
        FileInputStream("index.idx").use { input ->
            val reader = IndexReader(input)
            index = reader.read()
        }

        val clazz = index.getClassByName(DotName.createSimple("java.util.Map"))

        for (method in clazz.methods()) {
            println(method)
        }

    }

    @Test
    fun testComposedAnnotation() {
        val index = Indexer().apply {
            indexClass(DeclarationsAndTypes::class.java)
            indexClass(ComposedAnnotation::class.java)
            indexClass(RestStreamElementType::class.java)
        }.complete()
        val annotations = index.getAnnotations(ComposedAnnotation::class.java.name)
        assertEquals(1, annotations.size)
        val type = index.getClassByName(DeclarationsAndTypes::class.java.name)
        val method = type.method("test")
        val mutableList = method.annotations()
        println(mutableList)
    }
}