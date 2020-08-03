package io.noties.markwon.app.utils

import android.text.TextUtils
import android.widget.TextView

fun TextView.textOrHide(text: CharSequence?) {
    this.text = text
    this.hidden = TextUtils.isEmpty(text)
}