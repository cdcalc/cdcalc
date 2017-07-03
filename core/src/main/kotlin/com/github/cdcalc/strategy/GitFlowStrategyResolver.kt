package com.github.cdcalc.strategy

import com.github.cdcalc.data.SemVer

fun match(name: String): (String) -> Boolean {
    return { branch -> branch == name }
}

fun all(): (String) -> Boolean {
    return { true }
}

fun matches(searchString: String): (String) -> Boolean {
    return { branch ->
        searchString
                .toRegex()
                .containsMatchIn(branch)
    }
}

fun findBranchStrategy(branch: String): (org.eclipse.jgit.api.Git, com.github.cdcalc.CalculateConfiguration) -> SemVer {
    println("Trying to find matching branch for: " + branch)

    val strategies = listOf(
            Pair(com.github.cdcalc.strategy.match("master"), versionForMasterBranch()),
            Pair(com.github.cdcalc.strategy.matches("hotfix/.*"), versionForHotfixBranch()),
            Pair(com.github.cdcalc.strategy.matches("release/.*"), versionForReleaseBranch()),
            Pair(com.github.cdcalc.strategy.matches("develop"), versionForDevelopBranch()),
            Pair(com.github.cdcalc.strategy.all(), versionForAnyBranch())
    )

    val strategy = strategies.first {
        it -> it.first(branch)
    }

    return strategy.second
}
