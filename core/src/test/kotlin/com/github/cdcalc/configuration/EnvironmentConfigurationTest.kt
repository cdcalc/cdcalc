package com.github.cdcalc.configuration

import com.github.cdcalc.checkout
import com.github.cdcalc.initGitFlow
import org.eclipse.jgit.api.Git
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class EnvironmentConfigurationTest {
    lateinit var git: Git

    @BeforeEach
    fun before() {
        git = initGitFlow()
    }

    @Test
    fun `Should resolve stand alone configuration`() {
        val resolveConfiguration = resolveEnvironmentConfiguration(environment = { null })
        git.checkout("develop")

        val configuration = resolveConfiguration(git)

        assertEquals(EnvironmentConfiguration(BuildEnvironment.StandAlone, "develop"), configuration)
    }

    @Test fun `Should resolve Travis configuration`() {
        val resolveConfiguration = resolveEnvironmentConfiguration(
                fakeEnvironment(mapOf(Pair("TRAVIS", "true"), Pair("TRAVIS_BRANCH", "master")))
        )

        val master = git.repository.resolve("master")
        git.checkout().setName(master.name).call()

        val configuration = resolveConfiguration(git)

        assertEquals(EnvironmentConfiguration(BuildEnvironment.Travis, "master"), configuration)
    }

    @Test fun `Should resolve GitLab configuration`() {
        val resolveConfiguration = resolveEnvironmentConfiguration(
                fakeEnvironment(mapOf(Pair("GITLAB_CI", "true"), Pair("CI_COMMIT_REF_NAME", "master")))
        )

        val master = git.repository.resolve("master")
        git.checkout().setName(master.name).call()

        val configuration = resolveConfiguration(git)

        assertEquals(EnvironmentConfiguration(BuildEnvironment.GitLab, "master"), configuration)
    }

    @Test fun `Should resolve TeamCity configuration`() {
        val resolveConfiguration = resolveEnvironmentConfiguration(
                fakeEnvironment(mapOf(Pair("TEAMCITY_VERSION", "10")))
        )
        git.checkout("master")

        val configuration = resolveConfiguration(git)

        assertEquals(EnvironmentConfiguration(BuildEnvironment.TeamCity, "master"), configuration)
    }

    fun fakeEnvironment(variables: Map<String, String>): (String) -> String? {
        return {
            variables[it]
        }
    }


}