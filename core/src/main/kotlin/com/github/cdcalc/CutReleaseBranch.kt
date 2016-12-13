package com.github.cdcalc

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.CredentialsProvider

class CutReleaseBranch(val git: Git) {
    fun cutReleaseBranch(requiredBranch: String = "develop") {
        if (requiredBranch != git.repository.branch) {
            throw InvalidBranchException(requiredBranch, git.repository.branch)
        }

        val latestTag = git.tagList().call()
                .map { Tag(it.name) }
                .plus(Tag.Empty)
                .filter { Tag.Empty != it }
                .sortedDescending()
                .first()

        val bumpedVersion = latestTag.bumpMinor()
        val releaseBranch = "release/${bumpedVersion.major}.${bumpedVersion.minor}.${bumpedVersion.patch}"
        val releaseTag = "v${bumpedVersion.major}.${bumpedVersion.minor}.${bumpedVersion.patch}-rc.0"
        git.branchCreate().setName(releaseBranch).call()
        git.tag()
                .setMessage("Release candidate $releaseTag")
                .setName(releaseTag)
                .call()

        // TODO: putting this under test is almost impossible, we'll need to pass
        // a list of credential providers
        // git.push().setCredentialsProvider(CredentialsProvider.getDefault()).call()
    }
}


class InvalidBranchException(requiredBranch: String, currentBranch: String) :
        Throwable("$requiredBranch is required but current branch is $currentBranch")