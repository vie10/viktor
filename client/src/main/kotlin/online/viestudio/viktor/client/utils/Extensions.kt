@file:Suppress("unused")

package online.viestudio.viktor.client.utils

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import mu.KLogger
import java.awt.Desktop
import java.io.File
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

suspend inline fun <reified T> HttpClient.getBody(builder: HttpRequestBuilder.() -> Unit) = get(builder).body<T>()

suspend inline fun HttpClient.getBodyToFile(file: File, builder: HttpRequestBuilder.() -> Unit) = file.apply {
    get(builder).bodyAsChannel().writeTo(this)
}

suspend fun ByteReadChannel.writeTo(file: File) = file.write(this)

suspend fun File.write(channel: ByteReadChannel) = withContext(Dispatchers.IO) {
    parentFile.mkdirs()
    writeChannel().use {
        while (!channel.isClosedForRead) {
            val packet = channel.readRemaining(DEFAULT_BUFFER_SIZE.toLong())
            while (!packet.isEmpty) {
                val bytes = packet.readBytes()
                writeFully(bytes)
            }
        }
    }
}

operator fun <T> StateFlow<T>.getValue(owner: Any, kProperty: KProperty<*>) = value

operator fun <T> MutableStateFlow<T>.setValue(owner: Any, kProperty: KProperty<*>, newValue: T) = update { newValue }