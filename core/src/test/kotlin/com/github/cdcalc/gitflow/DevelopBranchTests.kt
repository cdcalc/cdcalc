package com.github.cdcalc.gitflow

import com.github.cdcalc.*
import com.github.cdcalc.data.SemVer
import com.github.cdcalc.strategy.TrackingException
import org.eclipse.jgit.api.Git
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DevelopBranchTests {
    lateinit var sut: Calculate
    lateinit var git: Git

    @BeforeEach
    fun before() {
        git = initGitFlow()
        sut = Calculate(git, CalculateSettings(), ::standAloneTestConfiguration)
    }

    @Test
    fun should_resolve_master_branch() {
        assertThrows<TrackingException> {
            sut.gitFacts()
        }
    }

    @Test
    fun should_stay_on_same_version_independent_on_tag() {
        git.tag().let {
            it.name = "v1.1.0-rc.0"
            it.message = "RC v1.1.0-rc.0"
            it.call()
        }

        assertThrows<TrackingException> {
            sut.gitFacts()
        }
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

        val result: SemVer = sut.gitFacts().semVer
        assertEquals("1.3.0-beta.1", result.toString())
    }

    @Test
    fun should_handle_release() {
        git.createTaggedCommit("v1.2.3-rc.0")
        git.checkout("release/1.2.3", true).createCommit()
        git.checkout("develop").createCommit()

        git.checkout("develop").merge("release/1.2.3")
        git.checkout("master").merge("release/1.2.3")
        git.tag().let {
            it.name = "v1.2.3"
            it.message = "v1.2.3"
            it.call()
        }

        git.checkout("develop")
        val result: SemVer = sut.gitFacts().semVer
        assertEquals("1.3.0-beta.1", result.toString())
    }

    @Test
    fun should_handle_hotfix() {
        git.createTaggedCommit("v1.2.3-rc.0")
        git.checkout("release/1.2.3", true).createCommit()
        git.checkout("develop").createCommit()

        git.checkout("develop").merge("release/1.2.3")
        git.checkout("master").merge("release/1.2.3")
        git.tag().let {
            it.name = "v1.2.3"
            it.message = "v1.2.3"
            it.call()
        }

        git.checkout("hotfix/1.2.4", true)
        git.createCommit().createCommit()

        git.checkout("master").merge("hotfix/1.2.4")
        git.tag().let {
            it.name = "v1.2.4"
            it.message = "v1.2.4"
            it.call()
        }

        git.checkout("develop").merge("hotfix/1.2.4")

        val result: SemVer = sut.gitFacts().semVer
        assertEquals("1.3.0-beta.2", result.toString())
    }

}