plugins {
    id("maven-publish")
    id("java-gradle-plugin")
    id("com.gradle.plugin-publish") version("0.10.0")
}

dependencies {
    implementation("org.eclipse.jgit:org.eclipse.jgit:5.2.0.201812061821-r")
    implementation(gradleApi())
    testImplementation(gradleTestKit())
}

tasks {
    jar {
        baseName = "com.github.cdcalc.gradle.plugin"
    }

    test {
        // NOTE: We're using the TRAVIS environment variable in the production code but it should not be set for the
        // functional tests. This would be problematic if we should have functional tests for Travis
        environment("TRAVIS", "false")
    }
}

gradlePlugin {
    plugins {
        register("calculateSemVerPlugin") {
            id = "com.github.cdcalc"
            displayName = "Calculate SemVer Plugin"
            implementationClass = "com.github.cdcalc.gradle.CalculateSemVerPlugin"
        }
    }
}

pluginBundle {
    website = "https://github.com/cdcalc/cdcalc"
    vcsUrl = "https://github.com/cdcalc/cdcalc"
    description = "Calculate version for upcoming releases."
    tags = listOf("semver")
}