/*
 * This file was generated by the Gradle 'init' task.
 */

plugins {
    `java-library`
    `maven-publish`
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://jcenter.bintray.com")
    }

    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

java {
    java.sourceCompatibility = JavaVersion.VERSION_17
    java.targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    api(libs.com.googlecode.json.simple.json.simple)
    api(libs.org.yaml.snakeyaml)
    api(libs.com.google.code.gson.gson)
    api(libs.org.jetbrains.annotations)
    api(libs.com.google.guava.guava)
    api(libs.it.unimi.dsi.fastutil)
    api(libs.org.slf4j.slf4j.api)
    api(libs.org.slf4j.slf4j.simple)
    api(libs.dnsjava.dnsjava)
    api(libs.org.jline.jline.reader)
    api(libs.org.ow2.asm.asm)
    testImplementation(libs.junit.junit)
    implementation("org.jetbrains:annotations:24.0.1")
    implementation("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")
}

group = "com.bedrockcloud"
version = "1.0-SNAPSHOT"
description = "Cloud"

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
    repositories {
        maven {
            name = "OSSRH"
            url = uri("http://185.117.250.237:8081/repository/maven-releases/")
            credentials {
                username = System.getenv("MAVEN_USERNAME")
                password = System.getenv("MAVEN_PASSWORD")
            }
        }
    }
}
