package online.viestudio.viktor.core.os

import java.io.File

@Suppress("MemberVisibilityCanBePrivate", "unused")
object OS {

    const val UNDEFINED_VALUE = "undefined"

    val isMac = name.contains("mac os x", true)
    val isWindows get() = name.contains("windows", true)
    val isLinux get() = !isMac && !isWindows

    val name get() = System.getProperty("os.name") ?: UNDEFINED_VALUE
    val arch get() = System.getProperty("os.arch") ?: UNDEFINED_VALUE
    val userName get() = System.getProperty("user.name") ?: UNDEFINED_VALUE
    val userHome get() = File(System.getProperty("user.home"))
    val bitness = System.getProperty("sun.arch.data.model") ?: UNDEFINED_VALUE
    val classpathSeparator get() = System.getProperty("path.separator") ?: ":"

    fun resolveApplicationDir(applicationName: String): File {
        val dir = if (isWindows) {
            File(System.getenv("APPDATA") ?: System.getProperty("user.home"))
        } else if (isMac) {
            userHome.resolve("Library/Application Support/")
        } else {
            userHome
        }
        return dir.resolve(applicationName.replace(" ", ""))
    }
}