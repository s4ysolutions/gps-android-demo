import com.android.build.api.dsl.Packaging

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "s4y.demo.mapsdksdemo.mapsforgevtm"
    compileSdk = 34

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    runtimeOnly("org.mapsforge:vtm-android:0.20.0:natives-armeabi-v7a")
    runtimeOnly("org.mapsforge:vtm-android:0.20.0:natives-arm64-v8a")
    runtimeOnly("org.mapsforge:vtm-android:0.20.0:natives-x86")
    runtimeOnly("org.mapsforge:vtm-android:0.20.0:natives-x86_64")
    implementation("org.mapsforge:vtm-android:0.20.0")
    implementation("com.caverock:androidsvg:1.4")
    implementation("org.mapsforge:vtm-http:0.20.0")
    implementation("org.mapsforge:vtm-themes:0.20.0")
    implementation("org.mapsforge:vtm-jts:0.20.0")
    //implementation("org.mapsforge:vtm-mvt:0.20.0")
    //implementation("org.mapsforge:vtm-android-mvt:0.20.0")
// https://github.com/square/okhttp/issues/4481
    implementation("com.squareup.okhttp3:okhttp:3.12.13")
    implementation("com.squareup.okio:okio:1.15.0")

    implementation(project(":map"))
    implementation(project(":mapsforge-maps"))
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}