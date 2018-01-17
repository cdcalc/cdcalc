package com.github.cdcalc

import com.github.cdcalc.configuration.EnvironmentConfiguration
import com.github.cdcalc.configuration.resolveEnvironmentConfiguration
import com.github.cdcalc.data.SemVer
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Constants




class Calculate(
        val git: Git,
        @Suppress("unused") val calculateConfiguration: CalculateSetting = CalculateSetting(),
        private val resolveEnvironmentConfiguration: (Git) -> EnvironmentConfiguration = resolveEnvironmentConfiguration()
) {
    fun gitFacts(): GitFacts {
        val (buildEnvironment, branch) = resolveEnvironmentConfiguration(git)

        println("Resolved branch $branch running in the context of $buildEnvironment")

        val semVer: SemVer = (com.github.cdcalc.strategy.findBranchStrategy(branch))(git, CalculateConfiguration(branch))
        val resolve = git.repository.resolve(Constants.HEAD)

        val mapOf = mapOf(Pair("sha", resolve.name))
        val gitFacts = GitFacts(branch, 0, semVer, mapOf)

        sendBuildNumberToCI(gitFacts)

        return gitFacts
    }

    fun sendBuildNumberToCI(gitFacts: GitFacts) {
        // TODO: this output is TC specific and need to be extracted later on.
        println("##teamcity[buildNumber '${gitFacts.semVer}']")
    }
}

data class CalculateSetting(val trackOrigin: String = "")

