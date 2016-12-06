package com.github.cdcalc.git

import com.github.cdcalc.strategy.CommitTag
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.revwalk.filter.RevFilter

fun <T> RevWalk.countFirstCommitUntil(start: RevCommit, matches: (RevCommit) -> T): Pair<T, Int>? {
    this.walkFirstParentCurrentBranch(start).forEachIndexed { i, revCommit ->
        val match = matches(revCommit)
        if (null != match) {
            return Pair(match, i)
        }
    }

    return null
}

fun RevWalk.highestMergedTag(head: RevCommit, trackingBranch: Sequence<CommitTag>): CommitTag {
    return trackingBranch.first { tag ->
        val taggedCommit = this.parseCommit(tag.objectId)
        this.isMergedInto(taggedCommit, head)
    }
}

fun RevWalk.mergeBase(head: RevCommit, trackingBranch: RevCommit): RevCommit {
    this.setRevFilter(org.eclipse.jgit.revwalk.filter.RevFilter.MERGE_BASE)
    this.markStart(head)
    this.markStart(trackingBranch)
    val mergeBase = this.next()

    return mergeBase
}

fun RevWalk.walkFirstParentCurrentBranch(start: RevCommit): Sequence<RevCommit> {
    return generateSequence(start, { commit ->
        if (commit.parents.isEmpty()) {
            null
        } else {
            this.parseCommit(commit.parents.first())
        }
    })
}

fun RevWalk.countCommits(headCommit: RevCommit, masterCommit: RevCommit): Int {
    this.walkFirstParentCurrentBranch(headCommit).forEachIndexed { i, commit ->
        if (this.isMergedInto(commit, masterCommit)) {
            return i
        }
    }

    return -1
}