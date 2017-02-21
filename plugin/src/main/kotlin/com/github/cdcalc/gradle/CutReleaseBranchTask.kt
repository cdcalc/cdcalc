package com.github.cdcalc.gradle

import com.github.cdcalc.CutReleaseBranch
import org.eclipse.jgit.api.Git
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File

open class CutReleaseBranchTask : DefaultTask() {
    override fun getDescription(): String {
        return "Will cut a release branch and create a release candidate tag"
    }

    override fun getGroup(): String {
        return "Release management"
    }

    @Suppress("unused")
    @TaskAction fun cutReleaseBranch() {
        val config: CDCalcExtensions = project.extensions.findByType(CDCalcExtensions::class.java) ?: CDCalcExtensions()
        val git = Git.open(File(config.repository))

        CutReleaseBranch(git).cutReleaseBranch()
    }
}