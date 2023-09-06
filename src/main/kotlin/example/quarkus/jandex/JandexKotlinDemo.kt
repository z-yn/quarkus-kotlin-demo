package example.quarkus.jandex

class JandexKotlinDemo{
    @field:Label("sss")
    var names: Map<Int, List<@Label("Name") String>>? = null
}