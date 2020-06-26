package io.noties.markwon.app.widget

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import io.noties.markwon.app.R
import kotlin.math.max

class FlowLayout(context: Context, attrs: AttributeSet) : ViewGroup(context, attrs) {

    private val spacingVertical: Int
    private val spacingHorizontal: Int

    init {
        val array = context.obtainStyledAttributes(attrs, R.styleable.FlowLayout)
        try {
            val spacing = array.getDimensionPixelSize(R.styleable.FlowLayout_fl_spacing, 0)
            spacingVertical = array.getDimensionPixelSize(R.styleable.FlowLayout_fl_spacingVertical, spacing)
            spacingHorizontal = array.getDimensionPixelSize(R.styleable.FlowLayout_fl_spacingHorizontal, spacing)
        } finally {
            array.recycle()
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val params = child.layoutParams as LayoutParams
            val left = paddingLeft + params.x
            val top = paddingTop + params.y
            child.layout(
                    left,
                    top,
                    left + child.measuredWidth,
                    top + child.measuredHeight
            )
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)

        // we must have width (match_parent or exact dimension)
        if (width <= 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            return
        }

        val availableWidth = width - paddingLeft - paddingRight

        // child must not exceed our width
        val childWidthSpec = MeasureSpec.makeMeasureSpec(availableWidth, MeasureSpec.AT_MOST)

        // we also could enforce flexible height here (instead of exact one)
        val childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)

        var x = 0
        var y = 0

        var lineHeight = 0

        for (i in 0 until childCount) {
            val child = getChildAt(i)

            // measure
            child.measure(childWidthSpec, childHeightSpec)

            val params = child.layoutParams as LayoutParams
            val measuredWidth = child.measuredWidth

            if (measuredWidth > (availableWidth - x)) {
                // new line
                // make next child start at child measure width (starting at x = 0)
                params.x = 0
                params.y = y + lineHeight + spacingVertical

                x = measuredWidth + spacingHorizontal
                // move vertically by max value of child height on this line
                y += lineHeight + spacingVertical

                lineHeight = child.measuredHeight

            } else {
                // we fit this line
                params.x = x
                params.y = y

                x += measuredWidth + spacingHorizontal
                lineHeight = max(lineHeight, child.measuredHeight)
            }
        }

        val height = y + lineHeight + paddingTop + paddingBottom

        setMeasuredDimension(width, height)
    }

    override fun generateDefaultLayoutParams(): ViewGroup.LayoutParams {
        return LayoutParams()
    }

    override fun checkLayoutParams(p: ViewGroup.LayoutParams?): Boolean {
        return p is LayoutParams
    }

    override fun generateLayoutParams(attrs: AttributeSet): ViewGroup.LayoutParams {
        return LayoutParams(context, attrs)
    }

    override fun generateLayoutParams(p: ViewGroup.LayoutParams): ViewGroup.LayoutParams {
        return LayoutParams(p)
    }

    class LayoutParams : ViewGroup.LayoutParams {
        constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
        constructor(width: Int, height: Int) : super(width, height)
        constructor(params: ViewGroup.LayoutParams) : super(params)
        constructor() : this(WRAP_CONTENT, WRAP_CONTENT)

        var x: Int = 0
        var y: Int = 0
    }
}