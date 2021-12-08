package io.noties.markdown.boundarytext

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet

class TextRoundedBgAttributeReader(context: Context, attrs: AttributeSet?, theme : String) {

    val horizontalPadding: Int
    val verticalPadding: Int
    val drawable: Drawable
    val drawableLeft: Drawable
    val drawableMid: Drawable
    val drawableRight: Drawable

    init {
        var typedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.TextRoundedBgHelper,
            0,
            R.style.RoundedBgTextView
        )
        if(theme.equals("dark")){
            typedArray = context.obtainStyledAttributes(
                    attrs,
                    R.styleable.TextRoundedBgHelper,
                    0,
                    R.style.RoundedBgTextDarkView
            )
        }
        horizontalPadding = typedArray.getDimensionPixelSize(
            R.styleable.TextRoundedBgHelper_roundedTextHorizontalPadding,
            0
        )
        verticalPadding = typedArray.getDimensionPixelSize(
            R.styleable.TextRoundedBgHelper_roundedTextVerticalPadding,
            0
        )
        drawable = typedArray.getDrawable(
                R.styleable.TextRoundedBgHelper_roundedTextDrawable
        )!!
        drawableLeft = typedArray.getDrawable(
                R.styleable.TextRoundedBgHelper_roundedTextDrawableLeft
        )!!
        drawableMid = typedArray.getDrawable(
                R.styleable.TextRoundedBgHelper_roundedTextDrawableMid
        )!!
        drawableRight = typedArray.getDrawable(
                R.styleable.TextRoundedBgHelper_roundedTextDrawableRight
        )!!
        typedArray.recycle()
    }
}
