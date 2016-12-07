package com.github.cdcalc.extensions

// TODO: this will not be needed when kotlin hits 1.1
// https://youtrack.jetbrains.com/issue/KT-5899
@Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
inline fun <T : AutoCloseable, R> T.use(block: (T) -> R): R {
    var currentThrowable: java.lang.Throwable? = null
    try {
        return block(this)
    } catch (throwable: Throwable) {
        currentThrowable = throwable as java.lang.Throwable
        throw throwable
    } finally {
        if (currentThrowable != null) {
            try {
                this.close()
            } catch (throwable: Throwable) {
                currentThrowable.addSuppressed(throwable)
            }
        } else {
            this.close()
        }
    }
}