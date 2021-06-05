import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack

plugins {
    kotlin("multiplatform") version "1.5.10"
    application
}

group = "pro.jako"
version = "1.0-SNAPSHOT"

val ktor_version = "1.5.4"

repositories {
    jcenter()
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven") }
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        withJava()
        testRuns["test"].executionTask.configure {
            useTestNG()
        }
        compilations {
            val main = getByName("main")
            tasks.register<Jar>("buildFatJarV1") {
                group = "application"
                dependsOn(tasks.assemble) //assemble build
                manifest {
                    attributes["Main-Class"] = "JvmMainKt"
                }
                doFirst {
                    from(
                        configurations.getByName("runtimeClasspath")
                            .map { if (it.isDirectory) it else zipTree(it) }, main.output.classesDirs
                    )

                }
                archiveBaseName.set("${project.name}-fat2")
            }
        }
    }
    js(LEGACY) {
        binaries.executable()
        browser {
            commonWebpackConfig {
                cssSupport.enabled = true
            }
        }
    }
    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("io.ktor:ktor-server-core:$ktor_version")
                implementation("io.ktor:ktor-server-cio:$ktor_version")
                implementation("io.ktor:ktor-websockets:$ktor_version")
                implementation("io.ktor:ktor-html-builder:$ktor_version")
                implementation("org.slf4j:slf4j-simple:1.7.30")
                implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.7.2")
            }
        }
        val jvmTest by getting
        val jsMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-html:0.7.2")
            }
        }
        val jsTest by getting
    }
}

application {
    mainClassName = "ServerKt"
}

tasks.getByName<KotlinWebpack>("jsBrowserProductionWebpack") {
    outputFileName = "js.js"
}

tasks.getByName<Jar>("jvmJar") {
    dependsOn(tasks.getByName("jsBrowserProductionWebpack"))
    val jsBrowserProductionWebpack = tasks.getByName<KotlinWebpack>("jsBrowserProductionWebpack")
    from(File(jsBrowserProductionWebpack.destinationDirectory, jsBrowserProductionWebpack.outputFileName))
}

tasks.getByName<JavaExec>("run") {
    dependsOn(tasks.getByName<Jar>("jvmJar"))
    classpath(tasks.getByName<Jar>("jvmJar"))
}


tasks.register<Jar>("buildFatJarV2") {
    //this is equivalent to buildFatJarV1
    val main = kotlin.jvm().compilations.getByName("main")
    group = "application"
    dependsOn(tasks.assemble) //do not run tests
//    dependsOn(tasks.build) //run tests
    manifest {
        attributes["Main-Class"] = "JvmMainKt"
    }
    doFirst {
        from(
            configurations.getByName("runtimeClasspath")
                .map { if (it.isDirectory) it else zipTree(it) }, main.output.classesDirs
        )

    }
    archiveBaseName.set("${project.name}-fat3")
}