import com.android.build.api.dsl.Packaging

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "s4y.demo.mapsdksdemo.mapsforgevtm"
    compileSdk = 35

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

dependencies {

    runtimeOnly(libs.vtmAndroid)
    runtimeOnly(libs.vtmAndroid)
    runtimeOnly(libs.vtmAndroid)
    runtimeOnly(libs.vtmAndroid)
    implementation(libs.vtmAndroid)
    implementation(libs.androidsvg)
    implementation(libs.vtmHttp)
    implementation(libs.vtmThemes)
    implementation(libs.vtmJts)
    //implementation("org.mapsforge:vtm-mvt:0.20.0")
    //implementation("org.mapsforge:vtm-android-mvt:0.20.0")
// https://github.com/square/okhttp/issues/4481
    implementation(libs.okhttp)
    implementation(libs.okio)

    implementation(project(":map"))
    implementation(project(":mapsforge-maps"))
    implementation(libs.coreKtx)
    implementation(libs.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.extJunit)
    androidTestImplementation(libs.espressoCore)
}