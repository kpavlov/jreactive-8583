import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    `java-library`
    kotlin("jvm") version "1.6.10"
    id("org.jetbrains.dokka") version "1.6.10"
    id("org.jlleitschuh.gradle.ktlint") version "10.2.1"
    signing
    `maven-publish`

    // https://github.com/gradle-nexus/publish-plugin
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    val slf4jVersion = "1.7.35"
    val junitJupiterVersion = "5.8.2"

    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    api(kotlin("stdlib-jdk8"))
    api("net.sf.j8583:j8583:1.17.0")
    api("io.netty:netty-handler:4.1.73.Final")
    api("org.slf4j:slf4j-api:$slf4jVersion")
    api("com.google.code.findbugs:jsr305:3.0.2")
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitJupiterVersion")
    testImplementation("org.mockito:mockito-junit-jupiter:4.3.1")
    testImplementation("org.apache.commons:commons-lang3:3.12.0")
    testImplementation("org.assertj:assertj-core:3.22.0")
    testImplementation(platform("org.springframework:spring-framework-bom:5.3.15"))
    testImplementation("org.springframework:spring-context")
    testImplementation("org.springframework:spring-test")
    testImplementation("org.slf4j:slf4j-simple:$slf4jVersion")
    testImplementation("net.jcip:jcip-annotations:1.0")
    testImplementation("org.awaitility:awaitility:4.1.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitJupiterVersion")
}

group = "com.github.kpavlov.jreactive8583"
version = if (findProperty("version") != "unspecified") findProperty("version")
else "0.0.1-SNAPSHOT"
description = "ISO8583 Connector for Netty"
java.sourceCompatibility = JavaVersion.VERSION_11
java.targetCompatibility = JavaVersion.VERSION_11

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf(
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
            TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED
        )
    }
}

val dokkaHtml by tasks.getting(org.jetbrains.dokka.gradle.DokkaTask::class)

val javadocJar: TaskProvider<Jar> by tasks.registering(Jar::class) {
    dependsOn(dokkaHtml)
    archiveClassifier.set("javadoc")
    from(dokkaHtml.outputDirectory)
}

java {
    withSourcesJar()
    withJavadocJar()
}

tasks.jar {
    manifest {
        attributes(
            mapOf(
                "Implementation-Title" to project.name,
                "Implementation-Version" to project.version
            )
        )
    }
}

tasks.assemble {
    dependsOn(javadocJar)
}

publishing {
    // https://docs.gradle.org/current/userguide/publishing_setup.html

    publications.create<MavenPublication>("maven") {
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
                    email.set("mail@KonstantinPavlov.net")
                    url.set("https://KonstantinPavlov.net?utm_source=jreactive8583")
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
        from(components["java"])
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
        // https://blog.solidsoft.pl/2015/09/08/deploy-to-maven-central-using-api-key-aka-auth-token/
        sonatype()
    }
}

signing {
    // https://docs.gradle.org/current/userguide/signing_plugin.html#sec:signatory_credentials
    sign(publishing.publications["maven"])
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
