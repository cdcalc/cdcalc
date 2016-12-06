package com.github.cdcalc

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.MergeCommand

fun Git.merge(branch: String) {
    this.merge()
            .setFastForward(MergeCommand.FastForwardMode.NO_FF)
            .setCommit(true)
            .include(this.repository.findRef(branch))
            .call()
}

