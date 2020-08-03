package io.noties.markwon.app.widget

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.widget.EditText
import io.noties.markwon.app.utils.KeyEventUtils

class TextField(context: Context, attrs: AttributeSet?) : EditText(context, attrs) {
    var onBackPressedListener: (() -> Unit)? = null

    override fun onDetachedFromWindow() {
        onBackPressedListener = null
        super.onDetachedFromWindow()
    }

    override fun onKeyPreIme(keyCode: Int, event: KeyEvent?): Boolean {
        if (isAttachedToWindow) {
            onBackPressedListener?.also { listener ->
                if (hasFocus()
                        && KeyEvent.KEYCODE_BACK == keyCode
                        && KeyEventUtils.isActionUp(event)) {
                    listener()
                }
            }
        }
        return super.onKeyPreIme(keyCode, event)
    }
}