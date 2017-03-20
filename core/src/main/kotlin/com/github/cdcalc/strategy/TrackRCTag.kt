package com.github.cdcalc.strategy

import com.github.cdcalc.CalculateConfiguration
import com.github.cdcalc.Tag
import com.github.cdcalc.data.CommitTag
import com.github.cdcalc.data.SemVer
import com.github.cdcalc.git.taggedCommits
import com.github.cdcalc.git.highestMergedTag
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.revwalk.RevWalkUtils


/**
 * Limitations
 * - Tag and commit must match
 * - If not tag exists, we're doomed
 */
fun trackRCTag(): (Git, CalculateConfiguration) -> SemVer {
    return { git, branch ->
        val head = git.repository.resolve(Constants.HEAD)

        val taggedCommits = git.taggedCommits()

        var aheadCount = 0
        val commitTag: CommitTag = RevWalk(git.repository).use { walk ->
            val headCommit = walk.parseCommit(head)
            val highestMergedTag = walk.highestMergedTag(
                    headCommit,
                    taggedCommits.values.sortedByDescending { it -> Tag(it.tagName) }.asSequence()
            )

            // TODO: we'll need to return this when it's working
            aheadCount = RevWalkUtils.count(walk, headCommit, walk.parseCommit(highestMergedTag.objectId))
            highestMergedTag
        }

        val baseTag = Tag(commitTag.tagName)

        val bumpedTag = baseTag.copy(minor = baseTag.minor + 1, patch = 0)

        if (branch.branch == "develop") {
            SemVer(bumpedTag.major, bumpedTag.minor, bumpedTag.patch, listOf("beta", aheadCount.toString()))
        } else {
            // TODO: this is a naive extraction of merge request id
            val version = branch.branch.replace("merge-requests/", "")
            SemVer(bumpedTag.major, bumpedTag.minor, bumpedTag.patch, listOf("alpha", version))
        }
    }
}