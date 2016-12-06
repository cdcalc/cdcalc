package com.github.cdcalc.data

import com.github.cdcalc.Tag

fun SemVer.tag(): Tag {
    return Tag(major, minor, patch)
}

data class SemVer(
        val major: Int = 0,
        val minor: Int = 0,
        val patch: Int = 0,
        val identifiers: List<String> = emptyList()) {

    override fun toString(): String {
        val tag = "$major.$minor.$patch"
        val identity = identifiers.joinToString(separator = ".", prefix = "-")
        return "$tag$identity"
    }
}
