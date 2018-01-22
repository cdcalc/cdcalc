package com.github.cdcalc.cli

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import com.github.cdcalc.Calculate
import org.eclipse.jgit.api.Git
import java.io.File


fun main(args: Array<String>) {
    val root = org.slf4j.LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger
    root.level = Level.WARN

    val git = Git.open(File("./"))
    Calculate(git).gitFacts(::println)
}