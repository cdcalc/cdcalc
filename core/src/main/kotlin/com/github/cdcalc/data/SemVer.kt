package com.github.cdcalc.data

import com.github.cdcalc.Tag

data class SemVer(
        val major: Int = 0,
        val minor: Int = 0,
        val patch: Int = 0,
        val identifiers: List<String> = emptyList()) {

    fun identity(): String {
        if (identifiers.isEmpty()) {
            return ""
        } else {
            return identifiers.joinToString(separator = ".", prefix = "-")
        }
    }

    override fun toString(): String {
        val tag = "$major.$minor.$patch"
        val identity = identity()
        return "$tag$identity"
    }
}

fun SemVer.tag(): Tag {
    return Tag(major, minor, patch)
}