package com.github.cdcalc

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.PersonIdent
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.*

fun Git.prettyLog() {
    val process = ProcessBuilder().directory(this.repository.directory).command("git", "lg").start()

    val stream = process.inputStream;
    val reader = InputStreamReader(stream);
    val bufferedReader = BufferedReader(reader);
    bufferedReader.lineSequence().forEach {
        println(it)
    }
    bufferedReader.close()
    reader.close()
    stream.close()
    process.waitFor()
}

fun Git.checkout(branch: String, create: Boolean = false): Git {
    this.checkout()
            .setCreateBranch(create)
            .setName(branch)
            .call()

    return this
}

fun Git.createTaggedCommit(tag: String): Git {
    this.createCommit()

    this.tag().let {
        it.name = tag
        it.message = "Released $tag"
        it.call()
    }

    this.commit()

    return this
}


fun Git.createCommit(message: String = "create file"): Git {
    val commit = this.commit()

    val id = UUID.randomUUID();
    val file = File(this.repository.workTree, id.toString())
    file.createNewFile()
    commit.author = PersonIdent("Örjan Sjöholm", "orjan.sjoholm@gmail.com")
    commit.setAll(true)
    commit.message = message + " > " + id.toString() + " @ " + this.repository.branch
    commit.call()

    return this
}

fun initGitFlow(): Git {
    val property = System.getProperty("java.io.tmpdir")

    val file = File(property, "test-repo")
    file.deleteRecursively()
    file.mkdirs()

    val git = Git.init().setDirectory(file).call()

    git.createCommit()
    git.tag().setName("v1.0.0").call()
    git.checkout("develop", true).createCommit()

    return git
}
