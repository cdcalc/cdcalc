package com.github.cdcalc.gitflow

import com.github.cdcalc.*
import org.eclipse.jgit.api.Git
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class ReleaseBranchTests {
    lateinit var sut: Calculate
    lateinit var git: Git

    @Before
    fun before() {
        git = initGitFlow()
        sut = Calculate(git)
    }

    @Test fun should_resolve_master_branch() {
        git.checkout("develop").createCommit().createCommit()
        git.checkout("release/2.0.0", true)
                .createCommit().createCommit()

        val result = sut.gitFacts()

        assertEquals("2.0.0-rc.2", result.semVer.toString())
    }}