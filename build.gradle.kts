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
    maven("https://maven.aliyun.com/repository/google")
    maven("https://maven.aliyun.com/repository/public")
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

// https://jetpackcompose.cn/docs/resources
dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    // implementation(files("libs/bundletool-all-1.15.6.jar"))
    implementation(compose.desktop.currentOs)
    implementation("com.alibaba:fastjson:1.2.83")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.5.20")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

// https://github.com/JetBrains/compose-multiplatform/tree/master/tutorials/Native_distributions_and_local_execution#packaging-resources
// https://github.com/JetBrains/compose-multiplatform/tree/master/tutorials/Native_distributions_and_local_execution
compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Deb, TargetFormat.Exe)
            packageName = "AndroidAdbTool"
            packageVersion = versionName
            description = "AndroidAdbTool"
            vendor = "Erning"
            macOS {
                iconFile.set(project.file("src/main/resources/logo/logo_mac.icns"))
                bundleID = "cn.erning.AndroidAdbTool"
            }
            windows {
                upgradeUuid = "2D79971E-C867-95ED-0859-B109F3EC97CC"
                console = true
                shortcut = true
                iconFile.set(project.file("src/main/resources/logo/logo_windows.ico"))
            }
            linux {
                iconFile.set(project.file("src/main/resources/logo/logo_linux.png"))
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