import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
plugins {
    application
    kotlin("jvm") version "1.3.72"
    id("com.github.johnrengelman.shadow") version "5.1.0"
}

group = "com.github.mckernant1"
version = "0.0.1"

application {
    mainClassName = "com.github.mckernant1.runner.RunnerKt"
}

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.7")
    implementation("net.dv8tion:JDA:4.1.1_166")
    implementation("org.apache.httpcomponents:httpclient-cache:4.5.12")
    implementation("com.github.mckernant1:lol-esports-api-wrapper:0.1.13")
    implementation("com.github.mckernant1:fs-scheduler:0.0.1")
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
