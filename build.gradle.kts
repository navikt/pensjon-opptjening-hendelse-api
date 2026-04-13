import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val springKafkaVersion = "3.3.14"
val jacksonVersion = "2.21.2"
val logbackEncoderVersion = "9.0"
val mockitoKotlinVersion = "6.3.0"
val navTokenSupportVersion = "5.0.30"
val hibernateValidatorVersion = "9.1.0.Final"


plugins {
    val kotlinVersion = "2.3.20"
    id("org.jetbrains.kotlin.jvm") version kotlinVersion
    id("org.jetbrains.kotlin.plugin.spring") version kotlinVersion
    id("org.springframework.boot") version "3.5.13"
    id("com.github.ben-manes.versions") version "0.53.0"
}

apply(plugin = "io.spring.dependency-management")

group = "no.nav.pensjon.opptjening"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.kafka:spring-kafka:$springKafkaVersion")

    // Kotlin
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")

    // Log and metric
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("net.logstash.logback:logstash-logback-encoder:$logbackEncoderVersion")

    // OIDC
    implementation("no.nav.security:token-validation-spring:$navTokenSupportVersion")
    implementation("no.nav.security:token-client-spring:$navTokenSupportVersion")
    implementation("org.hibernate.validator:hibernate-validator:$hibernateValidatorVersion")

    // Test - setup
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.kafka:spring-kafka-test:$springKafkaVersion")
    testImplementation("org.mockito.kotlin:mockito-kotlin:$mockitoKotlinVersion")

    // Test - token-validation-spring-test dependencies
    testImplementation("no.nav.security:token-validation-spring-test:$navTokenSupportVersion")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        freeCompilerArgs.add("-Xjsr305=strict")
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events(
            org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
        )
    }
}

tasks.withType<DependencyUpdatesTask>().configureEach {
    rejectVersionIf {
        isNonStableVersion(candidate.version)
    }
}

fun isNonStableVersion(version: String): Boolean {
    return listOf("BETA", "RC", "-M", "-rc-", "Alpha").any { version.uppercase().contains(it) }
}

