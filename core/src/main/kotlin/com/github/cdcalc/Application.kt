package com.github.cdcalc

import org.eclipse.jgit.api.Git
import java.io.File

fun main(args: Array<String>) {
    val git = Git.open(File("./"))
    Calculate(git).gitFacts()
}