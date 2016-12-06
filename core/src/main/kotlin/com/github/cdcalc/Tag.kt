package com.github.cdcalc

data class Tag(val major: Int, val minor: Int, val patch: Int) : Comparable<Tag> {
    companion object {
        val Empty = Tag(0,0,0)
    }

    override fun compareTo(other: Tag): Int {
        if (this.major != other.major) {
            return this.major - other.major
        }

        if (this.minor != other.minor) {
            return this.minor - other.minor
        }

        return this.patch - other.patch
    }
}

fun Tag.raw() : String {
    return "${this.major}.${this.minor}.${this.patch}"
}

fun Tag(tagName: String): Tag {
    val toRegex = """v?(\d+)\.(\d+)\.(\d+)""".toRegex()
    val matchEntire = toRegex.find(tagName) ?: return Tag.Empty

    matchEntire.groups.let {
        return Tag(it[1]!!.value.toInt(), it[2]!!.value.toInt(), it[3]!!.value.toInt())
    }
}