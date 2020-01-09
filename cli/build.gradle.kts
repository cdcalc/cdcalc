plugins {
    application
    `maven-publish`
}

dependencies {
    implementation("org.eclipse.jgit:org.eclipse.jgit:5.2.0.201812061821-r")
    implementation("ch.qos.logback:logback-classic:1.2.3")
}

application {
    mainClassName = "com.github.cdcalc.cli.ApplicationKt"
}

publishing {
    publications {
        register<MavenPublication>("cli") {
            from(components["java"])
            groupId = "com.github.cdcalc"
            artifactId = "cdcalc-cli"
        }
    }
}

