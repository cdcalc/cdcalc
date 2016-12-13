package com.github.cdcalc.git

import com.github.cdcalc.data.CommitTag
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.ObjectId

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
