import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val springVersion = "2.6.7"
val springKafkaVersion = "2.8.5"
val kotlinVersion = "1.6.21"
val jacksonVersion = "2.13.2"
val prometheusVersion = "1.8.5"
val logbackEncoderVersion = "7.1.1"
val navTokenSupportVersion = "2.0.15"
val hibernateValidatorVersion = "7.0.4.Final"
val mockWebserverVersion = "4.9.3"

plugins {
    id("org.springframework.boot") version "2.6.7"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.spring") version "1.6.10"
}

group = "no.nav.pensjon.opptjening"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring
    implementation("org.springframework.boot:spring-boot-starter-web:$springVersion")
    implementation("org.springframework.boot:spring-boot-starter-actuator:$springVersion")
    implementation("org.springframework.kafka:spring-kafka:$springKafkaVersion")

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")

    // Log and metric
    implementation("io.micrometer:micrometer-registry-prometheus:$prometheusVersion")
    implementation("net.logstash.logback:logstash-logback-encoder:$logbackEncoderVersion")

    // OIDC
    implementation("no.nav.security:token-validation-spring:$navTokenSupportVersion")
    implementation("no.nav.security:token-client-spring:$navTokenSupportVersion")
    implementation("org.hibernate:hibernate-validator:$hibernateValidatorVersion")

    // Test - setup
    testImplementation("org.springframework.boot:spring-boot-starter-test:$springVersion")
    testImplementation("org.springframework.kafka:spring-kafka-test:$springKafkaVersion")
    // Test - token-validation-spring-test dependencies
    testImplementation("no.nav.security:token-validation-spring-test:$navTokenSupportVersion")
    testImplementation("com.squareup.okhttp3:mockwebserver:$mockWebserverVersion")
    testImplementation("com.squareup.okhttp3:okhttp:$mockWebserverVersion")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
