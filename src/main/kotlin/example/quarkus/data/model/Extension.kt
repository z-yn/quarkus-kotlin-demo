package example.quarkus.data.model

data class Extension(
    val id: String? = null,
    val name: String? = null,
    val shortName: String? = null,
    val keywords: List<String>? = null,
)