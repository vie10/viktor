package online.viestudio.viktor.client.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

inline fun CoroutineScopeHolder.launchInScope(crossinline block: suspend CoroutineScope.() -> Unit) =
    scope.launch { block() }

interface CoroutineScopeHolder {

    val scope: CoroutineScope
    val context: CoroutineContext get() = scope.coroutineContext
}