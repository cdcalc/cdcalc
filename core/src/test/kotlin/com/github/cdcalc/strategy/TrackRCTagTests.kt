package com.github.cdcalc.strategy

import com.github.cdcalc.CalculateConfiguration
import com.github.cdcalc.*
import com.github.cdcalc.data.SemVer
import org.eclipse.jgit.api.Git
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals


class TrackRCTagTests {
    lateinit var sut: (Git, CalculateConfiguration) -> SemVer
    lateinit var git: Git

    @Before
    fun before() {
        git = initGitFlow()
        sut = trackRCTag()
    }

    @Test
    fun should_resolve_master_branch() {
        git.createTaggedCommit("v1.2.3-rc.0")
        git.createCommit()

        val result: SemVer = sut(git, CalculateConfiguration("develop", 1337))

        assertEquals("1.3.0-beta.1337", result.toString())
    }

    @Test
    fun should_extract_alpha_version_from_branch_name() {
        git.checkout("merge-requests/2", true)
        git.createCommit()

        val result = sut(git, CalculateConfiguration("merge-requests/2"))

        assertEquals("1.1.0-alpha.2", result.toString())
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

        val result = sut(git, CalculateConfiguration("merge-requests/1"))

        assertEquals("1.2.0-alpha.1", result.toString())
    }
}
