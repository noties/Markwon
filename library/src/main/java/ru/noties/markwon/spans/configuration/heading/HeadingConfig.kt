package ru.noties.markwon.spans.configuration.heading

import android.graphics.Typeface
import android.support.annotation.ColorInt
import android.support.annotation.Dimension

/**
 * Configuration for heading type (H1, H2, ..., H6)
 *
 * Can define different configurations for all six types of headings,
 * plus an extra configuration for the line breaks (only applies to H1 and H2)
 *
 * @property h1Config Config for H1 heading
 * @property h2Config Config for H2 heading
 * @property h3Config Config for H3 heading
 * @property h4Config Config for H4 heading
 * @property h5Config Config for H5 heading
 * @property h6Config Config for H6 heading
 * @property headingBreakConfig Config for line breaks (for H1 and H2)
 */
class HeadingConfig @JvmOverloads constructor(
        val h1Config: HeadingTypeConfig = HeadingTypeConfig(),
        val h2Config: HeadingTypeConfig = HeadingTypeConfig(),
        val h3Config: HeadingTypeConfig = HeadingTypeConfig(),
        val h4Config: HeadingTypeConfig = HeadingTypeConfig(),
        val h5Config: HeadingTypeConfig = HeadingTypeConfig(),
        val h6Config: HeadingTypeConfig = HeadingTypeConfig(),
        val headingBreakConfig: HeadingBreakConfig = HeadingBreakConfig()
) {
    fun setDensityFactor(factor: Float) {
        h1Config.densityFactor = factor
        h2Config.densityFactor = factor
        h3Config.densityFactor = factor
        h4Config.densityFactor = factor
        h5Config.densityFactor = factor
        h6Config.densityFactor = factor
        headingBreakConfig.densityFactor = factor
    }
}

/**
 * Configuration for given heading type (H1, H2, ..., H6)
 *
 * Can set text size, text color and font (typeface)
 *
 * @property textSize Text size for heading
 * @property textColor Text color for heading
 * @property typeface Text size for heading
 */
class HeadingTypeConfig @JvmOverloads constructor(
        //Standard sizes available at #SpannableTheme.java:HEADING_SIZES
        textSize: Float = -1F,

        @ColorInt val textColor: Int = -1,

        val typeface: Typeface? = null
) {
    internal var densityFactor: Float = -1F

    val textSize: Float = textSize
        get() = field * densityFactor
}

/**
 * Configuration for given heading type (H1, H2, ..., H6)
 *
 * Can set text size, text color and font (typeface)
 *
 * @property headingBreakStrokeWidth Stroke width for heading's line break
 * @property headingBreakColor Color for heading's line break
 */
class HeadingBreakConfig @JvmOverloads constructor(
        // by default paint.getStrokeWidth
        @Dimension headingBreakStrokeWidth: Int = -1,

        // by default, text color with `HEADING_DEF_BREAK_COLOR_ALPHA` applied alpha
        @ColorInt val headingBreakColor: Int = 0
) {
    internal var densityFactor: Float = -1F

    val headingBreakStrokeWidth: Float = headingBreakStrokeWidth.toFloat()
        get() = field * densityFactor
}