package io.noties.markwon.app.sample.ui

import android.content.Context
import android.view.View
import android.widget.ScrollView
import android.widget.TextView

import io.noties.markwon.app.R

abstract class MarkwonTextViewSample : MarkwonSample() {

    protected lateinit var context: Context
    protected lateinit var scrollView: ScrollView
    protected lateinit var textView: TextView

    override val layoutResId: Int = R.layout.sample_text_view

    override fun onViewCreated(view: View) {
        context = view.context
        scrollView = view.findViewById(R.id.scroll_view)
        textView = view.findViewById(R.id.text_view)
        render()
    }

    abstract fun render()
}