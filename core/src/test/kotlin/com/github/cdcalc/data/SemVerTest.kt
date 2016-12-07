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
}