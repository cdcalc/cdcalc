package com.github.cdcalc.gitflow

import com.github.cdcalc.Calculate
import com.github.cdcalc.Tag
import com.github.cdcalc.*
import org.eclipse.jgit.api.Git
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class MasterBranchTests {
    lateinit var sut: Calculate
    lateinit var git: Git

    @Before
    fun before() {
        git = initGitFlow()
        sut = Calculate(git)
    }

    @Test
    fun should_resolve_master_branch() {
        git.checkout("master")

        git.prettyLog()
        val result = sut.gitFacts()

        assertEquals("master", result.branch)
        assertEquals(Tag(1, 0, 0), result.tag)
        assertEquals(0, result.ahead)
    }


    @Test
    fun should_resolve_master_branch_new_version() {
        git.checkout("master")
        git.createCommit().createCommit()
        git.tag().setName("v1.3.37").setMessage("Release 1.3.37").call()

        git.prettyLog()
        val result = sut.gitFacts()

        assertEquals("master", result.branch)
        assertEquals(Tag(1, 3, 37), result.tag)
        assertEquals(0, result.ahead)
    }

}