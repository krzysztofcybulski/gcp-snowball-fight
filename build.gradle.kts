import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import ratpack.gradle.RatpackPlugin

buildscript {
    dependencies {
        classpath("io.ratpack:ratpack-gradle:1.9.0")
    }
}

plugins {
    application
    kotlin("jvm") version "1.4.21"
}

apply {
    plugin<RatpackPlugin>()
}

repositories {
    mavenLocal()
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.4.2")
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("com.google.cloud:google-cloud-bigquerystorage:2.7.0")

    runtimeOnly("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.7")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
        javaParameters = true
    }
}

application {
    mainClassName = "hello.WebAppKt"
}

tasks.replace("assemble").dependsOn("installDist")

tasks.register<DefaultTask>("stage") {
    dependsOn("installDist")
}
