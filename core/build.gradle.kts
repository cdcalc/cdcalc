import java.time.ZonedDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

plugins {
    id("maven-publish")
    id("com.jfrog.bintray") version("1.8.4")
}

dependencies {
    implementation("org.eclipse.jgit:org.eclipse.jgit:5.2.0.201812061821-r")
    testRuntime("ch.qos.logback:logback-classic:1.2.3")
}

val sourcesJar by tasks.registering(Jar::class) {
    classifier = "sources"
    from(sourceSets.main.get().allSource)
    dependsOn(tasks.classes)
}

val javadocJar by tasks.registering(Jar::class) {
    classifier = "javadoc"
    from(tasks.javadoc.get().destinationDir)
    dependsOn(tasks.javadoc)
}

publishing {
    publications {
        register<MavenPublication>("corePublication") {
            from(components["java"])
            groupId = "com.github.cdcalc"
            artifactId = "cdcalc-core"
            artifact(javadocJar.get())
            artifact(sourcesJar.get())
            pom {
                description.set("Calculate upcoming version from git commits")
                name.set("cdcalc")
                url.set("https://github.com/cdcalc/cdcalc")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("http://www.opensource.org/licenses/mit-license.php")
                        distribution.set("repo")
                    }
                }
                developers {
                    developer {
                        id.set("orjan.sjoholm")
                        name.set("Örjan Sjöholm")
                        email.set("orjansjoholm@gmail.com")
                    }
                }
            }
        }
    }
}

bintray {
    user = findProperty("bintrayUser") as String?
    key = findProperty("bintrayKey") as String?
    setPublications("corePublication")
    with(pkg) {
        repo = "cdcalc"
        userOrg = "cdcalc"
        name = "cdcalc"
        websiteUrl = "https://github.com/cdcalc/cdcalc"
        issueTrackerUrl = "https://github.com/cdcalc/cdcalc/issues"
        vcsUrl = "https://github.com/cdcalc/cdcalc"
        setLicenses("MIT")
        githubRepo = "cdcalc/cdcalc"
        githubReleaseNotesFile = "README.md"
        publish = true
        with(version) {
            released = ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT)
        }
    }
}
