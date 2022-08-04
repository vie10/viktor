val kotestVersion: String by project

plugins {
    kotlin("jvm")
}

dependencies {
    testImplementation("io.kotest", "kotest-runner-junit5", kotestVersion)
}