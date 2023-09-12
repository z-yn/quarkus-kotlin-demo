package example.quarkus

import io.quarkus.runtime.Quarkus
import io.quarkus.runtime.QuarkusApplication
import io.quarkus.runtime.annotations.QuarkusMain


@QuarkusMain
object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        println("Running main method")
        Quarkus.run(MyApp::class.java, *args)
    }

    class MyApp : QuarkusApplication {
        @Throws(Exception::class)
        override fun run(vararg args: String): Int {
            println("Do startup logic here")
            Quarkus.waitForExit()
            return 0
        }
    }

}