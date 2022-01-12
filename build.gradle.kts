plugins {
    java
    kotlin("jvm") version "1.6.10"
    id("org.jetbrains.dokka") version "1.6.0"
    id("org.jlleitschuh.gradle.ktlint") version "10.2.0"
    `maven-publish`
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("net.sf.j8583:j8583:1.17.0")
    implementation("io.netty:netty-handler:4.1.72.Final")
    implementation("org.slf4j:slf4j-api:1.7.32")
    implementation("com.google.code.findbugs:jsr305:3.0.2")
    implementation(kotlin("stdlib-jdk8"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testImplementation("org.mockito:mockito-junit-jupiter:4.2.0")
    testImplementation("org.apache.commons:commons-lang3:3.12.0")
    testImplementation("org.assertj:assertj-core:3.22.0")
    testImplementation("org.springframework:spring-context:5.3.14")
    testImplementation("org.springframework:spring-test:5.3.14")
    testImplementation("org.slf4j:slf4j-simple:1.7.32")
    testImplementation("net.jcip:jcip-annotations:1.0")
    testImplementation("org.awaitility:awaitility:4.1.1")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
}

group = "com.github.kpavlov.jreactive8583"
version = "1.3.5-SNAPSHOT"
description = "ISO8583 Connector for Netty"
java.sourceCompatibility = JavaVersion.VERSION_11

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

val dokkaHtml by tasks.getting(org.jetbrains.dokka.gradle.DokkaTask::class)

val javadocJar: TaskProvider<Jar> by tasks.registering(Jar::class) {
    dependsOn(dokkaHtml)
    archiveClassifier.set("javadoc")
    from(dokkaHtml.outputDirectory)
}

tasks.assemble {
    dependsOn(javadocJar)
}


publishing {
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
//    repositories {
//        maven {
//            name = "OSSRH"
//            url = "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
//            credentials {
//                username = System.getenv("MAVEN_USERNAME")
//                password = System.getenv("MAVEN_PASSWORD")
//            }
//        }
//    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
