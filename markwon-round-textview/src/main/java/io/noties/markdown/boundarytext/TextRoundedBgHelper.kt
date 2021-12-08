package io.noties.markdown.boundarytext


import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Layout
import android.text.Spanned
import io.noties.markwon.core.spans.CodeSpan

class TextRoundedBgHelper(
    val horizontalPadding: Int,
    verticalPadding: Int,
    drawable: Drawable,
    drawableLeft: Drawable,
    drawableMid: Drawable,
    drawableRight: Drawable
) {

    private val singleLineRenderer: TextRoundedBgRenderer by lazy {
        SingleLineRenderer(
            horizontalPadding = horizontalPadding,
            verticalPadding = verticalPadding,
            drawable = drawable
        )
    }

    private val multiLineRenderer: TextRoundedBgRenderer by lazy {
        MultiLineRenderer(
            horizontalPadding = horizontalPadding,
            verticalPadding = verticalPadding,
            drawableLeft = drawableLeft,
            drawableMid = drawableMid,
            drawableRight = drawableRight
        )
    }

    /**
     * Call this function during onDraw of another widget such as TextView.
     *
     * @param canvas Canvas to draw onto
     * @param text
     * @param layout Layout that contains the text
     */
    fun draw(canvas: Canvas, text: Spanned, layout: Layout) {
        // ideally the calculations here should be cached since they are not cheap. However, proper
        // invalidation of the cache is required whenever anything related to text has changed.
//        val spans = text.getSpans(0, text.length, Annotation::class.java)
        val spans = text.getSpans(0, text.length, CodeSpan::class.java)
        spans.forEach { span ->
//            if (span.value.equals("rounded")) {
                val spanStart = text.getSpanStart(span)
                val spanEnd = text.getSpanEnd(span)
                val startLine = layout.getLineForOffset(spanStart)
                val endLine = layout.getLineForOffset(spanEnd)

                // start can be on the left or on the right depending on the language direction.
                val startOffset = (layout.getPrimaryHorizontal(spanStart)
                    + -1 * layout.getParagraphDirection(startLine) * horizontalPadding).toInt()
                // end can be on the left or on the right depending on the language direction.
                val endOffset = (layout.getPrimaryHorizontal(spanEnd)
                    + layout.getParagraphDirection(endLine) * horizontalPadding).toInt()

                val renderer = if (startLine == endLine) singleLineRenderer else multiLineRenderer
                renderer.draw(canvas, layout, startLine, endLine, startOffset, endOffset)
//            }
        }
    }
}