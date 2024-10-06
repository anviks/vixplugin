import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

// Common variables for package paths and other settings
val targetJavaVersion = 21
val groupPackage = "com.github.anviks.vixplugin"
val pluginName = "vixplugin"
val relocationTarget = "${groupPackage}_lib"
val versionNumber = "1.0"

// External packages to relocate
val packagesToRelocate = listOf(
    "com.jeff_media.customblockdata",
    "com.jeff_media.morepersistentdatatypes",
    "com.jeff_media.armorequipevent",
)

plugins {
    java
    id("com.gradleup.shadow") version "8.3.1"
    id("io.papermc.paperweight.userdev") version "1.7.2"
    kotlin("jvm") version "2.0.20"
    kotlin("plugin.serialization") version "2.0.20"
}

java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
    }
}

kotlin {
    jvmToolchain(targetJavaVersion)
}

group = groupPackage
version = versionNumber

base {
    archivesName.set(pluginName)
}

sourceSets {
    named("main") {
        val mainPackagePath = groupPackage.replace(".", "/")
        java {
            setSrcDirs(listOf("src/main/java"))
            exclude("$mainPackagePath/sandbox/**")
            exclude("$mainPackagePath/listeners/DeathMessages.kt")
        }
    }
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
    maven("https://libraries.minecraft.net") {
        name = "minecraft-libraries"
    }
    maven("https://repo.codemc.org/repository/maven-public/")
    maven("https://repo.dmulloy2.net/repository/public/")
}

dependencies {
    implementation("commons-io:commons-io:2.11.0")
    implementation("org.jetbrains:annotations:24.0.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.1")
    implementation("com.fasterxml.jackson.core:jackson-core:2.14.2")
    implementation("com.jeff-media:custom-block-data:2.2.2")
    implementation("com.jeff-media:MorePersistentDataTypes:2.4.0")
    implementation("com.jeff-media:armor-equip-event:1.0.3")
    compileOnly("dev.jorel:commandapi-bukkit-core:9.5.0")
    paperweight.paperDevBundle("1.21.1-R0.1-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.0.20")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.6.20")
    implementation("org.reflections:reflections:0.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"

    if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible) {
        options.release.set(targetJavaVersion)
    }
}

tasks.named<ShadowJar>("shadowJar") {
    archiveFileName.set("$pluginName-$versionNumber.jar")

    packagesToRelocate.forEach {
        relocate(it, "$relocationTarget.${it.substringAfterLast('.')}")
    }

    destinationDirectory.set(file("C:/Users/Andreas Viks/Desktop/MC Servers/Paper 1.21.1/plugins"))
}
