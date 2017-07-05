package com.github.cdcalc.cli

import com.github.cdcalc.Calculate
import org.eclipse.jgit.api.Git
import java.io.File

fun main(args: Array<String>) {
    val git = Git.open(File("./"))
    Calculate(git).gitFacts()
}