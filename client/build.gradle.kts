val kotestVersion: String by project
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
}