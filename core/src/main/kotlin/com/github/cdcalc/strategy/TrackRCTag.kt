package com.github.cdcalc.strategy

import com.github.cdcalc.CalculateConfiguration
import com.github.cdcalc.Tag
import com.github.cdcalc.data.SemVer
import com.github.cdcalc.extensions.use
import com.github.cdcalc.git.highestMergedTag
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.revwalk.RevWalk


/**
 * Limitations
 * - Tag and commit must match
 * - If not tag exists, we're doomed
 */

data class CommitTag (
    val objectId: ObjectId,
    val tagName: String
)

fun trackRCTag(): (Git, CalculateConfiguration) -> SemVer {
    return { git, branch ->
        val head = git.repository.resolve(Constants.HEAD)

        val branchCommit: Map<ObjectId, CommitTag> = git.tagList().call().map {
            val peel = git.repository.peel(it)
            if (peel.peeledObjectId != null) {
                CommitTag(peel.peeledObjectId, it.name)
            } else {
                CommitTag(it.objectId, it.name)
            }
        }.associateBy(keySelector = {it.objectId}, valueTransform = {it})

        val commitTag: CommitTag = RevWalk(git.repository).use { walk ->
            val headCommit = walk.parseCommit(head)
            walk.highestMergedTag(headCommit, branchCommit.values.sortedByDescending { it -> Tag(it.tagName) }.asSequence())
        }

        val baseTag = Tag(commitTag.tagName)

        val bumpedTag = baseTag.copy(minor = baseTag.minor + 1, patch = 0)

        if (branch.branch == "develop") {
            SemVer(bumpedTag.major, bumpedTag.minor, bumpedTag.patch, listOf("beta", branch.buildCounter.toString()))
        } else {
            // TODO: this is a naive extraction of merge request id
            val version = branch.branch.replace("merge-requests/", "")
            SemVer(bumpedTag.major, bumpedTag.minor, bumpedTag.patch, listOf("alpha", version))
        }
    }
}