/*
 * This file was generated by the Gradle 'init' task.
 */

plugins {
    `java-library`
    `maven-publish`
}

repositories {
    mavenLocal()
    maven {
        url = uri("https://jcenter.bintray.com")
    }

    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
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
    compileOnly(libs.org.projectlombok.lombok)
}

group = "com.bedrockcloud"
version = "1.0-SNAPSHOT"
description = "Cloud"
java.sourceCompatibility = JavaVersion.VERSION_1_8

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}
