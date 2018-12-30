package com.github.cdcalc.gitflow

import com.github.cdcalc.*
import com.github.cdcalc.data.SemVer
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Constants
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MergeRequestTestes {
    lateinit var sut: Calculate
    lateinit var git: Git

    @BeforeEach
    fun before() {
        git = initGitFlow()
        sut = Calculate(git, CalculateSettings(), ::standAloneTestConfiguration)
    }

    @Test
    fun should_extract_alpha_version_from_branch_name() {
        git.checkout("merge-requests/2", true)
        git.createCommit()

        val result = sut.gitFacts()

        val head = git.repository.resolve(Constants.HEAD)
        assertEquals(SemVer(1,1,0, listOf("alpha", head.abbreviate(7).name().toUpperCase())), result.semVer)
    }

    @Test
    fun should_calulate_alpha_version_of_merge_request() {
        git.checkout("merge-requests/1", true)
        git.createCommit()
        git.createCommit()
        git.createCommit()

        git.checkout("develop")
        git.createCommit()
        git.createTaggedCommit("v1.1.0-rc.0")

        git.checkout("merge-requests/1")
        git.merge("develop")

        val result = sut.gitFacts()

        val head = git.repository.resolve(Constants.HEAD)
        assertEquals(SemVer(1,2,0, listOf("alpha", head.abbreviate(7).name().toUpperCase())), result.semVer)
    }
}

class ReleaseBranchTests {
    lateinit var sut: Calculate
    lateinit var git: Git

    @BeforeEach
    fun before() {
        git = initGitFlow()
        sut = Calculate(git, CalculateSettings(), ::standAloneTestConfiguration)
    }

    @Test fun should_create_rc_0_when_branched_from_develop() {
        git.checkout("develop").createCommit().createCommit()
        git.checkout("release/2.0.0", true)

        val result = sut.gitFacts()

        assertEquals("2.0.0-rc.0", result.semVer.toString())
    }

    @Test fun should_resolve_rc0_when_rc_tag_exists() {
        git.checkout("develop").createCommit().createCommit()
        git.checkout("release/2.0.0", true)
        git.tag().let {
            it.name = "v2.0.0-rc.0"
            it.message = "v2.0.0-rc.0"
            it.call()
        }

        val result = sut.gitFacts()

        assertEquals("2.0.0-rc.0", result.semVer.toString())
    }

    @Test fun should_resolve_master_branch() {
        git.checkout("develop").createCommit().createCommit()
        git.checkout("release/2.0.0", true)
        git.tag().let {
            it.message = "v2.0.0-rc.0"
            it.name = "v2.0.0-rc.0"
            it.call()
        }

        git.createCommit().createCommit()
        val result = sut.gitFacts()

        assertEquals("2.0.0-rc.2", result.semVer.toString())
    }
}
