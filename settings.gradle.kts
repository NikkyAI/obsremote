pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    // See https://jmfayard.github.io/refreshVersions
    id("de.fayard.refreshVersions") version "0.60.6"
}
rootProject.name = "obsremote"

refreshVersions {
//    extraArtifactVersionKeyRules(file("versionKeyRules.txt"))
}
