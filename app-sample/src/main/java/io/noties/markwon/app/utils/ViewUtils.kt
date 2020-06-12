package io.noties.markwon.app.utils

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE

var View.hidden: Boolean
    get() = visibility == GONE
    set(value) {
        visibility = if (value) GONE else VISIBLE
    }