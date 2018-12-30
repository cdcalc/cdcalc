package com.github.cdcalc.gitflow

import com.github.cdcalc.*
import com.github.cdcalc.data.SemVer
import org.eclipse.jgit.api.Git
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MasterBranchTests {
    lateinit var sut: Calculate
    lateinit var git: Git

    @BeforeEach
    fun before() {
        git = initGitFlow()
        sut = Calculate(git, CalculateSettings(), ::standAloneTestConfiguration)
    }

    @Test
    fun should_resolve_master_branch() {
        git.checkout("master")

        val result = sut.gitFacts()

        assertEquals("master", result.branch)
        assertEquals(SemVer(1,0,0), result.semVer)
    }

    @Test
    fun should_resolve_master_branch_new_version() {
        git.checkout("master")
        git.createCommit().createCommit()
        git.tag().setName("v1.3.37").setMessage("Release 1.3.37").call()

        val result = sut.gitFacts()

        assertEquals("master", result.branch)
        assertEquals(SemVer(1,3,37), result.semVer)
    }
}