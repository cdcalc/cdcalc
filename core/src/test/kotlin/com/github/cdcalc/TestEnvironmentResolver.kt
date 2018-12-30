package com.github.cdcalc

import com.github.cdcalc.configuration.BuildEnvironment
import com.github.cdcalc.configuration.EnvironmentConfiguration
import org.eclipse.jgit.api.Git

fun standAloneTestConfiguration(git: Git) : EnvironmentConfiguration {
    return EnvironmentConfiguration(BuildEnvironment.StandAlone, git.repository.branch)
}