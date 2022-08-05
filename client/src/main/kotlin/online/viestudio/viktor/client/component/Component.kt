package online.viestudio.viktor.client.component

import online.viestudio.viktor.client.log.Loggable
import online.viestudio.viktor.client.state.State

interface Component : Loggable {

    val state: State
    val name: String
    val isAsync: Boolean

    suspend fun start()

    suspend fun stop()
}