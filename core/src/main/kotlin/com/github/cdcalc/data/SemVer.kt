package com.github.cdcalc.data

data class SemVer(
        val major: Int = 0,
        val minor: Int = 0,
        val patch: Int = 0,
        val identifiers: List<String> = emptyList()) : Comparable<SemVer> {

    companion object {
        val Empty = SemVer()
        
        fun parse(name: String): SemVer {
            val toRegex = """v?(\d+)\.(\d+)\.(\d+)""".toRegex()
            val matchEntire = toRegex.find(name) ?: return Empty

            val splitTagAndIdentifiers = name.split("-")
            val identifiers = when {
                splitTagAndIdentifiers.size == 2 -> splitTagAndIdentifiers[1].split(".")
                else -> emptyList()
            }

            matchEntire.groups.let {
                return SemVer(it[1]!!.value.toInt(), it[2]!!.value.toInt(), it[3]!!.value.toInt(), identifiers)
            }
        }
    }

    fun identity(): String {
        if (identifiers.isEmpty()) {
            return ""
        } else {
            return identifiers.joinToString(separator = ".", prefix = "-")
        }
    }

    fun isStable(): Boolean {
        return identifiers.isEmpty()
    }

    fun bumpMinor(): SemVer {
        return this.copy(minor = minor +1, patch = 0, identifiers = kotlin.collections.emptyList())
    }

    fun bumpPatch(): SemVer {
        return this.copy(patch = patch + 1, identifiers = kotlin.collections.emptyList())
    }

    fun bump(): SemVer {
        if (major == 0 && minor == 0) {
            return bumpPatch()
        }

        return bumpMinor()
    }

    override fun compareTo(other: SemVer): Int {
        val comparators = sequence {
            yield(major.compareTo(other.major))
            yield(minor.compareTo(other.minor))
            yield(patch.compareTo(other.patch))
            yield(isStable().compareTo(other.isStable()))
            yield(compareIdentifiers(other))
            yield(identifiers.size.compareTo(other.identifiers.size))
        }

        return comparators.firstOrNull { it != 0 } ?: 0
    }

    private fun compareIdentifiers(other: SemVer): Int {
        val minIdentifierLength = Math.min(identifiers.size, other.identifiers.size)

        val identifyComparison: Int = (0..minIdentifierLength - 1)
                .asSequence()
                .map {
                    val thisId = identifiers[it].toIntOrNull()
                    val otherId = other.identifiers[it].toIntOrNull()

                    when {

                        thisId != null && otherId == null -> -1
                        thisId == null && otherId != null -> 1
                        thisId != null && otherId != null -> thisId.compareTo(otherId)
                        else -> identifiers[it].compareTo(other.identifiers[it])
                    }
                }
                .filter { it != 0 }
                .firstOrNull() ?: 0
        return identifyComparison
    }

    override fun toString(): String {
        val tag = "$major.$minor.$patch"
        val identity = identity()
        return "$tag$identity"
    }
}