package com.github.cdcalc

import com.github.cdcalc.git.taggedCommits
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Constants
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CutReleaseBranchTest {
    lateinit var sut: CutReleaseBranch
    lateinit var git: Git

    @BeforeEach
    fun before() {
        git = initGitFlow()
        sut = CutReleaseBranch(git)
    }

    @AfterEach fun after() {
        git.close()
    }

    @Test()
    fun shouldThrowIfTryingToCreateReleaseBranchFromDevelop() {
        git.checkout("master")

        assertThrows<InvalidBranchException> { sut.cutReleaseBranch() }
    }

    @Test fun shouldBeAbleToCutReleaseBranch() {
        sut.cutReleaseBranch()

        val releaseBranch = sut.git.branchList().call().single {
            it.name == "refs/heads/release/1.1.0" }

        val head = git.repository.resolve(Constants.HEAD)

        assertEquals(head, releaseBranch.objectId)
    }

    @Test fun shouldCreatePreReleaseTag() {
        sut.cutReleaseBranch()

        val head = git.repository.resolve(Constants.HEAD)
        val commitTag = git.taggedCommits()[head]

        assertEquals("refs/tags/v1.1.0-rc.0", commitTag!!.tagName)
    }
}
