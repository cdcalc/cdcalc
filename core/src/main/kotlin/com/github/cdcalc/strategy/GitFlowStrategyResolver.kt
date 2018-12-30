package com.github.cdcalc.strategy

import com.github.cdcalc.configuration.EnvironmentConfiguration
import com.github.cdcalc.data.SemVer

fun isSemVerTag(): (EnvironmentConfiguration) -> Boolean {
    return {
        !SemVer.parse(it.tag).isEmpty()
    }
}

fun matchBranch(name: String): (EnvironmentConfiguration) -> Boolean {
    return { config -> config.branch == name }
}

fun all(): (EnvironmentConfiguration) -> Boolean {
    return { true }
}

fun matchesBranch(searchString: String): (EnvironmentConfiguration) -> Boolean {
    return { config ->
        searchString
                .toRegex()
                .containsMatchIn(config.branch)
    }
}

fun findBranchStrategy(config: EnvironmentConfiguration): (org.eclipse.jgit.api.Git, EnvironmentConfiguration) -> SemVer {
    val strategies = listOf(
            Pair(com.github.cdcalc.strategy.isSemVerTag(), directTag()),
            Pair(com.github.cdcalc.strategy.matchBranch("master"), versionForMasterBranch()),
            Pair(com.github.cdcalc.strategy.matchesBranch("hotfix/.*"), versionForHotfixBranch()),
            Pair(com.github.cdcalc.strategy.matchesBranch("release/.*"), versionForReleaseBranch()),
            Pair(com.github.cdcalc.strategy.matchesBranch("develop"), versionForDevelopBranch()),
            Pair(com.github.cdcalc.strategy.all(), versionForAnyBranch())
    )

    val strategy = strategies.first {
        it -> it.first(config)
    }

    return strategy.second
}

