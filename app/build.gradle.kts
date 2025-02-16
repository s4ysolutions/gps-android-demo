import java.util.Properties
import java.io.FileInputStream

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}
val mapboxAccessToken = localProperties.getProperty("MAPBOX_ACCESS_TOKEN")

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "s4y.demo.mapsdksdemo"
    compileSdk = 34

    buildFeatures {
        compose = true
        buildConfig=true
    }

    defaultConfig {
        applicationId = "s4y.demo.mapsdksdemo"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        buildConfigField("String", "mapboxAccessToken", "\"$mapboxAccessToken\"")
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
        }
        release {
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            pickFirsts += "assets/**/*.svg"
            pickFirsts += "assets/**/*.png"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

}

dependencies {
    implementation(project(":map"))
    implementation(project(":mapsforge"))
    implementation(project(":mapbox"))
    implementation(project(":gps-android"))
    implementation(project(":mapsforge-vtm"))
    implementation(project(":gps"))
    implementation(libs.coreKtx)
    implementation(libs.lifecycleRuntimeKtx)
    implementation(libs.activityCompose)
    implementation(libs.lifecycleViewmodelCompose)
    implementation(libs.accompanistPermissions)
    implementation(platform(libs.compose.bom))
    // implementation(libs.ui)
    // implementation("androidx.compose.ui:ui-graphics")
    // implementation("androidx.compose.ui:ui-tooling-preview")
    implementation(libs.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.extJunit)
    androidTestImplementation(libs.espressoCore)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.uiTestJunit4)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.uiTestManifest)
}