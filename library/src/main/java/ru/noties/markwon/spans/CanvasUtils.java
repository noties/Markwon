package ru.noties.markwon.spans;

import android.graphics.Paint;
import android.support.annotation.NonNull;

abstract class CanvasUtils {

    static float textCenterY(int top, int bottom, @NonNull Paint paint) {
        // @since 1.1.1 it's `top +` and not `bottom -`
        return (int) (top + ((bottom - top) / 2) - ((paint.descent() + paint.ascent()) / 2.F + .5F));
    }

    private CanvasUtils() {
    }
}
