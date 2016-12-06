package com.github.cdcalc

import com.github.cdcalc.data.SemVer
import com.github.cdcalc.data.tag
import com.github.cdcalc.strategy.findBranchStrategy
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Constants
import java.io.File

class Calculate(val git: Git) {

    constructor(directory: File) : this(Git.init().setGitDir(directory).call()) {
    }

    fun gitFacts(): GitFacts {
        val branch = git.repository.branch

        val semVer: SemVer = (com.github.cdcalc.strategy.findBranchStrategy(branch))(git, CalculateConfiguration(branch))

        val resolve = git.repository.resolve(Constants.HEAD)
        println(resolve.name)

        val mapOf = mapOf(Pair("sha", resolve.name))
        return GitFacts(branch, semVer.tag(), 0, semVer, mapOf)
    }
}


