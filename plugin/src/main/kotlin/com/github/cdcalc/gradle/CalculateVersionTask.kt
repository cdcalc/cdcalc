package com.github.cdcalc.gradle

import com.github.cdcalc.Calculate
import com.github.cdcalc.CalculateSettings
import com.github.cdcalc.GitFacts
import org.eclipse.jgit.api.Git
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

open class CalculateVersionTask : DefaultTask() {
    init {
        group = "Release management"
        description = "Will calculate the upcoming version tracking branches and tags"
    }

    @TaskAction fun calculateVersion() {
        val config: CDCalcExtensions = project.extensions.findByType(CDCalcExtensions::class.java) ?: CDCalcExtensions()
        val git = Git.open(config.repository)
        val gitFacts = Calculate(git, CalculateSettings(versionFile = config.versionFile)).gitFacts()

        addProperties(gitFacts)
    }

    private fun addProperties(facts: GitFacts) {
        extensions.extraProperties["branch"] = facts.branch
        extensions.extraProperties["version"] = facts.semVer.toString()
    }
}
