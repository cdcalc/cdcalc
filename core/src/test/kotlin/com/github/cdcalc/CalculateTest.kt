package com.github.cdcalc

import com.github.cdcalc.configuration.BuildEnvironment
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

    @Test fun `Should output TeamCity build number`(){
        outputBuildNumber(BuildEnvironment.TeamCity, GitFacts(branch = "master", semVer = SemVer(1,2,3)), {
            assertEquals("##teamcity[buildNumber '1.2.3']", it)
        })
    }

    @Test fun `Should output plain number for standalone`(){
        outputBuildNumber(BuildEnvironment.StandAlone, GitFacts(branch = "master", semVer = SemVer(1,2,3)), {
            assertEquals("1.2.3", it)
        })
    }
}