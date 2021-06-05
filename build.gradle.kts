import org.jetbrains.kotlin.gradle.plugin.KotlinJsCompilerType.IR
import org.jetbrains.kotlin.gradle.plugin.KotlinJsCompilerType.LEGACY
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack

val appCompiler = System.getProperties().getProperty("appCompiler").orEmpty() == "IR"
val jsCompiler = if (appCompiler) IR else LEGACY


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
    }
    js(jsCompiler) {
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
    mainClassName = "JvmMainKt"
}

val jsBrowserProductionWebpack = tasks.getByName<KotlinWebpack>("jsBrowserProductionWebpack") {
    outputFileName = "js.js"
}
val jsBrowserDevelopmentWebpack = tasks.getByName<KotlinWebpack>("jsBrowserDevelopmentWebpack") {
    outputFileName = "js.js"
}

tasks.getByName<Jar>("jvmJar") {
    dependsOn(jsBrowserProductionWebpack)
    from(File(jsBrowserProductionWebpack.destinationDirectory, jsBrowserProductionWebpack.outputFileName))
}

tasks.getByName<JavaExec>("run") {
    dependsOn(tasks.getByName<Jar>("jvmJar"))
    classpath(tasks.getByName<Jar>("jvmJar"))
}

fun copyJsToWebContent(task: KotlinWebpack = jsBrowserDevelopmentWebpack) {
    val jsBuild = task.destinationDirectory
    //File(task.destinationDirectory, task.outputFileName)
    val targetDir = File(projectDir, "src/jvmMain/resources/js")
    println("COPYING JS -------------------------------------- from $jsBuild -- ${task.outputFileName}")
    targetDir.mkdirs()
    targetDir.listFiles()?.forEach { it.deleteRecursively() }
//    jsBuild.copyRecursively(targetDir, true)
    val skip = setOf("tar", "zip")
    jsBuild.listFiles()
        ?.filterNot { skip.contains(it.extension) }
        ?.forEach { it.copyTo(targetDir.resolve(it.name), true) }
}

tasks.register("appJs") {
    group = "application"
    dependsOn(jsBrowserDevelopmentWebpack)
    doLast { copyJsToWebContent(jsBrowserDevelopmentWebpack) }
    //doLast { if (jsCompiler == IR) copyJsToWebContent() else copyJsToWebContent() }
}

tasks.register("appJsProd") {
    group = "application"
    dependsOn(jsBrowserProductionWebpack)
    //doFirst { if (jsCompiler != IR) error("Per usare questo task si deve avviare gradlew con -DmdProduzione=true") }
    doLast { copyJsToWebContent(jsBrowserProductionWebpack) }
}

tasks.register<Jar>("buildFatJarV2") {
    dependsOn(jsBrowserProductionWebpack)

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
                .map { if (it.isDirectory) it else zipTree(it) }, main.output.classesDirs, main.output.resourcesDir

        )
        // from(File(jsBrowserProductionWebpack.destinationDirectory, jsBrowserProductionWebpack.outputFileName))

    }
    archiveBaseName.set("${project.name}-fat-v2")
}