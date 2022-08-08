package online.viestudio.viktor.client.event

interface EventSubscriber<T : Event> {

    fun doesAccept(event: Event): Boolean

    suspend fun onEvent(event: T)
}