plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("kapt")

    id("org.springframework.boot")
    id("io.spring.dependency-management")

    `maven-publish`
}

dependencies {
    compileOnly("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")

    implementation("io.github.oshai:kotlin-logging-jvm:6.0.3")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    kapt("org.springframework.boot:spring-boot-configuration-processor")
}

java {
    withSourcesJar()
    withJavadocJar()
}

tasks.jar {
    enabled = true
}

tasks.bootJar {
    enabled = false
}

publishing { uploadToGPR(project, "starter-api") }
