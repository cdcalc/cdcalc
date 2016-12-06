package com.github.cdcalc.gradle

import com.github.cdcalc.Calculate
import org.eclipse.jgit.api.Git
import org.gradle.api.DefaultTask
import org.gradle.api.GradleScriptException
import org.gradle.api.tasks.TaskAction
import java.io.File

open class CalculateVersionTask : DefaultTask() {
    @Suppress("unused")
    @TaskAction fun calculateVersion() {
        val config: CDCalcExtensions = project.extensions.findByType(CDCalcExtensions::class.java) ?: CDCalcExtensions()

        val git = Git.open(File(config.repository))
        try {
            val gitFacts = Calculate(git).gitFacts()
            config.calculatedVersion = gitFacts.semVer.toString()
        } catch (e: Throwable) {
            println("Something when bad let's fallback 0.0.1" + e.message)
            config.calculatedVersion = "0.0.1"
        }
    }
}