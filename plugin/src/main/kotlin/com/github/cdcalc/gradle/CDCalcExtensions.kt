package com.github.cdcalc.gradle

import java.io.File

open class CDCalcExtensions {
    var repository: File = File(".git")
    var versionFile: File? = null
}
