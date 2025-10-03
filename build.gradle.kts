import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar.Companion.shadowJar
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    kotlin("multiplatform")
    id("dev.reformator.stacktracedecoroutinator")
    id("com.gradleup.shadow")
    kotlin("plugin.serialization")
//    application
}

group = "moe.nikky"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

//application {
//    mainClass = "moe.nikky.vrcobs.MainKt"
//}

stacktraceDecoroutinator {
    enabled = false
}

kotlin {
    jvmToolchain(21)
    jvm {
//        @OptIn(ExperimentalKotlinGradlePluginApi::class)
//        binaries {
//            executable {
//                mainClass = "MainKt"
//            }
//        }
        compilerOptions {
//            optIn.add("kotlin.time.ExperimentalTime")
        }
//        withJava()
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        mainRun {
            mainClass = "MainKt"
        }
    }
    mingwX64 {
        compilerOptions {
//            optIn.add("kotlin.time.ExperimentalTime")
        }
        binaries {
            executable() {
                this.baseName = "obsremote"
                this.entryPoint = "main"
                runTaskProvider?.orNull?.also { runTask ->
                    val args = providers.gradleProperty("runArgs")
                    runTask.argumentProviders.add {
                        args.orNull?.let { listOf(it) }/*?.split(' ')*/ ?: emptyList()
                    }
                }
            }
        }
    }
    sourceSets {
        commonMain.dependencies {
            implementation(Kotlin.stdlib)
//            implementation(Kotlin.stdlib.common)

            implementation(KotlinX.coroutines.core)
            implementation(KotlinX.serialization.json)
            implementation(KotlinX.datetime)

            implementation("io.ktor:ktor-client-core:_")
            implementation("io.ktor:ktor-client-cio:_")
            implementation("io.ktor:ktor-client-content-negotiation:_")
            implementation("io.ktor:ktor-serialization-kotlinx-json:_")

            implementation("com.github.ajalt.clikt:clikt:_")
            implementation("com.github.ajalt.clikt:clikt-markdown:_")

            // https://github.com/Rejeq/ktobs
            implementation("io.github.rejeq:ktobs-core:_")
            implementation("io.github.rejeq:ktobs-ktor:_")

            implementation("com.squareup.okio:okio:_")
//            implementation("com.saveourtool:okio-extras:_")
            implementation("com.saveourtool.okio-extras:okio-extras:_")

            implementation("io.github.oshai:kotlin-logging:_")


            implementation("com.github.ajalt.mordant:mordant:_")
            implementation("com.github.ajalt.mordant:mordant-coroutines:_")
            implementation("com.github.ajalt.mordant:mordant-markdown:_")
        }
        mingwMain.dependencies {
//            implementation("org.jetbrains.kotlinx:kotlinx-datetime:_")
//            implementation("com.fleeksoft.ksoup:ksoup:_")
//            implementation("com.squareup.okio:okio:_")
            implementation("io.ktor:ktor-client-winhttp:_")
//            implementation("io.github.oshai:kotlin-logging-mingwx64:_")
        }
        jvmMain.dependencies {
            implementation("ch.qos.logback:logback-classic:_")
        }
    }
}

//dependencies {
////    testImplementation(kotlin("test"))
//
//    implementation(Kotlin.stdlib)
//    implementation(Kotlin.stdlib.common)
//
//    implementation(KotlinX.coroutines.core)
//    implementation(KotlinX.serialization.json)
//    implementation(KotlinX.datetime)
//
//    implementation(Ktor.client.core)
//    implementation(Ktor.client.json)
//    implementation(Ktor.client.cio)
//    implementation("io.ktor:ktor-serialization-kotlinx-json:_")
//
//    implementation("com.github.ajalt.clikt:clikt:_")
//    implementation("com.github.ajalt.clikt:clikt-markdown:_")
//
////    implementation("io.ktor:ktor-network:_")
//
////    implementation("com.illposed.osc:javaosc-core:_")
//
//    // https://github.com/Rejeq/ktobs
//    implementation("io.github.rejeq:ktobs-core:_")
//    implementation("io.github.rejeq:ktobs-ktor:_")
//
////    implementation("io.obs-websocket.community:client:_")
//
//    implementation("io.github.cdimascio:dotenv-kotlin:_")
//
//    implementation("io.github.oshai:kotlin-logging:_")
//
//
//    implementation("ch.qos.logback:logback-classic:_")
//
//    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-debug:_")
////    implementation("org.eclipse.jetty.websocket:websocket-client:_")
////    implementation("dev.reformator.stacktracedecoroutinator:stacktrace-decoroutinator-jvm-legacy:_")
//}

tasks.shadowJar {
//    archiveBaseName = ""
    archiveClassifier = ""
    archiveVersion = ""
}

//tasks.test {
//    useJUnitPlatform()
//}
//kotlin {
//    jvmToolchain(21)
//}

tasks {
    val linkDebugExecutableMingwX64 by getting {}
    val linkReleaseExecutableMingwX64 by getting {}
    val shadowJar by getting {}

    val packageDistributable by registering(Zip::class) {
        group = "package"
        from(linkDebugExecutableMingwX64)
        from(shadowJar)
//        from(project.file("README.md"))
        archiveBaseName.set(project.name)
        archiveVersion.set("")
        archiveClassifier.set("")
        destinationDirectory.set(project.file("build"))
    }
    val deployDistributable by registering(Copy::class) {
        group = "package"
        from(linkDebugExecutableMingwX64)
        this.destinationDir = File(System.getProperty("user.home")).resolve("bin")
    }
}