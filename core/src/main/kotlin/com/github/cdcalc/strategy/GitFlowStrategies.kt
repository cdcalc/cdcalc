package com.github.cdcalc.strategy

import com.github.cdcalc.configuration.EnvironmentConfiguration
import com.github.cdcalc.data.SemVer
import com.github.cdcalc.git.RefSemVer
import com.github.cdcalc.git.aheadOfTag
import com.github.cdcalc.git.processTags
import com.github.cdcalc.git.semVerTags
import org.eclipse.jgit.api.Git

fun versionForHotfixBranch(): (Git, EnvironmentConfiguration) -> SemVer {
    return { git, branch ->
        val semVer = SemVer.parse(branch.branch)

        val tags = git.semVerTags()
            .filterTagsDescending { versionFromTag ->
                versionFromTag == semVer.copy(patch = Math.max(0, semVer.patch - 1))
            }

        val (_, ahead) = git.aheadOfTag(tags)

        semVer.copy(identifiers = listOf("rc", ahead.toString()))
    }
}

fun versionForReleaseBranch(): (Git, EnvironmentConfiguration) -> SemVer {
    return { git, branch ->
        val semVer = SemVer.parse(branch.branch)

        val tags = git.semVerTags()
            .filterTagsDescending { versionFromTag ->
                versionFromTag == semVer.copy(identifiers = listOf("rc", "0"))
            }

        val (_, ahead) = git.aheadOfTag(tags)

        semVer.copy(identifiers = listOf("rc", ahead.toString()))
    }
}

fun versionForDevelopBranch(): (Git, EnvironmentConfiguration) -> SemVer {
    return { git, _ ->
        val tags = git.semVerTags()
            .filterTagsDescending { versionFromTag ->
                versionFromTag.identifiers == listOf("rc", "0")
            }

        val (semVer, ahead) = git.aheadOfTag(tags, true)

        if (semVer == SemVer.Empty || ahead == 0) {
            throw TrackingException("Couldn't find any reachable rc.0 tags before current commit")
        }

        semVer.bump().copy(identifiers = listOf("beta", (ahead - 1).toString()))
    }
}

class TrackingException(message: String) : Throwable(message)

fun versionForMasterBranch(): (Git, EnvironmentConfiguration) -> SemVer {
    return { git, _ ->
        val tags = git.semVerTags()
            .filterTagsDescending({ it.isStable() })

        git.processTags(tags) { _, headCommit, list ->
            list.firstOrNull { headCommit == it.commit }?.semVer ?: SemVer()
        }
    }
}

fun directTag(): (Git, EnvironmentConfiguration) -> SemVer {
    return { _, config ->
        SemVer.parse(config.tag)
    }
}

fun versionForAnyBranch(): (Git, EnvironmentConfiguration) -> SemVer {
    return { git, _ ->
        val tags = git.semVerTags()
            .filterTagsDescending({ versionFromTag ->
                versionFromTag.identifiers == listOf("rc", "0") || versionFromTag.isStable()
            })

        val processTags: SemVer = git.processTags(tags) { walk, headCommit, list ->
            val taggedCommit = list.firstOrNull {
                walk.isMergedInto(it.commit, headCommit) && headCommit != it.commit
            }?.semVer ?: SemVer()
            taggedCommit.bump().copy(identifiers = listOf("alpha", headCommit.abbreviate(7).name().toUpperCase()))
        }
        processTags
    }
}

private fun List<RefSemVer>.filterTagsDescending(tagFilter: (SemVer) -> Boolean) : List<RefSemVer> {
    return this.filter { tag -> tagFilter(tag.semVer) }
            .sortedByDescending { tag -> tag.semVer }
}