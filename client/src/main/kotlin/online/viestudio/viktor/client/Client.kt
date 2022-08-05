package online.viestudio.viktor.client

import kotlinx.coroutines.flow.StateFlow
import online.viestudio.viktor.client.component.Component
import online.viestudio.viktor.client.coroutines.CoroutineScopeHolder
import online.viestudio.viktor.client.log.Loggable
import online.viestudio.viktor.client.state.State
import org.koin.core.component.KoinComponent

interface Client : Loggable, CoroutineScopeHolder, KoinComponent {

    val name: String
    val stateFlow: StateFlow<State>
    val state: State
    val components: Set<Component>

    suspend fun start(): Boolean

    suspend fun stop(): Boolean
}