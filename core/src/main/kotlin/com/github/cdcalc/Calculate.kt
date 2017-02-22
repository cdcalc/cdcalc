package com.github.cdcalc

import com.github.cdcalc.data.SemVer
import com.github.cdcalc.data.tag
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Constants

class Calculate(val git: Git, val calculateConfiguration: CalculateSetting = CalculateSetting()) {

    fun gitFacts(): GitFacts {
        val branch = git.repository.branch

        val semVer: SemVer = (com.github.cdcalc.strategy.findBranchStrategy(branch, calculateConfiguration.trackOrigin))(git, CalculateConfiguration(branch))

        val resolve = git.repository.resolve(Constants.HEAD)
        println(resolve.name)

        val mapOf = mapOf(Pair("sha", resolve.name))
        val gitFacts = GitFacts(branch, semVer.tag(), 0, semVer, mapOf)

        sendBuildNumberToCI(gitFacts)

        return gitFacts
    }

    fun sendBuildNumberToCI(gitFacts: GitFacts) {
        // TODO: this output is TC specific and need to be extracted later on.
        println("##teamcity[buildNumber '${gitFacts.semVer}']")
    }
}

data class CalculateSetting(val trackOrigin: String = "")

