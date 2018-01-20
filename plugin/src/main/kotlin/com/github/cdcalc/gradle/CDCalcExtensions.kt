package com.github.cdcalc.gradle

open class CDCalcExtensions {
    var repository: String = ".git"

    @Deprecated("Will be removed in v0.1.0, since it's not needed any more")
    var trackOrigin: String = "origin/"

    @Deprecated("Will be removed in v0.1.0, use calculateVersion.version instead")
    var calculatedVersion: String = "0.0.1"
    @Deprecated("Will be removed in v0.1.0, use calculateVersion.branch instead")
    var branch: String = ""
}
