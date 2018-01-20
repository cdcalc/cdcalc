package com.github.cdcalc

import com.github.cdcalc.configuration.EnvironmentConfiguration
import com.github.cdcalc.configuration.resolveEnvironmentConfiguration
import com.github.cdcalc.data.SemVer
import org.eclipse.jgit.api.Git
import java.io.File


class Calculate(
        val git: Git,
        private val calculateSettings: CalculateSettings = CalculateSettings(),
        private val resolveEnvironmentConfiguration: (Git) -> EnvironmentConfiguration = resolveEnvironmentConfiguration()
) {
    fun gitFacts(): GitFacts {
        val (buildEnvironment, branch) = resolveEnvironmentConfiguration(git)

        println("Resolved branch $branch running in the context of $buildEnvironment")

        val semVer: SemVer = (com.github.cdcalc.strategy.findBranchStrategy(branch))(git, CalculateConfiguration(branch))

        val gitFacts = GitFacts(branch = branch, semVer = semVer)

        sendBuildNumberToCI(gitFacts)
        writeVersionToFile(calculateSettings, gitFacts)

        return gitFacts
    }

    fun sendBuildNumberToCI(gitFacts: GitFacts) {
        // TODO: this output is TC specific and need to be extracted later on.
        println("##teamcity[buildNumber '${gitFacts.semVer}']")
    }
}

internal fun writeVersionToFile(calculateSettings: CalculateSettings, gitFacts: GitFacts) {
    if (calculateSettings.versionFile != null) {
        calculateSettings.versionFile.writeText(gitFacts.semVer.toString())
    }
}

data class CalculateSettings(val versionFile: File? = null)

