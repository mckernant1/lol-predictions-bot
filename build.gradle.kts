import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    application
    kotlin("jvm") version "2.1.10"
    id("com.gradleup.shadow") version "9.3.1"
}

group = "com.mckernant1.lol"
version = "0.0.1"

application {
    mainClass = "com.mckernant1.lol.blitzcrank.RunnerKt"
}

repositories {
    mavenCentral()
    // Required for AWS profiler
    maven(uri("https://d1osg35nybn3tt.cloudfront.net"))
    maven(uri("https://mvn.mckernant1.com/release"))
}

dependencies {
    // Utils
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.google.guava:guava:33.4.8-jre")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.21.0")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.21.0")

    // Discord
    implementation("net.dv8tion:JDA:6.3.0") {
        exclude(module = "opus-java")
    }

    // My Libs
    implementation("com.mckernant1.lol:esports-api:0.2.2")
    implementation("com.mckernant1:kotlin-utils:0.3.19")
    implementation("com.mckernant1.commons:metrics:0.1.4")

    // Logging
    implementation("org.slf4j:slf4j-api:2.0.17")
    implementation("org.apache.logging.log4j:log4j-core:2.25.3")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.25.3")
    implementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.25.3")
    implementation("org.slf4j:jul-to-slf4j:2.0.17")

    // AWS
    implementation(platform("software.amazon.awssdk:bom:2.41.29"))
    implementation("software.amazon.awssdk:cloudwatch")
    implementation("software.amazon.awssdk:dynamodb-enhanced")

    implementation("com.amazonaws:codeguru-profiler-java-agent:1.2.2")

    testImplementation("org.testng:testng:7.12.0")
}

tasks {
    compileKotlin {
        compilerOptions.jvmTarget.set(JvmTarget.JVM_21)
    }
    compileTestKotlin {
        compilerOptions.jvmTarget.set(JvmTarget.JVM_21)
    }
}

tasks.withType<AbstractTestTask>().configureEach {
    failOnNoDiscoveredTests = false
}

tasks.register<Test>("test-integration") {
    useTestNG {
        suites("src/test/resources/testng.xml")
        includeGroups("integration")
    }
}
