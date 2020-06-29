package io.noties.markwon.app.sample.ui

import android.content.Context
import android.view.View
import android.widget.EditText
import io.noties.markwon.app.R

abstract class MarkwonEditTextSample: MarkwonSample() {

    protected lateinit var context: Context
    protected lateinit var editText: EditText

    override val layoutResId: Int
        get() = R.layout.activity_edit_text

    override fun onViewCreated(view: View) {
        context = view.context
        editText = view.findViewById(R.id.edit_text)
        render()
    }

    abstract fun render()
}