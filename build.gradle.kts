import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
plugins {
    application
    kotlin("jvm") version "1.3.72"
    id("com.github.johnrengelman.shadow") version "5.1.0"
}

group = "com.github.mckernant1"
version = "1.0-SNAPSHOT"

application {
    mainClassName = "com.github.mckernant1.runner.RunnerKt"
}

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.jessecorbett:diskord:1.6.2")
    implementation("com.github.mckernant1:lol-esports-api-wrapper:0.1.6")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}


tasks.withType<ShadowJar>() {
    manifest {
        attributes["Main-Class"] = "RunnerKt"
    }
}
