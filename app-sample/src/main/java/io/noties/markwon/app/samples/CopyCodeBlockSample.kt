package io.noties.markwon.app.samples

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.Layout
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.text.style.LeadingMarginSpan
import android.view.View
import android.widget.TextView
import io.noties.debug.Debug
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonSpansFactory
import io.noties.markwon.app.R
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample
import io.noties.markwon.sample.annotations.MarkwonArtifact
import io.noties.markwon.sample.annotations.MarkwonSampleInfo
import io.noties.markwon.sample.annotations.Tag
import io.noties.markwon.utils.LeadingMarginUtils
import org.commonmark.node.FencedCodeBlock

@MarkwonSampleInfo(
        id = "20210315112847",
        title = "Copy code block",
        description = "Copy contents of fenced code blocks",
        artifacts = [MarkwonArtifact.CORE],
        tags = [Tag.rendering, Tag.block, Tag.spanFactory, Tag.span]
)
class CopyCodeBlockSample : MarkwonTextViewSample() {

    override fun render() {
        val md = """
            # Hello code blocks!
            ```java
            final int i = 0;
            final Type t = Type.init()
              .filter(i -> i.even)
              .first(null);
            ```
            bye bye!
        """.trimIndent()

        val markwon = Markwon.builder(context)
                .usePlugin(object : AbstractMarkwonPlugin() {
                    override fun configureSpansFactory(builder: MarkwonSpansFactory.Builder) {
                        builder.appendFactory(FencedCodeBlock::class.java) { _, _ ->
                            CopyContentsSpan()
                        }
                        builder.appendFactory(FencedCodeBlock::class.java) { _, _ ->
                            CopyIconSpan(context.getDrawable(R.drawable.ic_code_white_24dp)!!)
                        }
                    }
                })
                .build()

        markwon.setMarkdown(textView, md)
    }

    class CopyContentsSpan : ClickableSpan() {
        override fun onClick(widget: View) {
            val spanned = (widget as? TextView)?.text as? Spanned ?: return
            val start = spanned.getSpanStart(this)
            val end = spanned.getSpanEnd(this)
            // by default code blocks have new lines before and after content
            val contents = spanned.subSequence(start, end).toString().trim()
            // copy code here
            Debug.i(contents)
        }

        override fun updateDrawState(ds: TextPaint) {
            // do not apply link styling
        }
    }

    class CopyIconSpan(val icon: Drawable) : LeadingMarginSpan {

        init {
            if (icon.bounds.isEmpty) {
                icon.setBounds(0, 0, icon.intrinsicWidth, icon.intrinsicHeight)
            }
        }

        override fun getLeadingMargin(first: Boolean): Int = 0

        override fun drawLeadingMargin(
                c: Canvas,
                p: Paint,
                x: Int,
                dir: Int,
                top: Int,
                baseline: Int,
                bottom: Int,
                text: CharSequence,
                start: Int,
                end: Int,
                first: Boolean,
                layout: Layout
        ) {

            // called for each line of text, we are interested only in first one
            if (!LeadingMarginUtils.selfStart(start, text, this)) return

            val save = c.save()
            try {
                // horizontal position for icon
                val w = icon.bounds.width().toFloat()
                // minus quarter width as padding
                val left = layout.width - w - (w / 4F)
                c.translate(left, top.toFloat())
                icon.draw(c)
            } finally {
                c.restoreToCount(save)
            }
        }
    }
}