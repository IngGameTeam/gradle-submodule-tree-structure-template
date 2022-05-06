buildscript {
    repositories {
        gradlePluginPortal()
    }
    dependencies {
        classpath("gradle.plugin.com.github.johnrengelman:shadow:7.1.2")
    }
}
val kotlin_version = "1.6.10"
plugins {
    kotlin("jvm") version kotlin_version
    id("org.jetbrains.dokka") version kotlin_version apply false
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

allprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "com.github.johnrengelman.shadow")

    tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
        archiveFileName.set("${project.name}.jar")
    }

    repositories {
        mavenCentral()

    }

    dependencies {
        testImplementation(kotlin("test"))

        compileOnly("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version")
    }

    if (version == "unspecified") {
        version = rootProject.version
    }


}

fun childTreeApi(p: Project) {
    p.childProjects.values.forEach {
        var parentProj = it.parent
        while (parentProj !== null && parentProj !== rootProject) {
            parentProj.dependencies.api(it)
            parentProj = parentProj.parent
        }
        childTreeApi(it)
    }
}
childTreeApi(rootProject)
