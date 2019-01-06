import com.jfrog.bintray.gradle.BintrayExtension
import org.gradle.api.tasks.bundling.Jar
import com.jfrog.bintray.gradle.tasks.BintrayUploadTask

plugins {
    kotlin("jvm") version "1.3.10"
    `maven-publish`
    id("com.github.cdcalc") version "0.0.20"
    id("com.jfrog.bintray") version "1.8.4"
}

group = "com.github.cdcalc"

repositories {
    jcenter()
}

dependencies {
    compile(kotlin("stdlib"))
}

val sourcesJar by tasks.registering(Jar::class) {
    classifier = "sources"
    from(sourceSets.main.get().allSource)
}

publishing {
    publications {
        register("mavenJava", MavenPublication::class) {
            from(components["java"])
            artifact(sourcesJar.get())
        }
    }
}

cdcalc {
    versionFile = file(".version")
    repository = file("../.git")
}

val named = tasks.named("calculateVersion")

bintray {
    user = "a_bintray_user"
    key = "a_bintray_api_key"

    setPublications("mavenJava")
    pkg(delegateClosureOf<BintrayExtension.PackageConfig> {
        repo = "cdcalc"
        name = "cdcalc-gradle-sample"
        userOrg = "cdcalc"
        setLicenses("MIT")
        version(closureOf<BintrayExtension.VersionConfig> {
        })
    })
    publish = true
    dryRun = true
}

// Custom calculate version tasks
val calculateAndSetVersion by tasks.registering(com.github.cdcalc.gradle.CalculateVersionTask::class) {
    this.doLast {
        // version and branch is provided as extra variables
        val calculatedVersion: String = this.extra.get("version") as String
        val uploadTasks = project.tasks.withType(BintrayUploadTask::class)
        uploadTasks.configureEach {
            this.versionName = calculatedVersion
        }

        /* NOTE: this is not working at the moment
        project.extensions.configure(com.jfrog.bintray.gradle.BintrayExtension::class) {
            this.pkg.version.name = calculatedVersion
        }
        */
    }
}

val releaseLocal by tasks.registering {
    group = "Publishing"
    description = "Calculate version and release to maven local"
    dependsOn("calculateVersion", "publishToMavenLocal")
}

tasks.named("publishToMavenLocal").configure {
    mustRunAfter("calculateVersion")
}

tasks.wrapper {
    this.gradleVersion = "5.1"
    this.distributionType = Wrapper.DistributionType.ALL
}
