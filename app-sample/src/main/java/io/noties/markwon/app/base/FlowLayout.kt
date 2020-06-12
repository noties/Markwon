package io.noties.markwon.app.base

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup

class FlowLayout(context: Context, attrs: AttributeSet) : ViewGroup(context, attrs) {
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
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
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