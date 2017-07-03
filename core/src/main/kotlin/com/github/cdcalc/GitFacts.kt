package com.github.cdcalc

import com.github.cdcalc.data.SemVer

data class GitFacts(val branch: String, val ahead: Int, val semVer: SemVer, val metaData: Map<String, String>)
