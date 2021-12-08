package io.noties.markdown.boundarytext
import android.os.Build
import android.text.Layout

// Extension functions for Layout object

/**
 * Android system default line spacing extra
 */
private const val DEFAULT_LINESPACING_EXTRA = 0f

/**
 * Android system default line spacing multiplier
 */
private const val DEFAULT_LINESPACING_MULTIPLIER = 1f

/**
 * Get the line bottom discarding the line spacing added.
 */
fun Layout.getLineBottomWithoutSpacing(line: Int): Int {
    val lineBottom = getLineBottom(line)
    val lastLineSpacingNotAdded = Build.VERSION.SDK_INT >= 19
    val isLastLine = line == lineCount - 1

    val lineBottomWithoutSpacing: Int
    val lineSpacingExtra = spacingAdd
    val lineSpacingMultiplier = spacingMultiplier
    val hasLineSpacing = lineSpacingExtra != DEFAULT_LINESPACING_EXTRA
        || lineSpacingMultiplier != DEFAULT_LINESPACING_MULTIPLIER

    if (!hasLineSpacing || isLastLine && lastLineSpacingNotAdded) {
        lineBottomWithoutSpacing = lineBottom
    } else {
        val extra: Float
        if (lineSpacingMultiplier.compareTo(DEFAULT_LINESPACING_MULTIPLIER) != 0) {
            val lineHeight = getLineHeight(line)
            extra = lineHeight - (lineHeight - lineSpacingExtra) / lineSpacingMultiplier
        } else {
            extra = lineSpacingExtra
        }

        lineBottomWithoutSpacing = (lineBottom - extra).toInt()
    }

    return lineBottomWithoutSpacing
}

/**
 * Get the line height of a line.
 */
fun Layout.getLineHeight(line: Int): Int {
    return getLineTop(line + 1) - getLineTop(line)
}

/**
 * Returns the top of the Layout after removing the extra padding applied by  the Layout.
 */
fun Layout.getLineTopWithoutPadding(line: Int): Int {
    var lineTop = getLineTop(line)
    if (line == 0) {
        lineTop -= topPadding
    }
    return lineTop
}

/**
 * Returns the bottom of the Layout after removing the extra padding applied by the Layout.
 */
fun Layout.getLineBottomWithoutPadding(line: Int): Int {
    var lineBottom = getLineBottomWithoutSpacing(line)
    if (line == lineCount - 1) {
        lineBottom -= bottomPadding
    }
    return lineBottom
}