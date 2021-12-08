package io.noties.markdown.boundarytext
import android.graphics.Canvas

/**
 * <pre>
 *     author : NeXT
 *     time   : 2018/12/11
 *     desc   :
 *     copy from: androidx.core.graphics.Canvas.kt
 * </pre>
 */

inline fun Canvas.withTranslation(
    x: Float = 0.0f,
    y: Float = 0.0f,
    block: Canvas.() -> Unit
) {
    val checkpoint = save()
    translate(x, y)
    try {
        block()
    } finally {
        restoreToCount(checkpoint)
    }
}