package com.github.cdcalc.git

import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevWalk

fun RevWalk.walkFirstParentCurrentBranch(start: RevCommit): Sequence<RevCommit> {
    return generateSequence(start, { commit ->
        if (commit.parents.isEmpty()) {
            null
        } else {
            this.parseCommit(commit.parents.first())
        }
    })
}

// TODO: change order and rename to reflect first parent
fun RevWalk.countCommits(headCommit: RevCommit, masterCommit: RevCommit): Int {
    this.walkFirstParentCurrentBranch(headCommit).forEachIndexed { i, commit ->
        if (this.isMergedInto(commit, masterCommit)) {
            return i
        }
    }

    return -1
}