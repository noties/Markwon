package io.noties.markwon.app.samples.editor.shared

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import io.noties.markwon.app.R
import io.noties.markwon.app.sample.ui.MarkwonSample
import io.noties.markwon.core.spans.EmphasisSpan
import io.noties.markwon.core.spans.StrongEmphasisSpan
import java.util.ArrayList

abstract class MarkwonEditTextSample : MarkwonSample() {

    protected lateinit var context: Context
    protected lateinit var editText: EditText

    override val layoutResId: Int
        get() = R.layout.sample_edit_text

    override fun onViewCreated(view: View) {
        context = view.context
        editText = view.findViewById(R.id.edit_text)
        initBottomBar(view)
        render()
    }

    abstract fun render()

    private fun initBottomBar(view: View) {
        // all except block-quote wraps if have selection, or inserts at current cursor position
        val bold: Button = view.findViewById(R.id.bold)
        val italic: Button = view.findViewById(R.id.italic)
        val strike: Button = view.findViewById(R.id.strike)
        val quote: Button = view.findViewById(R.id.quote)
        val code: Button = view.findViewById(R.id.code)

        addSpan(bold, StrongEmphasisSpan())
        addSpan(italic, EmphasisSpan())
        addSpan(strike, StrikethroughSpan())

        bold.setOnClickListener(InsertOrWrapClickListener(editText, "**"))
        italic.setOnClickListener(InsertOrWrapClickListener(editText, "_"))
        strike.setOnClickListener(InsertOrWrapClickListener(editText, "~~"))
        code.setOnClickListener(InsertOrWrapClickListener(editText, "`"))
        quote.setOnClickListener {
            val start = editText.selectionStart
            val end = editText.selectionEnd
            if (start < 0) {
                return@setOnClickListener
            }
            if (start == end) {
                editText.text.insert(start, "> ")
            } else {
                // wrap the whole selected area in a quote
                val newLines: MutableList<Int> = ArrayList(3)
                newLines.add(start)
                val text = editText.text.subSequence(start, end).toString()
                var index = text.indexOf('\n')
                while (index != -1) {
                    newLines.add(start + index + 1)
                    index = text.indexOf('\n', index + 1)
                }
                for (i in newLines.indices.reversed()) {
                    editText.text.insert(newLines[i], "> ")
                }
            }
        }
    }

    private fun addSpan(textView: TextView, vararg spans: Any) {
        val builder = SpannableStringBuilder(textView.text)
        val end = builder.length
        for (span in spans) {
            builder.setSpan(span, 0, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        textView.text = builder
    }

    private class InsertOrWrapClickListener(
            private val editText: EditText,
            private val text: String
    ) : View.OnClickListener {
        override fun onClick(v: View) {
            val start = editText.selectionStart
            val end = editText.selectionEnd
            if (start < 0) {
                return
            }
            if (start == end) {
                // insert at current position
                editText.text.insert(start, text)
            } else {
                editText.text.insert(end, text)
                editText.text.insert(start, text)
            }
        }

    }
}