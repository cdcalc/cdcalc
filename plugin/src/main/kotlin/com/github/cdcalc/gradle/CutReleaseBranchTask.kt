package com.github.cdcalc.gradle

import com.github.cdcalc.CutReleaseBranch
import org.eclipse.jgit.api.Git
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File

open class CutReleaseBranchTask : DefaultTask() {
    init {
        group = "Release management"
        description = "Will cut a release branch and create a release candidate tag"
    }

    @TaskAction fun cutReleaseBranch() {
        val config: CDCalcExtensions = project.extensions.findByType(CDCalcExtensions::class.java) ?: CDCalcExtensions()
        val git = Git.open(config.repository)

        CutReleaseBranch(git).cutReleaseBranch()
    }
}