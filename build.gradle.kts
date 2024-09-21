import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.platform.jvm.JvmPlatform

plugins {
    `java-library`
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.dokka)
    alias(libs.plugins.detekt)
    signing
    `maven-publish`
    alias(libs.plugins.nexusPublish) // https://github.com/gradle-nexus/publish-plugin
}

repositories {
    mavenCentral()
}

dependencies {
    api(libs.findbugs)
    api(libs.netty)
    api(libs.j8583)
    api(libs.slf4j.api)
    api(kotlin("stdlib-jdk8"))

    testImplementation(libs.commons.lang3)
    testImplementation(libs.assertj)
    testImplementation(libs.awaitility)
    testImplementation(libs.junit.jupiter.params)
    testImplementation(libs.mockito)
    testImplementation(libs.slf4j.simple)
    testImplementation(platform(libs.spring.bom))
    testImplementation(libs.spring.context)
    testImplementation(libs.spring.test)
    testImplementation(kotlin("test-junit5"))
    testRuntimeOnly(libs.junit.jupiter.engine)
}

group = "com.github.kpavlov.jreactive8583"
version = findProperty("version")?.toString() ?: "0.0.1-SNAPSHOT"
description = "ISO8583 Connector for Netty"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    withSourcesJar() // Include sources JAR
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
        progressiveMode = true
        freeCompilerArgs.addAll(
            "-Xjvm-default=all",
            "-Xjsr305=strict",
            "-Xexplicit-api=strict"
        )
    }
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events = setOf(
            TestLogEvent.PASSED,
            TestLogEvent.SKIPPED,
            TestLogEvent.FAILED
        )
    }
}

val dokkaJavadocJar by tasks.registering(Jar::class) {
    dependsOn(tasks.dokkaJavadoc)
    from(tasks.dokkaJavadoc.flatMap { it.outputDirectory })
    archiveClassifier.set("javadoc")
}

tasks.assemble {
    dependsOn(dokkaJavadocJar)
}

tasks.jar {
    manifest {
        attributes(
            "Implementation-Title" to project.name,
            "Implementation-Version" to project.version
        )
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            artifact(dokkaJavadocJar.get())

            pom {
                name.set("ISO8583 Connector for Netty")
                description.set("ISO8583 protocol client and server Netty connectors.")
                url.set("https://github.com/kpavlov/jreactive-8583")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("kpavlov")
                        name.set("Konstantin Pavlov")
                        email.set("mail@kpavlov.me")
                        url.set("https://kpavlov.me?utm_source=jreactive8583")
                        roles.set(listOf("owner", "developer"))
                    }
                }
                scm {
                    connection.set("scm:git:git@github.com:kpavlov/jreactive-8583.git")
                    developerConnection.set("scm:git:git@github.com:kpavlov/jreactive-8583.git")
                    url.set("https://github.com/kpavlov/jreactive-8583")
                    tag.set("HEAD")
                }
                inceptionYear.set("2015")
            }
        }
    }

    repositories {
        maven {
            name = "myRepo"
            url = uri(layout.buildDirectory.dir("repo"))
        }
    }
}

nexusPublishing {
    repositories {
        sonatype()
    }
}

signing {
    // https://docs.gradle.org/current/userguide/signing_plugin.html#sec:signatory_credentials
    sign(publishing.publications["maven"])
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}
