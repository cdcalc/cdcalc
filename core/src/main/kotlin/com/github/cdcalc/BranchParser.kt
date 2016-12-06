package com.github.cdcalc


class BranchParser {
    fun findTagInBranchName(branchName: String): Tag {
        println("Matchin branch: " + branchName)
        val toRegex = """(release|hotfix)/(\d+)\.(\d+)\.(\d+)""".toRegex()
        val matchEntire = toRegex.matchEntire(branchName) ?: return Tag.Empty

        matchEntire.groups.let {
            return Tag(it[2]!!.value.toInt(),
                    it[3]!!.value.toInt(),
                    it[4]!!.value.toInt())
        }
    }
}