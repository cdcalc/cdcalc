package com.github.cdcalc.gradle

import com.github.cdcalc.Calculate
import com.github.cdcalc.CalculateSetting
import com.github.cdcalc.GitFacts
import org.eclipse.jgit.api.Git
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File

open class CalculateVersionTask : DefaultTask() {
    init {
        group = "Release management"
        description = "Will calculate the upcoming version tracking branches and tags"
    }

    @TaskAction fun calculateVersion() {
        val config: CDCalcExtensions = project.extensions.findByType(CDCalcExtensions::class.java) ?: CDCalcExtensions()
        val git = Git.open(File(config.repository))
        val gitFacts = Calculate(git, CalculateSetting(config.trackOrigin)).gitFacts()

        config.calculatedVersion = gitFacts.semVer.toString()
        config.branch = gitFacts.branch

        addProperties(gitFacts)
    }

    private fun addProperties(facts: GitFacts) {
        extensions.extraProperties["branch"] = facts.branch
        extensions.extraProperties["version"] = facts.semVer.toString()
    }
}
