package com.github.cdcalc.strategy

import com.github.cdcalc.CalculateConfiguration
import com.github.cdcalc.data.SemVer
import org.eclipse.jgit.api.Git

fun match(name: String): (String) -> Boolean {
    return { branch -> branch == name }
}

fun matches(searchString: String): (String) -> Boolean {
    return { branch ->
        searchString
                .toRegex()
                .containsMatchIn(branch)
    }
}

/**
 * NOTE: In the long run we'll need to make this configurable
 */
fun findBranchStrategy(branch: String): (org.eclipse.jgit.api.Git, com.github.cdcalc.CalculateConfiguration) -> SemVer {
    println("Trying to find matching branch for: " + branch)

    val matchers = mapOf(
            Pair(com.github.cdcalc.strategy.match("master"), com.github.cdcalc.strategy.taggedCommit()),
            Pair(com.github.cdcalc.strategy.matches("hotfix/.*"), trackingBranch("master")),
            Pair(com.github.cdcalc.strategy.matches("release/.*"), trackingBranch("develop")),
            Pair(com.github.cdcalc.strategy.matches("merge-requests/.*"), com.github.cdcalc.strategy.trackRCTag()),
            Pair(com.github.cdcalc.strategy.matches("develop"), com.github.cdcalc.strategy.trackRCTag())
    )

    val strategy = matchers.entries.singleOrNull() {
        it -> it.key(branch)
    } ?: throw com.github.cdcalc.strategy.StrategyNotFoundException("Couldn't find a matching strategy for $branch")

    return strategy.value
}

class StrategyNotFoundException(message: String) : Throwable(message) {

}
