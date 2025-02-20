plugins {
    kotlin("jvm") version "2.0.0"
    kotlin("plugin.spring") version "2.0.0" apply false
    kotlin("plugin.jpa") version "2.0.0" apply false
    kotlin("kapt") version "1.9.0" apply false

    id("jacoco")
    id("org.springframework.boot") version "3.2.2" apply false
    id("io.spring.dependency-management") version "1.1.4" apply false
}

allprojects {
    group = "com.docshunt"

    repositories {
        mavenCentral()
        maven { url = uri("https://artifacts.consensys.net/public/maven/maven/") }
        docshuntGPR()
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "jacoco")

    tasks.test {
        useJUnitPlatform()
        finalizedBy("jacocoTestReport")
    }

    tasks.compileKotlin {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "17"
        }
    }

    configurations {
        compileOnly {
            extendsFrom(configurations.annotationProcessor.get())
        }
    }

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(17)
        }
    }
}
