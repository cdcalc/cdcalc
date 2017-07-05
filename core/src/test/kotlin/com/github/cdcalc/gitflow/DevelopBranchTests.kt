package com.github.cdcalc.gitflow

import com.github.cdcalc.*
import com.github.cdcalc.data.SemVer
import com.github.cdcalc.strategy.TrackingException
import org.eclipse.jgit.api.Git
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class DevelopBranchTests {
    lateinit var sut: Calculate
    lateinit var git: Git

    @Before
    fun before() {
        git = initGitFlow()
        sut = Calculate(git)
    }

    @Test(expected = TrackingException::class)
    fun should_resolve_master_branch() {
        sut.gitFacts()
    }

    @Test(expected = TrackingException::class)
    fun should_stay_on_same_version_independent_on_tag() {
        git.tag().let {
            it.name = "v1.1.0-rc.0"
            it.message = "RC v1.1.0-rc.0"
            it.call()
        }

        sut.gitFacts()
    }

    @Test
    fun should_bump_by_rc_tag() {
        git.tag().let {
            it.name = "v1.1.0-rc.0"
            it.message = "RC v1.1.0-rc.0"
            it.call()
        }
        git.createCommit()

        val result: GitFacts = sut.gitFacts()

        assertEquals(SemVer(1,2,0, listOf("beta", "0")), result.semVer)
    }

    @Test
    fun should_resolve_master_branch_rc() {
        git.createTaggedCommit("v1.2.3-rc.0")
        git.createCommit()
        git.createCommit()

        val result: SemVer = sut.gitFacts().semVer
        assertEquals("1.3.0-beta.1", result.toString())
    }

    @Test
    fun should_treat_merge_commit_as_one() {
        git.createTaggedCommit("v1.2.3-rc.0")

        git.checkout("new-feature", true)
        git.createCommit()
        git.createCommit()

        git.checkout("develop")
        git.merge("new-feature")

        git.prettyLog()
        val result: SemVer = sut.gitFacts().semVer
        assertEquals("1.3.0-beta.0", result.toString())
    }

    @Test
    fun should_handle_reintegrated_develop() {
        git.createTaggedCommit("v1.2.3-rc.0")

        git.checkout("new-feature", true)
        git.createCommit()
        git.createCommit()

        git.checkout("develop")
        git.createCommit()

        git.checkout("new-feature")
        git.merge("develop")

        git.checkout("develop")
        git.merge("new-feature")

        git.prettyLog()
        val result: SemVer = sut.gitFacts().semVer
        assertEquals("1.3.0-beta.1", result.toString())
    }

}