package com.github.cdcalc

import org.eclipse.jgit.api.Git

class CutReleaseBranch(val git: Git) {
    fun cutReleaseBranch(requiredBranch: String = "develop") {
        if (requiredBranch != git.repository.branch) {
            throw InvalidBranchException(requiredBranch, git.repository.branch)
        }

        val latestTag = git.tagList().call()
                .map { Tag(it.name) }
                .plus(Tag.Empty)
                .sortedDescending()
                .first()

        val bumpedVersion = latestTag.bump()
        val releaseBranch = "release/${bumpedVersion.major}.${bumpedVersion.minor}.${bumpedVersion.patch}"
        val releaseTag = "v${bumpedVersion.major}.${bumpedVersion.minor}.${bumpedVersion.patch}-rc.0"

        git.branchCreate().setName(releaseBranch).call()
        git.tag()
                .setMessage("Release candidate $releaseTag")
                .setName(releaseTag)
                .call()

        // NOTE: it's possible to push to git here but different credentials and since it's hard to test we'll
        // wait with the implementation below.
        // git.push().setCredentialsProvider(CredentialsProvider.getDefault()).call()
    }
}


class InvalidBranchException(requiredBranch: String, currentBranch: String) :
        Throwable("$requiredBranch is required but current branch is $currentBranch")