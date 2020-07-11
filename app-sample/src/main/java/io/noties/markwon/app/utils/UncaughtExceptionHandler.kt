package io.noties.markwon.app.utils

@Suppress("unused")
class UncaughtExceptionHandler(private val origin: Thread.UncaughtExceptionHandler?)
    : Thread.UncaughtExceptionHandler {

    override fun uncaughtException(t: Thread?, e: Throwable?) {
        
    }
}