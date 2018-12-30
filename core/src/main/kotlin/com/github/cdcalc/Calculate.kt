package com.github.cdcalc

import com.github.cdcalc.configuration.BuildEnvironment
import com.github.cdcalc.configuration.EnvironmentConfiguration
import com.github.cdcalc.configuration.resolveEnvironmentConfiguration
import com.github.cdcalc.data.SemVer
import org.eclipse.jgit.api.Git
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File


class Calculate(
        val git: Git,
        private val calculateSettings: CalculateSettings = CalculateSettings(),
        private val resolveEnvironmentConfiguration: (Git) -> EnvironmentConfiguration = resolveEnvironmentConfiguration()
) {
    private val log: Logger = LoggerFactory.getLogger(Calculate::class.java)

    fun gitFacts(printVersion: (String) -> Unit = log::info): GitFacts {
        val environmentConfiguration = resolveEnvironmentConfiguration(git)

        log.info("Resolved branch ${environmentConfiguration.branch} running in the context of ${environmentConfiguration.buildEnvironment}")

        val semVer: SemVer = (com.github.cdcalc.strategy.findBranchStrategy(environmentConfiguration))(git, environmentConfiguration)

        val gitFacts = GitFacts(branch = environmentConfiguration.branch, semVer = semVer)

        outputBuildNumber(environmentConfiguration, gitFacts, printVersion)
        writeVersionToFile(calculateSettings, gitFacts)

        return gitFacts
    }
}

internal fun outputBuildNumber(environmentConfiguration: EnvironmentConfiguration, gitFacts: GitFacts, output: (String) -> Unit) {
    if (environmentConfiguration.buildEnvironment == BuildEnvironment.TeamCity) {
        output("##teamcity[buildNumber '${gitFacts.semVer}']")
    } else {
        output(gitFacts.semVer.toString())
    }
}

internal fun writeVersionToFile(calculateSettings: CalculateSettings, gitFacts: GitFacts) {
    if (calculateSettings.versionFile != null) {
        calculateSettings.versionFile.writeText(gitFacts.semVer.toString())
    }
}

data class CalculateSettings(val versionFile: File? = null)

