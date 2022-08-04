package online.viestudio.viktor.client.utils

import mu.KLogger
import java.awt.Desktop
import java.net.URI

fun browseUri(uri: String) = Desktop.getDesktop().browse(URI.create(uri))

fun KLogger.measuredInfo(action: String, millis: Long) = info { "$action in $millis ms." }

fun KLogger.measuredDebug(action: String, millis: Long) = debug { "$action in $millis ms." }

fun KLogger.failedError(action: String, throwable: Throwable) = error(throwable) { "$action failed." }

fun KLogger.failedWarn(action: String, throwable: Throwable) = warn(throwable) { "$action failed." }