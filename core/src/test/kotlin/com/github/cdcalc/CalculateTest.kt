package com.github.cdcalc

import com.github.cdcalc.configuration.BuildEnvironment
import com.github.cdcalc.data.SemVer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.support.io.TempDirectory
import org.junit.jupiter.api.support.io.TempDirectory.TempDir
import java.nio.file.Path

@ExtendWith(TempDirectory::class)
class CalculateTest {

    @Test fun `Should write version to file if specified`(@TempDir tempDir: Path) {
        val versionFile = tempDir.resolve(".version").toFile()
        writeVersionToFile(CalculateSettings(versionFile = versionFile), GitFacts("master", SemVer(1,2,3)))
        assertEquals("1.2.3", versionFile.readText())
    }

    @Test fun `Should output TeamCity build number`(){
        outputBuildNumber(BuildEnvironment.TeamCity, GitFacts(branch = "master", semVer = SemVer(1,2,3))) {
            assertEquals("##teamcity[buildNumber '1.2.3']", it)
        }
    }

    @Test fun `Should output plain number for standalone`(){
        outputBuildNumber(BuildEnvironment.StandAlone, GitFacts(branch = "master", semVer = SemVer(1,2,3))) {
            assertEquals("1.2.3", it)
        }
    }
}