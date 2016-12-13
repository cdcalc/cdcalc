package com.github.cdcalc.git

import com.github.cdcalc.data.CommitTag
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevWalk

fun RevWalk.highestMergedTag(head: RevCommit, trackingBranch: Sequence<CommitTag>): CommitTag {
    return trackingBranch.first { tag ->
        val taggedCommit = this.parseCommit(tag.objectId)
        this.isMergedInto(taggedCommit, head)
    }
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