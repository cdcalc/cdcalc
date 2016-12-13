package com.github.cdcalc.data

import org.eclipse.jgit.lib.ObjectId

data class CommitTag (
        val objectId: ObjectId,
        val tagName: String
)
