package com.github.cdcalc.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

open class CalculateSemVerPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.create("cdcalc", CDCalcExtensions::class.java)

        project.tasks.register ("calculateVersion", CalculateVersionTask::class.java)
        project.tasks.register("cutReleaseBranch", CutReleaseBranchTask::class.java)
    }
}

