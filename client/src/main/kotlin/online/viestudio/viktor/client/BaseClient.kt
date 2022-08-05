package online.viestudio.viktor.client

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.newFixedThreadPoolContext
import mu.KLogger
import mu.KotlinLogging
import online.viestudio.viktor.client.component.Component
import online.viestudio.viktor.client.coroutines.launchInScope
import online.viestudio.viktor.client.state.State
import online.viestudio.viktor.client.utils.*
import java.util.concurrent.CancellationException

abstract class BaseClient(
    final override val name: String,
    scopeThreads: Int = Runtime.getRuntime().availableProcessors(),
) : Client {

    private val dispatcher = newFixedThreadPoolContext(scopeThreads, toString())
    private val _stateFlow = MutableStateFlow(State.Inactive)
    final override val stateFlow: StateFlow<State> = _stateFlow
    final override val state: State by stateFlow
    final override val scope: CoroutineScope = CoroutineScope(dispatcher)
    final override val log: KLogger = KotlinLogging.logger(toString())
    final override val components: Set<Component> get() = getKoin().getAll<Component>().toSet()

    override suspend fun start(): Boolean {
        if (state != State.Inactive) return false
        log.info { "Starting..." }
        _stateFlow.emit(State.Starting)
        return measureCatching {
            startComponents()
            onStart()
        }.onSuccess {
            log.measuredInfo("Started", it)
            _stateFlow.emit(State.Active)
        }.onFailure {
            log.failedError("Starting", it)
            stopComponents()
            _stateFlow.emit(State.Inactive)
        }.isSuccess
    }

    private suspend fun startComponents() {
        components.forEach {
            if (it.isAsync) {
                launchInScope { it.wrappedStart() }
            } else {
                it.wrappedStart()
            }
        }
    }

    private suspend fun Component.wrappedStart() {
        log.debug { "Starting..." }
        measureCatching { start() }.onSuccess {
            log.measuredDebug("Started", it)
        }.onFailure {
            log.failedWarn("Starting", it)
        }
    }

    protected open suspend fun onStart() {}

    override suspend fun stop(): Boolean {
        if (state != State.Active) return false
        log.info { "Stopping..." }
        _stateFlow.emit(State.Stopping)
        return measureCatching {
            stopComponents()
            onStop()
        }.onSuccess {
            log.measuredInfo("Stopped", it)
        }.onFailure {
            log.failedError("Stopping", it)
        }.also {
            scope.cancel(CancellationException("Stopping client"))
            dispatcher.close()
            _stateFlow.emit(State.Inactive)
        }.isSuccess
    }

    private suspend fun stopComponents() {
        components.forEach { it.wrappedStop() }
    }

    private suspend fun Component.wrappedStop() {
        log.debug { "Stopping..." }
        measureCatching { stop() }.onSuccess {
            log.measuredDebug("Stopped", it)
        }.onFailure {
            log.failedWarn("Stopping", it)
        }
    }

    protected open suspend fun onStop() {}

    final override fun toString(): String = "Client $name"
}