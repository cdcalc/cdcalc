package com.github.cdcalc

import org.junit.Test
import kotlin.test.assertEquals

class TagTest {
    @Test fun shouldBumpPatchForNotReleasedPackaged() {
        assertEquals(Tag(0, 0, 1), Tag.Empty.bump())
    }

    @Test fun shouldBumpMinorForReleasePackages() {
        assertEquals(Tag(3, 15, 0), Tag(3, 14, 1).bump())
    }
}