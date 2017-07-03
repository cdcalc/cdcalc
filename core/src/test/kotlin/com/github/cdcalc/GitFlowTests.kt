package com.github.cdcalc

import com.github.cdcalc.data.SemVer
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.MergeCommand
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.revwalk.RevWalkUtils
import org.eclipse.jgit.revwalk.filter.RevFilter
import org.junit.Before
import org.junit.Test
import java.time.Instant
import kotlin.test.assertEquals

class GitFlowTests {
    lateinit var sut: Calculate
    lateinit var git: Git

    @Before fun before() {
        git = initGitFlow()
        sut = Calculate(git)
    }

    @Test fun should_resolve_master_branch() {
        git.checkout("master")

        val result = sut.gitFacts()

        assertEquals("master", result.branch)
    }

    @Test fun should_resovle_closest_reachable_tag() {
        git.checkout("master")

        val result = sut.gitFacts()

        assertEquals(SemVer(1,0,0), result.semVer)

    }

    @Test fun should_find_the_current_tag() {
        git.checkout("master")
                .createTaggedCommit("v1.0.1")

        val result = sut.gitFacts()

        assertEquals(SemVer(1,0,1), result.semVer)
    }

    /* Release branch tests */
    @Test fun should_use_branch_name() {
        git.checkout("release/2.0.0", true)

        val result = sut.gitFacts()

        assertEquals(SemVer(2,0,0, listOf("rc", "0")), result.semVer)
    }

    /* Feature from release branch */
    @Test fun feature_should_use_release_branch() {
        git.checkout("release/2.0.0", true).createCommit()

        val result = sut.gitFacts()

        assertEquals(SemVer(2,0,0, listOf("rc", "0")), result.semVer)

    }

    /* Feature from release branch */

    @Test fun test_git_flow() {
        git.createTaggedCommit("v2.0.0-rc.0")
        git.checkout("release/2.0.0", true).createCommit().createCommit()
        git.checkout("master").merge()
                .setFastForward(MergeCommand.FastForwardMode.NO_FF)
                .setCommit(true)
                .include(git.repository.findRef("release/2.0.0"))
                .call()

        git.tag().setName("v2.0.0").setMessage("Release 2.0.0").call()

        git.checkout("develop").createCommit()
        git.merge()
                .setFastForward(MergeCommand.FastForwardMode.NO_FF)
                .setCommit(true)
                .include(git.repository.findRef("release/2.0.0"))
                .call()

        git.branchDelete().setBranchNames("release/2.0.0").call()
        git.checkout("develop")

        val result = sut.gitFacts()

        assertEquals("2.1.0-beta.1", result.semVer.toString())
    }


    @Test fun find_latest() {
        git.checkout("develop").createCommit().createCommit()
        git.checkout("feature/foo", true).createCommit().createCommit().createCommit()
        git.checkout("develop").merge()
                .setFastForward(MergeCommand.FastForwardMode.NO_FF)
                .setCommit(true)
                .include(git.repository.findRef("feature/foo"))
                .call()


        git.checkout("master").createCommit()

        val develop: Ref = git.branchList().call().single {
            it.name == "refs/heads/develop"
        }

        val master: Ref = git.branchList().call().single {
            it.name == "refs/heads/master"
        }


        val calculateDivergence = git.calculateDivergence(develop, master)
        println(calculateDivergence)
    }

    @Suppress("UNUSED_VARIABLE")
    @Test fun fetchSomeAuthorData() {

        val revWalk = RevWalk(git.repository)
        val parseCommit = revWalk.parseCommit(git.repository.findRef("master").objectId)


        val authorIdent = parseCommit.getAuthorIdent();
        val authorDate = authorIdent.getWhen();
        val authorTimeZone = authorIdent.getTimeZone();

        Instant.EPOCH
        println(parseCommit.shortMessage)
        println(parseCommit)
        println(Instant.now().epochSecond)
    }


}

fun Git.calculateDivergence(local: Ref, tracking: Ref): CommitCount {
    RevWalk(this.repository).use { walk ->
        val localCommit = walk.parseCommit( local.objectId )
        val trackingCommit = walk.parseCommit( tracking.objectId)

        walk.revFilter = RevFilter.MERGE_BASE
        walk.markStart(localCommit)
        walk.markStart(trackingCommit)
        val mergeBase = walk.next()

        walk.reset()
        walk.revFilter = RevFilter.ALL

        val aheadCount = RevWalkUtils.count(walk, localCommit, mergeBase)
        val behindCount = RevWalkUtils.count(walk, trackingCommit, mergeBase)

        walk.dispose()

        return CommitCount(behindCount, aheadCount)
    }
}
data class CommitCount(val behind: Int, val ahead: Int) {

}

