import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.kotlin.plugin.jpa") version "1.8.20"
    id("org.springframework.boot") version "2.7.11"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("jvm") version "1.3.71"
    kotlin("plugin.spring") version "1.8.20"
}

group = "ar.edu.unq.eperdemic"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
    google()
}
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-neo4j")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-neo4j")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")


    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.mockito.kotlin:mockito-kotlin:4.1.0")
    implementation("org.mockito:mockito-core:5.2.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation("mysql:mysql-connector-java:8.0.33")
    testImplementation("org.testng:testng:7.1.0")
    implementation("com.google.gms:google-services:4.3.3")
    implementation ("com.google.firebase:firebase-analytics:17.2.2")
    implementation ("com.google.firebase:firebase-admin:6.11.0")
    implementation ("com.google.firebase:firebase-auth:19.3.2")
    implementation("io.ktor:ktor-server-netty:1.6.4")

}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
    }
}

configurations {
    all {
        exclude("org.slf4j:log4j-over-slf4j")
    }
}
