import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val springVersion = "2.6.6"
val navTokenSupportVersion = "2.0.14"
val kotlinVersion = "1.6.21"

plugins {
    id("org.springframework.boot") version "2.6.6"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.spring") version "1.6.10"
}

group = "no.nav.pensjon.opptjening"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    // Spring
    implementation("org.springframework.boot:spring-boot-starter-web:$springVersion")
    implementation("org.springframework.boot:spring-boot-starter-actuator:$springVersion")
    implementation("org.springframework.kafka:spring-kafka:$springVersion")

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.2")

    // Log and metric
    implementation("io.micrometer:micrometer-registry-prometheus:1.8.5")
    implementation("net.logstash.logback:logstash-logback-encoder:7.1.1")

    // OIDC
    implementation("no.nav.security:token-validation-spring:$navTokenSupportVersion")
    implementation("no.nav.security:token-client-spring:$navTokenSupportVersion")
    implementation("org.hibernate:hibernate-validator:7.0.4.Final")

    //Test
    testImplementation("org.springframework.boot:spring-boot-starter-test:$springVersion")
    testImplementation("org.springframework.kafka:spring-kafka-test:$springVersion")
    testImplementation("org.testcontainers:kafka:1.17.1")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.8.2")
    testImplementation("no.nav.security:token-validation-spring-test:$navTokenSupportVersion")
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
