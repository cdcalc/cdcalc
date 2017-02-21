package com.github.cdcalc.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

open class CalculateSemVerPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.tasks.create("calculateVersion", CalculateVersionTask::class.java)
        project.tasks.create("cutReleaseBranch", CutReleaseBranchTask::class.java)
        project.extensions.create("cdcalc", CDCalcExtensions::class.java)
    }
}

