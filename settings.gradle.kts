pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    @Suppress("UnstableApiUsage")
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "MapSDKsDemo"
include(":app")
include(":mapsforge")
include(":map")
include(":gps-android")
include(":permissions")
include(":tests")
include(":mapsforge-vtm")
include(":mapsforge-maps")
include(":gps")
