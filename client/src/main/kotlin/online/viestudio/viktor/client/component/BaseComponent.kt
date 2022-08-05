package online.viestudio.viktor.client.component

import mu.KLogger
import mu.KotlinLogging
import online.viestudio.viktor.client.state.State
import online.viestudio.viktor.client.utils.measureCatching

abstract class BaseComponent(
    final override val name: String,
    final override val isAsync: Boolean = false,
) : Component {

    final override val log: KLogger = KotlinLogging.logger(toString())

    final override var state: State = State.Inactive

    final override fun toString(): String = "Component $name"

    final override suspend fun start() {
        if (state != State.Inactive) return
        state = State.Starting
        measureCatching { onStart() }.onSuccess {
            state = State.Active
        }.onFailure {
            state = State.Inactive
        }.getOrThrow()
    }

    protected open suspend fun onStart() {}

    final override suspend fun stop() {
        if (state != State.Active) return
        state = State.Stopping
        measureCatching { onStop() }.also { state = State.Inactive }.getOrThrow()
    }

    protected open suspend fun onStop() {}
}