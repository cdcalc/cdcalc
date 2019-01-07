import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        mavenCentral()
        jcenter()
    }
}

plugins {
    base
    kotlin("jvm") version "1.3.11"
    id("com.github.cdcalc") version "0.0.21"
    id("com.github.ben-manes.versions") version "0.20.0" apply false
}

allprojects {
    group = "com.github.cdcalc"
}

subprojects {
    apply(plugin = "com.github.ben-manes.versions")
    apply(plugin = "kotlin")
    apply(plugin = "jacoco")
    apply(plugin = "org.jetbrains.kotlin.jvm")


    repositories {
        jcenter()
        mavenLocal()
    }

    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }

    dependencies {
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.3.11")

        testImplementation("org.junit.jupiter:junit-jupiter-api:5.4.0-M1")
        testImplementation("org.junit.jupiter:junit-jupiter-params:5.4.0-M1")
        testRuntime("org.junit.jupiter:junit-jupiter-engine:5.4.0-M1")
    }

    tasks {
        test {
            useJUnitPlatform()
        }

        val jacocoTestReport = named<JacocoReport>("jacocoTestReport") {
            reports {
                xml.isEnabled = true
                html.isEnabled = true
            }
        }

        check {
            dependsOn(jacocoTestReport)
        }
    }
}
/*
TaskProvider<Task> calculateVersionTask = tasks.named('calculateVersion')
calculateVersionTask.configure {
    it.doLast() { versionTask ->
        project.findProject(":core").tasks.named("bintrayUpload").configure { Task t ->
            t.setProperty("versionName", versionTask.ext.version)
            t.setProperty("versionVcsTag", "v${versionTask.ext.version}")
        }
    }
}

TaskProvider<Task> releaseSetup = tasks.register('releaseSetup')
releaseSetup.configure {
    it.dependsOn(calculateVersionTask)
}

TaskProvider<Task> releaseBuild = tasks.register('releaseBuild')
releaseBuild.configure {
    it.dependsOn(releaseSetup)
    it.mustRunAfter(releaseSetup)
    it.dependsOn(':core:build', ':plugin:build', ':cli:build')
}

TaskProvider<Task> releasePublish = tasks.register('releasePublish')
releasePublish.configure {
    it.dependsOn(releaseBuild)
    it.dependsOn(':core:bintrayUpload', ':plugin:publishPlugins')
    it.mustRunAfter(releaseBuild)
}

TaskProvider<Task> release = tasks.register('release')
release.configure {
    it.dependsOn(releasePublish)
}
*/

project(":plugin") {
    dependencies {
        implementation(project(":core"))
    }
}

project(":cli") {
    dependencies {
        implementation(project(":core"))
    }
}

tasks.wrapper {
    this.gradleVersion = "5.1"
    this.distributionType = Wrapper.DistributionType.ALL
}
