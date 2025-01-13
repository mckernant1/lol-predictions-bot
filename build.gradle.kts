import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
plugins {
    application
    kotlin("jvm") version "1.8.21"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.mckernant1.lol"
version = "0.0.1"

application {
    mainClass.set("com.mckernant1.lol.blitzcrank.RunnerKt")
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
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.google.guava:guava:32.1.0-jre")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.14.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.14.2")

    // Discord
    implementation("net.dv8tion:JDA:5.0.2") {
        exclude(module = "opus-java")
    }

    // My Libs
    implementation("com.mckernant1.lol:esports-api:0.2.0")
    implementation("com.mckernant1.commons:kotlin-utils:0.2.3")
    implementation("com.mckernant1.commons:metrics:0.0.10")

    // Logging
    implementation("org.slf4j:slf4j-api:2.0.5")
    implementation("org.apache.logging.log4j:log4j-core:2.20.0")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.20.0")
    implementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.20.0")
    implementation("org.slf4j:jul-to-slf4j:2.0.7")

    // AWS
    implementation(platform("software.amazon.awssdk:bom:2.18.21"))
    implementation("software.amazon.awssdk:cloudwatch")
    implementation("software.amazon.awssdk:dynamodb-enhanced")

    implementation("com.amazonaws:codeguru-profiler-java-agent:1.2.2")

    testImplementation("org.testng:testng:7.7.0")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "21"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "21"
    }
}

task<Test>("test-integration") {
    useTestNG {
        suites("src/test/resources/testng.xml")
        includeGroups("integration")
    }
}

tasks.withType<ShadowJar> {
    from("./src/main/resources")
    manifest {
        attributes["Main-Class"] = "RunnerKt"
    }
}
