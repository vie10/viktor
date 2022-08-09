package online.viestudio.viktor.api

interface Endpoints {

    val resourcesIndex: String

    fun resolve(path: String): String
}