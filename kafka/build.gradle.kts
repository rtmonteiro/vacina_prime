group = "br.lenkeryan"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
}

plugins {
    application
    kotlin("jvm")
    kotlin("plugin.serialization") version "1.4.21"
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.5.10")
    implementation("org.apache.kafka:kafka-clients:3.0.0")
    implementation("org.slf4j:slf4j-simple:1.7.33")
    implementation("com.twilio.sdk:twilio:8.25.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.5.10")
    implementation("org.xerial:sqlite-jdbc:3.36.0.3")
    implementation("com.j256.ormlite:ormlite-jdbc:6.1")
}


