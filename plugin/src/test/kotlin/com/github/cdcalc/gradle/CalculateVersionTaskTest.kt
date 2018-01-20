package com.github.cdcalc.gradle

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.PersonIdent
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome.SUCCESS
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.slf4j.LoggerFactory
import java.io.File
import kotlin.test.assertTrue

class CalculateVersionTaskFunctionalTest {
    private val log = LoggerFactory.getLogger(CalculateVersionTaskFunctionalTest::class.java)

    @Rule @JvmField val testProjectDir = TemporaryFolder()
    private lateinit var buildFile: File

    @Before fun before() {
        buildFile = testProjectDir.newFile("build.gradle")
        log.debug("Gradle file:" + buildFile.absolutePath)
    }

    @Test fun `Should add an extra property for version at calculateVersion`() {
        val content = """
            plugins {
              id "com.github.cdcalc"
            }

            cdcalc {
                repository '.git'
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

        val result = GradleRunner.create()
                .withPluginClasspath()
                .withProjectDir(testProjectDir.root)
                .withArguments("printVersion", "-Duser.dir=${testProjectDir.root}")
                .withDebug(true)
                .build()

        val task = result.task(":printVersion")!!
        assertEquals(task.outcome, SUCCESS)
        assertTrue(result.output.contains("assertVersion[1.2.3]"), "assertVersion[1.2.3] should be present in: ${result.output}")
    }

    @Test fun `Should add an extra property for branch at calculateVersion`() {
        val content = """
            plugins {
              id "com.github.cdcalc"
            }

            cdcalc {
                repository '.git'
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

        val result = GradleRunner.create()
                .withPluginClasspath()
                .withProjectDir(testProjectDir.root)
                .withArguments("printBranch", "-Duser.dir=${testProjectDir.root}")
                .withDebug(true)
                .build()

        val task = result.task(":printBranch")!!
        assertEquals(task.outcome, SUCCESS)
        assertTrue(result.output.contains("assertBranch[master]"), "assertBranch[master] should be present in: ${result.output}")
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