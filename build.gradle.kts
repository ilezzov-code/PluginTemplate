import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.kotlin.dsl.named

plugins {
    id("java-library")
    id("com.gradleup.shadow") version "9.5.1"
    id("com.github.gmazzo.buildconfig") version "6.0.10"
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.18.2-R0.1-SNAPSHOT")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(17)
}

val pluginName: String by project
val pluginVersion: String by project
val pluginDescription: String by project
val pluginMainClass: String by project
val pluginApiVersion: String by project
val pluginAuthor: String by project
val pluginWebsite: String by project
val pluginMainCommand: String by project

buildConfig {
    className("BuildConfig")
    packageName("ru.ilezzov.pluginTemplate")

    buildConfigField("String", "NAME", "\"$pluginName\"")
    buildConfigField("String", "MAIN_CLASS", "\"$pluginMainClass\"")
    buildConfigField("String", "API_VERSION", "\"$pluginApiVersion\"")
    buildConfigField("String", "AUTHOR", "\"$pluginAuthor\"")
    buildConfigField("String", "WEBSITE", "\"$pluginWebsite\"")
    buildConfigField("String", "DESCRIPTION", "\"$pluginDescription\"")
    buildConfigField("String", "MAIN_COMMAND", "\"$pluginMainCommand\"")
}

val copyJar by tasks.registering(Copy::class) {
    from(tasks.named<ShadowJar>("shadowJar").get().archiveFile)
    into(file("D:/ILeZzoV Server/plugins"))
    outputs.upToDateWhen { false }
}

tasks.named<ShadowJar>("shadowJar") {
    finalizedBy(copyJar)
}

tasks.shadowJar {
    configurations = project.configurations.runtimeClasspath.map { setOf(it) }
}

tasks {
    build {
        dependsOn(shadowJar)
    }

    processResources {
        val props = mapOf(
            "pluginName" to pluginName,
            "pluginVersion" to pluginVersion,
            "pluginDescription" to pluginDescription,
            "pluginMainClass" to pluginMainClass,
            "pluginApiVersion" to pluginApiVersion,
            "pluginAuthor" to pluginAuthor,
            "pluginWebsite" to pluginWebsite,
            "pluginMainCommand" to pluginMainCommand
        )

        inputs.properties(props)
        filteringCharset = "UTF-8"

        filesMatching("plugin.yml") {
            expand(props)
        }
    }
}
