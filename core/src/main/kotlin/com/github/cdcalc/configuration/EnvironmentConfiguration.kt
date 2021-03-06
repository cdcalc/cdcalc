package com.github.cdcalc.configuration

import org.eclipse.jgit.api.Git

data class EnvironmentConfiguration(val buildEnvironment: BuildEnvironment, val branch: String, val tag: String? = null)

fun resolveEnvironmentConfiguration(environment: (String) -> String? = ::getEnvironmentVariable): (git : Git) -> EnvironmentConfiguration {
    return { git ->
        buildConfiguration(git, environment)
    }
}

private fun buildConfiguration(git: Git, environment: (String) -> String?) : EnvironmentConfiguration {
    if ("true" == environment("TRAVIS")) {
        return EnvironmentConfiguration(BuildEnvironment.Travis, environment("TRAVIS_BRANCH")!!, environment("TRAVIS_TAG"))
    }

    if ("true" == environment("GITLAB_CI")) {
        return EnvironmentConfiguration(BuildEnvironment.GitLab, environment("CI_COMMIT_REF_NAME")!!)
    }

    if (!environment("TEAMCITY_VERSION").isNullOrEmpty()) {
        return EnvironmentConfiguration(BuildEnvironment.TeamCity, git.repository.branch)
    }

    return EnvironmentConfiguration(BuildEnvironment.StandAlone, git.repository.branch)
}

private fun getEnvironmentVariable(key: String) : String? {
    return System.getenv(key)
}