package io.noties.markdown.boundarytext

import android.content.Context
import android.graphics.Canvas
import android.text.Spanned
import android.util.AttributeSet
import android.widget.TextView

/**
 * A TextView that can draw rounded background to the portions of the text. See
 * [TextRoundedBgHelper] for more information.
 *
 * See [TextRoundedBgAttributeReader] for supported attributes.
 */
class RoundedBgTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.textViewStyle
) : TextView(context, attrs, defStyleAttr) {

    private lateinit var textRoundedBgHelper: TextRoundedBgHelper
    private lateinit var attributeReader : TextRoundedBgAttributeReader

    private lateinit var mAttrs : AttributeSet
    private var mTheme : String

    init {
        mTheme = "light"
        if (attrs != null) {
            mAttrs = attrs
            attributeReader = TextRoundedBgAttributeReader(context, mAttrs, mTheme)
            textRoundedBgHelper = TextRoundedBgHelper(
                    horizontalPadding = attributeReader.horizontalPadding,
                    verticalPadding = attributeReader.verticalPadding,
                    drawable = attributeReader.drawable,
                    drawableLeft = attributeReader.drawableLeft,
                    drawableMid = attributeReader.drawableMid,
                    drawableRight = attributeReader.drawableRight
            )
        }

    }

    fun setThemeChange(theme : String) {
        mTheme = theme
        attributeReader = TextRoundedBgAttributeReader(context, mAttrs, mTheme)
        textRoundedBgHelper = TextRoundedBgHelper(
                horizontalPadding = attributeReader.horizontalPadding,
                verticalPadding = attributeReader.verticalPadding,
                drawable = attributeReader.drawable,
                drawableLeft = attributeReader.drawableLeft,
                drawableMid = attributeReader.drawableMid,
                drawableRight = attributeReader.drawableRight
        )
    }

    fun setTextColorWith (textColor : Int){
        this.setTextColor(textColor)
    }

    override fun onDraw(canvas: Canvas) {
        // need to draw bg first so that text can be on top during super.onDraw()
        if (text is Spanned && layout != null) {
            canvas.withTranslation(totalPaddingLeft.toFloat(), totalPaddingTop.toFloat()) {
                textRoundedBgHelper.draw(canvas, text as Spanned, layout)
            }
        }
        super.onDraw(canvas)
    }
}