package online.viestudio.viktor.api

@Suppress("unused")
open class BaseUrlEndpoints(
    private val baseUrl: String,
) : Endpoints {

    final override val resourcesIndex: String = resolve("resources")

    final override fun resolve(path: String): String = "$baseUrl/$path"
}