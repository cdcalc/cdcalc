package com.github.cdcalc.gradle

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.PersonIdent
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome.SUCCESS
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import kotlin.test.assertTrue

class CalculateVersionTaskFunctionalTest {
    @Rule @JvmField val testProjectDir = TemporaryFolder()

    @Test fun `Should add an extra property for version at calculateVersion`() {
        val buildFile = testProjectDir.newFile("build.gradle")

        val content = """
            plugins {
              id "com.github.cdcalc"
            }

            task printVersion(dependsOn: 'calculateVersion') {
                doLast {
                    println "assertVersion[${'$'}calculateVersion.version]"
                }
            }
        """.trimIndent()
        buildFile.writeText(content)

        gitInit(testProjectDir.root)
                .commit("Initial commit")
                .tag("v1.2.3")

        val result = runGradle(testProjectDir.root, "printVersion")

        val task = result.task(":printVersion")!!
        assertEquals(task.outcome, SUCCESS)
        assertContains("assertVersion[1.2.3]", result.output)
    }

    @Test fun `Should add an extra property for branch at calculateVersion`() {
        val buildFile = testProjectDir.newFile("build.gradle")

        val content = """
            plugins {
              id "com.github.cdcalc"
            }

            task printBranch(dependsOn: 'calculateVersion') {
                doLast {
                    println "assertBranch[${'$'}calculateVersion.branch]"
                }
            }
        """.trimIndent()
        buildFile.writeText(content)

        gitInit(testProjectDir.root)
                .commit("Initial commit")
                .tag("v1.2.3")

        val result = runGradle(testProjectDir.root, "printBranch")

        val task = result.task(":printBranch")!!
        assertEquals(task.outcome, SUCCESS)
        assertContains("assertBranch[master]", result.output)
    }

    @Test fun `Should be possible to specify the git repository`() {
        val buildFolder = testProjectDir.newFolder("subdir")
        val buildFile = testProjectDir.newFile("subdir/build.gradle")

        val content = """
            plugins {
              id "com.github.cdcalc"
            }

            cdcalc {
                repository = file('../.git')
            }

            task printVersion(dependsOn: 'calculateVersion') {
                doLast {
                    println "assertVersion[${'$'}calculateVersion.version]"
                }
            }
        """.trimIndent()
        buildFile.writeText(content)

        gitInit(testProjectDir.root)
                .commit("Initial commit")
                .tag("v4.1.7")

        val result = runGradle(buildFolder, "printVersion")

        val task = result.task(":calculateVersion")!!
        assertEquals(task.outcome, SUCCESS)
        assertContains("assertVersion[4.1.7]", result.output)
    }

    @Test fun `Should be able to write the version to file`() {
        val buildFile = testProjectDir.newFile("build.gradle")

        val content = """
            plugins {
              id "com.github.cdcalc"
            }

            cdcalc {
                versionFile = file('.version')
            }
        """.trimIndent()
        buildFile.writeText(content)

        gitInit(testProjectDir.root)
                .commit("Initial commit")
                .tag("3.12.1")

        runGradle(testProjectDir.root, "calculateVersion")

        val file = File(testProjectDir.root, ".version")
        assertEquals("3.12.1", file.readText())
    }

    private fun assertContains(expected: String, actual: String) {
        assertTrue(actual.contains(expected), "$expected should be present in: $actual")
    }

    private fun runGradle(projectDir: File, vararg arguments: String): BuildResult {
        val defaultArguments = listOf("-Duser.dir=$projectDir")

        return GradleRunner.create()
                .withPluginClasspath()
                .withProjectDir(projectDir)
                .withArguments(defaultArguments.plus(arguments))
                .withDebug(true)
                .build()
    }

    private fun gitInit(root: File): Git = Git.init().setDirectory(root).call()

    private fun Git.tag(tag: String): Git {
        this.tag().setName(tag).call()
        return this
    }

    private fun Git.commit(message: String = "create file"): Git {
        val commit = this.commit()
        commit.author = PersonIdent("Örjan Sjöholm", "orjan.sjoholm@gmail.com")
        commit.setAll(true)
        commit.message = message
        commit.call()
        return this
    }
}