import java.util.Properties
import java.io.FileInputStream

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

val localProperties = Properties()
val localPropertiesFile = rootDir.resolve("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}
val mapboxDownloadsToken = localProperties.getProperty("MAPBOX_DOWNLOADS_TOKEN")


dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    @Suppress("UnstableApiUsage")
    repositories {
        google()
        mavenCentral()
        // Mapbox Maven repository
        maven {
            url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
            // Do not change the username below. It should always be "mapbox" (not your username).
            credentials.username = "mapbox"
            // Use the secret token stored in gradle.properties as the password
            credentials.password = mapboxDownloadsToken
            authentication.create<BasicAuthentication>("basic")
        }
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
include(":mapbox")
