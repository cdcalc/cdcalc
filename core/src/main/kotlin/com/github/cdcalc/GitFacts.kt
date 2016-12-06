package com.github.cdcalc

import com.github.cdcalc.data.SemVer
import java.util.*

data class GitFacts(val branch: String, val tag: Tag, val ahead: Int, val semVer: SemVer, val metaData: Map<String, String>) {
}
