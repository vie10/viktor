import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotestVersion: String by project
val kotestKoinVersion: String by project
val kotlinLoggingVersion: String by project
val logbackVersion: String by project
val coroutinesVersion: String by project
val koinVersion: String by project

plugins {
    kotlin("jvm")
}

dependencies {
    implementation("io.github.microutils", "kotlin-logging-jvm", kotlinLoggingVersion)
    implementation("ch.qos.logback", "logback-classic", logbackVersion)
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-core", coroutinesVersion)
    implementation("io.insert-koin", "koin-core", koinVersion)
}

dependencies {
    testImplementation("io.kotest", "kotest-runner-junit5", kotestVersion)
    testImplementation("io.kotest.extensions", "kotest-extensions-koin", kotestKoinVersion)
    testImplementation("io.insert-koin", "koin-test", koinVersion)
    testImplementation("io.insert-koin", "koin-test-junit4", koinVersion)
}

with(tasks) {
    withType<KotlinCompile> {
        kotlinOptions.freeCompilerArgs += listOf("-opt-in=kotlinx.coroutines.DelicateCoroutinesApi")
    }
}