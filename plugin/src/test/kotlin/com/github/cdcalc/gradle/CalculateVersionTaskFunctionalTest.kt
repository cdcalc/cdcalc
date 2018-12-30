package com.github.cdcalc.gradle

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.PersonIdent
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome.SUCCESS
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.support.io.TempDirectory
import java.io.File
import java.nio.file.Path

@ExtendWith(TempDirectory::class)
class CalculateVersionTaskFunctionalTest {
    @BeforeEach
    fun beforeEach(@TempDirectory.TempDir tempDirectory: Path) {
        testDir = tempDirectory
    }

    private lateinit var testDir: Path

    @Test
    fun `Should output version`() {
        testDir.createFile("build.gradle") {
            """
                plugins {
                    id "com.github.cdcalc"
                }
            """
        }

        gitInit(testDir.toFile())
                .commit("Initial commit")
                .tag("v1.2.3")

        val result = runGradle(testDir.toFile(), "calculateVersion")

        val task = result.task(":calculateVersion")!!
        assertEquals(task.outcome, SUCCESS)
        assertContains("1.2.3", result.output)
    }

    @Test
    fun `Should set the project version`() {
        testDir.createFile("build.gradle") {
            """
                plugins {
                  id "com.github.cdcalc"
                }

                task printVersion(dependsOn: 'calculateVersion') {
                    doLast {
                        println "assertVersion[${'$'}project.version]"
                    }
                }
            """
        }

        gitInit(testDir.toFile())
                .commit("Initial commit")
                .tag("v1.2.3")

        val result = runGradle(testDir.toFile(), "printVersion")

        val task = result.task(":printVersion")!!
        assertEquals(task.outcome, SUCCESS)
        assertContains("assertVersion[1.2.3]", result.output)
    }

    @Test
    fun `Should add an extra property for version at calculateVersion`() {
        testDir.createFile("build.gradle") {
            """
                plugins {
                  id "com.github.cdcalc"
                }

                task printVersion(dependsOn: 'calculateVersion') {
                    doLast {
                        println "assertVersion[${'$'}calculateVersion.version]"
                    }
                }
            """
        }

        gitInit(testDir.toFile())
                .commit("Initial commit")
                .tag("v1.2.3")

        val result = runGradle(testDir.toFile(), "printVersion")

        val task = result.task(":printVersion")!!
        assertEquals(task.outcome, SUCCESS)
        assertContains("assertVersion[1.2.3]", result.output)
    }

    @Test
    fun `Should add an extra property for branch at calculateVersion`() {
        testDir.createFile("build.gradle") {
            """
                plugins {
                  id "com.github.cdcalc"
                }

                task printBranch(dependsOn: 'calculateVersion') {
                    doLast {
                        println "assertBranch[${'$'}calculateVersion.branch]"
                    }
                }
            """
        }

        gitInit(testDir.toFile())
                .commit("Initial commit")
                .tag("v1.2.3")

        val result = runGradle(testDir.toFile(), "printBranch")

        val task = result.task(":printBranch")!!
        assertEquals(task.outcome, SUCCESS)
        assertContains("assertBranch[master]", result.output)
    }

    @Test
    fun `Should be possible to specify the git repository`() {
        val buildFolder = testDir.newFolder("subdir")
        testDir.createFile("subdir/build.gradle") {
            """
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
            """
        }

        gitInit(testDir.toFile())
                .commit("Initial commit")
                .tag("v4.1.7")

        val result = runGradle(buildFolder, "printVersion")

        val task = result.task(":calculateVersion")!!
        assertEquals(task.outcome, SUCCESS)
        assertContains("assertVersion[4.1.7]", result.output)
    }

    @Test
    fun `Should be able to write the version to file`() {
        testDir.createFile("build.gradle") {
            """
                plugins {
                  id "com.github.cdcalc"
                }

                cdcalc {
                    versionFile = file('.version')
                }
            """
        }

        gitInit(testDir.toFile())
                .commit("Initial commit")
                .tag("3.12.1")

        runGradle(testDir.toFile(), "calculateVersion")

        val file = File(testDir.toFile(), ".version")
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
                .forwardOutput()
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

private fun Path.createFile(name: String, content: () -> String) {
    val file = this.resolve(name).toFile()

    file.writeText(content().trimIndent())
}

private fun Path.newFile(name: String): File {
    return this.resolve(name).toFile()
}

private fun Path.newFolder(name: String): File {
    val folder = this.newFile(name)
    folder.mkdirs()
    return folder
}