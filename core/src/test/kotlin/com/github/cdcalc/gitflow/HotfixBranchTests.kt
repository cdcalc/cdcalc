package com.github.cdcalc.gitflow;

import com.github.cdcalc.*
import org.eclipse.jgit.api.Git
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class HotfixBranchTests {
    lateinit var sut: Calculate
    lateinit var git:Git

    @BeforeEach
    fun before() {
        git = initGitFlow()
        sut = Calculate(git, CalculateSettings(), ::standAloneTestConfiguration)
    }

    @Test
    fun should_resolve_master_branch() {
        git.checkout("master")
        git.checkout("hotfix/1.0.1", true)
                .createCommit().createCommit()

        val result: GitFacts = sut.gitFacts()
        assertEquals("1.0.1-rc.2", result.semVer.toString())
    }

    @Test
    fun should_not_count_individual_commits_in_merged() {
        git.checkout("master")
        git.checkout("hotfix/1.0.1", true)
                .createCommit().createCommit()

        git.checkout("feature/foo", true).createCommit().createCommit().createCommit()
        git.checkout("hotfix/1.0.1")
        git.merge("feature/foo")
        git.createCommit()

        val result: GitFacts = sut.gitFacts()

        assertEquals("1.0.1-rc.4", result.semVer.toString())
    }
}