package com.github.cdcalc

import org.eclipse.jgit.api.Git

class CutReleaseBranch(val git: Git) {
    fun cutReleaseBranch(requiredBranch: String = "develop") {
        if (requiredBranch != git.repository.branch) {
            throw InvalidBranchException(requiredBranch, git.repository.branch)
        }
    }
}

class InvalidBranchException(val requiredBranch: String, val currentBranch: String) :
        Throwable("$requiredBranch is required but current branch is $currentBranch")