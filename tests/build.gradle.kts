plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    testImplementation(platform("org.junit:junit-bom:5.10.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    // testImplementation("junit:junit:4.13.2")
    testImplementation(kotlin("test"))
    // testImplementation(kotlin("test-junit"))
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.mockito:mockito-core:5.9.0")
}

tasks.test {
    useJUnitPlatform()
}