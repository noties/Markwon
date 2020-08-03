package io.noties.markwon.app.utils

import android.view.View
import android.view.inputmethod.InputMethodManager

object KeyboardUtils {

    fun show(view: View) {
        view.context.getSystemService(InputMethodManager::class.java)
                ?.showSoftInput(view, 0)
    }

    fun hide(view: View) {
        view.context.getSystemService(InputMethodManager::class.java)
                ?.hideSoftInputFromWindow(view.windowToken, 0)
    }
}