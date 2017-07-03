package com.github.cdcalc.data

import org.junit.Test
import kotlin.test.assertEquals

class SemVerTest {
    @Test
    fun should_not_print_identifier_if_non_is_present() {
        val semVer = SemVer(1,2,3)

        assertEquals("1.2.3", semVer.toString())
    }

    @Test
    fun should_handle_identifiers() {
        val semVer = SemVer(3,2,1, listOf("beta", "1337"))

        assertEquals("3.2.1-beta.1337", semVer.toString())
    }

    @Test
    fun compare_beta_and_beta_id() {
        val versions = listOf(
                SemVer(0, 0, 1),
                SemVer(0, 1, 0),
                SemVer(1, 0, 0),
                SemVer(1, 0, 1),
                SemVer(1, 1, 0),
                SemVer(1, 1, 1),
                SemVer(1, 2, 0),
                SemVer(1, 3, 0, listOf("alpha")),
                SemVer(1, 3, 0, listOf("beta")),
                SemVer(1, 3, 0, listOf("beta", "1"))
        )

        val sortedVersions = versions.reversed().sorted()

        assertEquals(versions, sortedVersions)
    }

    // http://semver.org/ rule #11
    @Test
    fun compare_semver_11() {
        val one = SemVer(1,0,0)
        val versions = listOf(
                one.copy(identifiers = listOf("alpha")),
                one.copy(identifiers = listOf("alpha", "1")),
                one.copy(identifiers = listOf("alpha", "beta")),
                one.copy(identifiers = listOf("beta")),
                one.copy(identifiers = listOf("beta", "2")),
                one.copy(identifiers = listOf("beta", "11")),
                one.copy(identifiers = listOf("rc", "1")),
                one.copy(identifiers = listOf("rc", "1", "1")),
                one
        )

        val sortedVersions = versions.reversed().sorted()

        assertEquals(versions, sortedVersions)
    }

    @Test
    fun parse_semver_from_tag() {
        val tags = listOf(
                Pair("v1.0.0", SemVer(1, 0, 0)),
                Pair("v3.2.1", SemVer(3, 2, 1)),
                Pair("v3.2.1-beta", SemVer(3, 2, 1, listOf("beta"))),
                Pair("v3.2.1-alpha.1", SemVer(3, 2, 1, listOf("alpha", "1"))),
                Pair("v3.2.1", SemVer(3, 2, 1))
        )

        val parsedVersions = tags.map { SemVer.parse(it.first) }.toList()
        val expectedVersions = tags.map { it.second }.toList()

        assertEquals(expectedVersions, parsedVersions)
    }

    @Test fun shouldBumpPatchForNotReleasedPackaged() {
        assertEquals(SemVer(0, 0, 1), SemVer.Empty.bump())
    }

    @Test fun shouldBumpMinorForReleasePackages() {
        assertEquals(SemVer(3, 15, 0), SemVer(3, 14, 1).bump())
    }
}