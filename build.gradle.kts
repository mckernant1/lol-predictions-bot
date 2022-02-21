import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
plugins {
    application
    kotlin("jvm") version "1.4.20"
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
    maven {
        url = uri("http://mckernant1-mvn.s3-website-us-west-2.amazonaws.com/release")
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.+")

    implementation("net.dv8tion:JDA:4.2.0_204")

    implementation("com.github.mckernant1:lol-esports-api-wrapper:0.1.21")
    implementation("com.github.mckernant1.lol:esports-api:0.0.3")
    implementation("com.github.mckernant1:kotlin-utils:0.0.6")

    implementation("org.slf4j:slf4j-simple:1.7.30")

    implementation(platform("software.amazon.awssdk:bom:2.15.+"))
    implementation("software.amazon.awssdk:cloudwatch")
    implementation("software.amazon.awssdk:dynamodb-enhanced")

    testImplementation("org.testng:testng:7.3.0")
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

tasks.withType<ShadowJar>() {
    manifest {
        attributes["Main-Class"] = "RunnerKt"
    }
}
