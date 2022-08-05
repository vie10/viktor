package online.viestudio.viktor.client.utils

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import mu.KLogger
import java.awt.Desktop
import java.net.URI
import kotlin.reflect.KProperty
import kotlin.system.measureTimeMillis

fun browseUri(uri: String) = Desktop.getDesktop().browse(URI.create(uri))

fun KLogger.measuredInfo(action: String, millis: Long) = info { "$action in $millis ms." }

fun KLogger.measuredDebug(action: String, millis: Long) = debug { "$action in $millis ms." }

fun KLogger.failedError(action: String, throwable: Throwable) = error(throwable) { "$action failed." }

fun KLogger.failedWarn(action: String, throwable: Throwable) = warn(throwable) { "$action failed." }

inline fun measureCatching(block: () -> Unit): Result<Long> {
    val result: Result<Unit>
    val measuredMillis = measureTimeMillis {
        result = runCatching { block() }
    }
    return result.mapCatching { measuredMillis }
}

operator fun <T> StateFlow<T>.getValue(owner: Any, kProperty: KProperty<*>) = value

operator fun <T> MutableStateFlow<T>.setValue(owner: Any, kProperty: KProperty<*>, newValue: T) = update { newValue }