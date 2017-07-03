package com.github.cdcalc.git

import com.github.cdcalc.data.CommitTag
import com.github.cdcalc.data.SemVer
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevWalk

fun Git.taggedCommits(): Map<ObjectId, CommitTag> {
    val branchCommit: Map<ObjectId, CommitTag> = this.tagList().call().map {
        val peel = this.repository.peel(it)
        if (peel.peeledObjectId != null) {
            CommitTag(peel.peeledObjectId, it.name)
        } else {
            CommitTag(it.objectId, it.name)
        }
    }.associateBy(keySelector = {it.objectId}, valueTransform = {it})

    return branchCommit
}

fun Git.semVerTags(): List<RefSemVer> {
    return this.tagList()
            .call()
            .map {
                RefSemVer(it, SemVer.parse(it.name))
            }
}

// https://github.com/centic9/jgit-cookbook/blob/master/src/main/java/org/dstadler/jgit/porcelain/ListTags.java
fun Git.peeledObjectId(ref: Ref): ObjectId {
    val peel = this.repository.peel(ref)
    val objectId = peel.peeledObjectId ?: ref.objectId
    return objectId
}

fun <T> Git.processTags(tags: List<RefSemVer>, filter: (walk: RevWalk, headCommit: RevCommit, Sequence<RevCommitSemVer>) -> T) : T {

    val head = this.repository.resolve(Constants.HEAD)
    RevWalk(this.repository).use { walk ->
        val headCommit: RevCommit = walk.parseCommit(head)

        val map: Sequence<RevCommitSemVer> = tags.asSequence().map { (ref, semVer) ->
            val objectId = this.peeledObjectId(ref)
            val taggedCommit: RevCommit = walk.parseCommit(objectId)
            RevCommitSemVer(taggedCommit, semVer)
        }

        return filter(walk, headCommit, map)
    }
}

fun Git.aheadOfTag(tags: List<RefSemVer>, ignoreTagsOnHead: Boolean = false) : SemVerAhead {
    val semVerAhead = processTags(tags) { walk, headCommit, sequence ->
        sequence.filter { (taggedCommit) ->
            when {
                ignoreTagsOnHead -> headCommit != taggedCommit
                else -> walk.isMergedInto(taggedCommit, headCommit)
            }
        }.map { (taggedCommit, semVer: SemVer) ->
            println("Check if " + headCommit.name + " is merged into " + taggedCommit.name)
            SemVerAhead(
                    semVer,
                    walk.countCommits(headCommit, taggedCommit),
                    headCommit == taggedCommit)
        }.plus(SemVerAhead(SemVer.Empty, 0, false))
                .first()
    }

    return semVerAhead
}


data class RefSemVer(val ref: Ref, val semVer: SemVer)

data class RevCommitSemVer(val commit: RevCommit, val semVer: SemVer)

data class SemVerAhead(val semVer: SemVer, val ahead: Int, val commitIsMatchingTag: Boolean)
