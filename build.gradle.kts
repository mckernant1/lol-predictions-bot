import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
plugins {
    application
    kotlin("jvm") version "1.6.10"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "com.github.mckernant1"
version = "0.0.1"

application {
    mainClass.set("com.github.mckernant1.lol.blitzcrank.RunnerKt")
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://mvn.mckernant1.com/release")
    }
    maven {
        name = "m2-dv8tion"
        url = uri( "https://m2.dv8tion.net/releases")
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("com.google.code.gson:gson:2.10")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.14.0")

    implementation("net.dv8tion:JDA:4.4.0_352")

    implementation("com.github.mckernant1.lol:esports-api:0.0.17")
    implementation("com.github.mckernant1:kotlin-utils:0.0.32")

    implementation("org.slf4j:slf4j-api:2.0.5")
    implementation("org.apache.logging.log4j:log4j-core:2.19.0")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.19.0")
    implementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.19.0")

    implementation(platform("software.amazon.awssdk:bom:2.18.21"))
    implementation("software.amazon.awssdk:cloudwatch")
    implementation("software.amazon.awssdk:dynamodb-enhanced")

    testImplementation("org.testng:testng:7.6.1")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "11"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "11"
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
