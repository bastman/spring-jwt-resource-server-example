import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.32"
    kotlin("plugin.spring") version "1.4.32"
    id("org.springframework.boot") version "2.4.5"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

dependencies {
    // kotlin
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    // logging
    implementation("io.github.microutils:kotlin-logging:2.0.+")
    // spring
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    // implementation("org.springframework.boot:spring-boot-starter-security")
    // implementation("com.nimbusds:nimbus-jose-jwt:8.4")
    // jackson
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // swagger
    val swaggerVersion_2x = "2.9.2"
    implementation("io.springfox:springfox-swagger2:$swaggerVersion_2x")
    implementation("io.springfox:springfox-swagger-ui:$swaggerVersion_2x")

    // test
    //testImplementation("org.jetbrains.kotlin:kotlin-test")
    //testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    //testImplementation("org.springframework.security:spring-security-test")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// https://spring.io/guides/topicals/spring-boot-docker/
// https://docs.spring.io/spring-boot/docs/2.4.5/gradle-plugin/reference/htmlsingle/#build-image
tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootBuildImage>("bootBuildImage") {
    imageName = "docker.local/spring-jwt-resource-server-example:snapshot"
}

