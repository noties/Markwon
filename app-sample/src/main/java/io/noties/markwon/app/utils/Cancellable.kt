package io.noties.markwon.app.utils

interface Cancellable {
    val isCancelled: Boolean

    fun cancel()
}