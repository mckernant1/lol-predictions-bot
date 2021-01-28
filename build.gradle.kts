import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
plugins {
    application
    kotlin("jvm") version "1.4.10"
    id("com.github.johnrengelman.shadow") version "5.1.0"
}

group = "com.github.mckernant1"
version = "0.0.1"

application {
    mainClassName = "com.github.mckernant1.lol.blitzcrank.RunnerKt"
}

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.7")

    implementation("net.dv8tion:JDA:4.2.0_204")

    implementation("com.github.mckernant1:lol-esports-api-wrapper:0.1.19")
    implementation("com.github.mckernant1:kotlin-utils:0.0.4")

    implementation("org.slf4j:slf4j-api:1.7.30")
    implementation("org.slf4j:slf4j-simple:1.7.30")

    implementation(platform("software.amazon.awssdk:bom:2.15.69"))
    implementation("software.amazon.awssdk:cloudwatch")
    implementation("software.amazon.awssdk:dynamodb-enhanced")

    testImplementation("org.testng:testng:7.3.0")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

task<Test>("test-integration") {
    useTestNG {
        suites("src/test/resources/testng.xml")
        includeGroups("integration")
    }
}

tasks.withType<ShadowJar>() {
    manifest {
        attributes["Main-Class"] = "RunnerKt"
    }
}
