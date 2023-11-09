import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val versionName = "1.0.0"

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
}

group = "cn.erning"
version = versionName

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

// https://jetpackcompose.cn/docs/resources
dependencies {
    implementation(compose.desktop.currentOs)
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

// https://github.com/JetBrains/compose-multiplatform/tree/master/tutorials/Native_distributions_and_local_execution
compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Deb, TargetFormat.Exe)
            packageName = "AndroidAdbTool"
            packageVersion = versionName
            description = "AndroidAdbTool"
            vendor = "叫我二宁大人！"
            macOS {
                iconFile.set(project.file("logo_mac.icns"))
            }
            windows {
                iconFile.set(project.file("logo_windows.ico"))
            }
            linux {
                iconFile.set(project.file("logo_linux.png"))
            }
        }
    }
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "MainKt"
        attributes["Implementation-Version"] = versionName
    }
}

tasks.register<Jar>("ubarJar") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    archiveClassifier.set("ubar")
    archiveVersion.set(versionName)
    from(sourceSets.main.get().output)
    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}