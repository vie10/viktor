package online.viestudio.viktor.api.resources

import kotlinx.serialization.Serializable
import online.viestudio.viktor.api.hash.Hash

@Serializable
data class Resource(
    val path: String,
    val url: String,
    val hash: Hash,
    val sizeInBytes: Long,
)
