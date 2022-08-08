package online.viestudio.viktor.client.component

import io.ktor.util.collections.*
import mu.KLogger
import mu.KotlinLogging
import online.viestudio.viktor.client.event.Event
import online.viestudio.viktor.client.event.EventSubscriber
import online.viestudio.viktor.client.state.State
import online.viestudio.viktor.client.utils.measureCatching

abstract class BaseComponent(
    final override val name: String,
    final override val isAsync: Boolean = false,
) : Component {

    private val eventSubscribers = ConcurrentSet<EventSubscriber<Event>>()
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

    final override suspend fun onEvent(event: Event) {
        eventSubscribers.forEach {
            if (it.doesAccept(event)) it.onEvent(event)
        }
    }

    @Suppress("UNCHECKED_CAST")
    protected fun <T : Event> subscribe(subscriber: EventSubscriber<T>) {
        eventSubscribers.add(subscriber as EventSubscriber<Event>)
    }

    protected inline fun <reified T : Event> subscribe(crossinline block: suspend (T) -> Unit) = subscribe(
        object : EventSubscriber<T> {

            override fun doesAccept(event: Event): Boolean = event is T

            override suspend fun onEvent(event: T) = block(event)
        }
    )
}