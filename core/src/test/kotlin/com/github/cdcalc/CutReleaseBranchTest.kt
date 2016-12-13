package com.github.cdcalc

import com.github.cdcalc.git.taggedCommits
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Constants
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class CutReleaseBranchTest {
    lateinit var sut: CutReleaseBranch
    lateinit var git: Git

    @Before fun before() {
        git = initGitFlow()
        sut = CutReleaseBranch(git)
    }

    @After fun after() {
        git.close()
    }

    @Test(expected = InvalidBranchException::class)
    fun shouldThrowIfTryingToCreateReleaseBranchFromDevelop() {
        git.checkout("master")

        sut.cutReleaseBranch()
    }

    @Test fun shouldBeAbleToCutReleaseBranch() {
        sut.cutReleaseBranch()

        val releaseBranch = sut.git.branchList().call().single {
            it.name == "refs/heads/release/1.1.0" }

        val head = git.repository.resolve(Constants.HEAD)
        git.prettyLog()

        assertEquals(head, releaseBranch.objectId)
    }

    @Test fun shouldCreatePreReleaseTag() {
        sut.cutReleaseBranch()

        val head = git.repository.resolve(Constants.HEAD)
        val commitTag = git.taggedCommits()[head]

        assertEquals("refs/tags/v1.1.0-rc.0", commitTag!!.tagName)
    }


}
