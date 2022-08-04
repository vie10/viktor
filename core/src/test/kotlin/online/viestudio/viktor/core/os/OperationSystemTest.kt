package online.viestudio.viktor.core.os

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import online.viestudio.viktor.core.os.OperationSystem.arch
import online.viestudio.viktor.core.os.OperationSystem.isLinux
import online.viestudio.viktor.core.os.OperationSystem.isMac
import online.viestudio.viktor.core.os.OperationSystem.isWindows
import online.viestudio.viktor.core.os.OperationSystem.name
import online.viestudio.viktor.core.os.OperationSystem.resolveApplicationDir
import online.viestudio.viktor.core.os.OperationSystem.userHome
import java.io.File

class OperationSystemTest : BehaviorSpec({

    given("this system") {
        `when`("get name") {
            then("returns the name of this system") {
                name.shouldBe(System.getProperty("os.name"))
            }
        }
        `when`("get arch") {
            then("returns the arch of this system") {
                arch.shouldBe(System.getProperty("os.arch"))
            }
        }

        `when`("resolve application dir") {
            then("is under \"Application Support\" dir").config(enabled = isMac) {
                resolveApplicationDir("test").parentFile.name.contains("application support", true)
            }

            then("is under user home dir").config(enabled = isLinux) {
                resolveApplicationDir("test").parentFile.shouldBe(userHome)
            }
            then("is under appdata dir").config(enabled = isWindows) {
                resolveApplicationDir("test").parentFile.shouldBe(File(System.getenv("APPDATA")))
            }
        }
    }
    given("this system name") {
        `when`("get isWindows") {
            then("returns true if contains \"windows\"") {
                isWindows.shouldBe(name.contains("windows", true))
            }
        }
        `when`("get isMac") {
            then("returns true if contains \"mac os x\"") {
                isMac.shouldBe(name.contains("mac os x", true))
            }
        }
        `when`("get isLinux") {
            then("returns true if isn't windows or mac") {
                isLinux.shouldBe(!isWindows && !isMac)
            }
        }
    }
})