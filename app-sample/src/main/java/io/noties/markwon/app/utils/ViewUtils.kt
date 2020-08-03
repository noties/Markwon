package io.noties.markwon.app.utils

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.ViewTreeObserver

var View.hidden: Boolean
    get() = visibility == GONE
    set(value) {
        visibility = if (value) GONE else VISIBLE
    }

fun View.onPreDraw(action: () -> Unit) {
    viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
        override fun onPreDraw(): Boolean {
            val vto = viewTreeObserver
            if (vto.isAlive) {
                vto.removeOnPreDrawListener(this)
            }
            action()
            // do not block drawing
            return true
        }
    })
}

var View.active: Boolean
    get() = isActivated
    set(newValue) {
        isActivated = newValue

        (this as? ViewGroup)?.children?.forEach { it.active = newValue }
    }