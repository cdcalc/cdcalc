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

fun Tag(tagName: String): Tag {
    val toRegex = """v?(\d+)\.(\d+)\.(\d+)""".toRegex()
    val matchEntire = toRegex.find(tagName) ?: return Tag.Empty

    matchEntire.groups.let {
        return Tag(it[1]!!.value.toInt(), it[2]!!.value.toInt(), it[3]!!.value.toInt())
    }
}

fun Tag.bump(): Tag {
    if (this.major == 0 && this.minor == 0) {
        return this.bumpPatch()
    }

    return this.bumpMinor()
}

fun Tag.bumpMinor(): Tag {
    return this.copy(minor = this.minor + 1, patch = 0)
}

fun Tag.bumpPatch(): Tag {
    return this.copy(patch = this.patch + 1)
}