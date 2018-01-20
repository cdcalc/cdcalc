package com.github.cdcalc

import com.github.cdcalc.data.SemVer
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class CalculateTest {
    @Rule @JvmField val tempDir = TemporaryFolder()

    @Test fun `Should write version to file if specified`() {
        val versionFile = tempDir.newFile()
        writeVersionToFile(CalculateSettings(versionFile = versionFile), GitFacts("master", SemVer(1,2,3)))
        assertEquals("1.2.3", versionFile.readText())
    }
}