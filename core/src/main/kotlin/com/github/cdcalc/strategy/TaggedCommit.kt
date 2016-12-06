package com.github.cdcalc.strategy

import com.github.cdcalc.CalculateConfiguration
import com.github.cdcalc.Tag
import com.github.cdcalc.data.SemVer
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Constants

/**
 * Limitations
 * - Tag and commit must match
 * - If not tag exists, we're doomed
 */
fun taggedCommit(): (Git, CalculateConfiguration) -> SemVer {
    return { git, branch ->
        val head = git.repository.resolve(Constants.HEAD)

        val single = git.tagList().call().map {
            val peel = git.repository.peel(it)
            if (peel.peeledObjectId != null) {
                Pair(peel.peeledObjectId, it.name)
            } else {
                Pair(it.objectId, it.name)
            }
        }.single() { it.first.equals(head) }

        val tag = Tag(single.second)
        SemVer(tag.major, tag.minor, tag.patch)
    }
}