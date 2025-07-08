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
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
////                                                   # available:"0.10.0"
////                                                   # available:"1.0.0-rc-1"
////                                                   # available:"1.0.0"
    // See https://jmfayard.github.io/refreshVersions
    id("de.fayard.refreshVersions") version "0.60.5"
}
rootProject.name = "obsremote"

refreshVersions {
    this.extraArtifactVersionKeyRules(file("versionKeyRules.txt"))
}
