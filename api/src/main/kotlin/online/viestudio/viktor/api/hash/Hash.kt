package online.viestudio.viktor.api.hash

import kotlinx.serialization.Serializable

@Serializable
data class Hash(
    val algorithm: String,
    val hex: String,
)
