plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

dependencies {
    implementation(libs.kotlinxCoroutinesCore)
    testImplementation(platform(libs.junitBom))
    testImplementation(libs.jupiterJunitJupiter)
    // testImplementation("junit:junit:4.13.2")
    testImplementation(kotlin("test"))
    // testImplementation(kotlin("test-junit"))
    testImplementation(kotlin("test-junit5"))
    testImplementation(libs.mockitoCore)
}

tasks.test {
    useJUnitPlatform()
}