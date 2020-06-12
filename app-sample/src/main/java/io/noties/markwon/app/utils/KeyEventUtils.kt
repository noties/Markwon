package io.noties.markwon.app.utils

import android.view.KeyEvent

object KeyEventUtils {
    fun isActionUp(event: KeyEvent?): Boolean {
        return event == null || KeyEvent.ACTION_UP == event.action
    }
}