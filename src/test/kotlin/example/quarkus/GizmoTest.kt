@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")
package example.quarkus

import io.quarkus.gizmo.ClassCreator
import io.quarkus.gizmo.ClassOutput
import io.quarkus.test.junit.QuarkusTest
import jdk.internal.org.objectweb.asm.Opcodes
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.io.path.Path


//https://github.com/quarkusio/gizmo/blob/main/USAGE.adoc
@QuarkusTest
class GizmoTest {

    object FileClassOutput: ClassOutput {
        override fun write(name: String, data: ByteArray) {
            val lastSlash = name.lastIndexOf('/')
            if (lastSlash>0) {
                File("build/classes/generated/${name.substring(0,lastSlash)}").mkdirs()
            }
            File("build/classes/generated/$name.class").writeBytes(data)
        }

    }
    fun addField(fooClazz:ClassCreator) {
        val myField = fooClazz.getFieldCreator("score", Int::class.java)
        myField.setModifiers(Opcodes.ACC_FINAL.or(Opcodes.ACC_PROTECTED) )
    }

    fun addMethod(fooClazz: ClassCreator) {
        val method = fooClazz.getMethodCreator("alwaysReturnFalse", Boolean::class.java)
        method.setModifiers(Opcodes.ACC_PRIVATE);
        method.returnValue(method.load(false))
    }
    @Test
    fun test() {
        val creator = ClassCreator.builder()
            .className("example.quarkus.gizmo.GeneratedClass")
            .build()
        addField(creator)
        addMethod(creator)
        creator.writeTo(FileClassOutput)
    }
}