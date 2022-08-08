@file:Suppress("unused")

package online.viestudio.viktor.client.security

import io.ktor.util.cio.*
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import java.io.File
import java.security.MessageDigest

suspend fun File.sha1Checksum() = readChannel().sha1Checksum()

suspend fun File.sha256Checksum() = readChannel().sha256Checksum()

suspend fun ByteReadChannel.sha1Checksum() = checksum("SHA1")

suspend fun ByteReadChannel.sha256Checksum() = checksum("SHA256")

suspend fun ByteReadChannel.checksum(algorithm: String): String {
    val md = MessageDigest.getInstance(algorithm)
    while (!isClosedForRead) {
        val packet = readRemaining(DEFAULT_BUFFER_SIZE.toLong())
        while (!packet.isEmpty) {
            val bytes = packet.readBytes()
            md.update(bytes)
        }
    }
    return md.digest().toHex()
}

fun ByteArray.toHex(): String = joinToString("") { eachByte -> "%02x".format(eachByte) }