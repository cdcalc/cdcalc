package com.github.cdcalc.strategy

import com.github.cdcalc.CalculateConfiguration
import com.github.cdcalc.Tag
import com.github.cdcalc.data.SemVer
import com.github.cdcalc.extensions.use
import com.github.cdcalc.git.countCommits
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.revwalk.RevWalk


fun trackingBranch(trackingBranch: String): (Git, CalculateConfiguration) -> SemVer {
    return { git, branch ->
        val head = git.repository.resolve(Constants.HEAD)
        val master = git.repository.resolve(trackingBranch)

        val mergeBase = RevWalk(git.repository).use { walk ->
            val headCommit = walk.parseCommit(head)
            val masterCommit = walk.parseCommit(master)

            walk.countCommits(headCommit, masterCommit)
        }

        val tag = Tag(branch.branch)

        println(tag)
        SemVer(tag.major, tag.minor, tag.patch, listOf("rc", mergeBase.toString()))
    }
}
