package io.noties.markwon.utils;

import android.os.Build;
import android.text.Layout;

import androidx.annotation.NonNull;

/**
 * @since 4.4.0
 */
public abstract class LayoutUtils {

    private static final float DEFAULT_EXTRA = 0F;
    private static final float DEFAULT_MULTIPLIER = 1F;

    public static int getLineBottomWithoutPaddingAndSpacing(
            @NonNull Layout layout,
            int line
    ) {

        final int bottom = layout.getLineBottom(line);
        final boolean lastLineSpacingNotAdded = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        final boolean isSpanLastLine = line == (layout.getLineCount() - 1);

        final int lineBottom;
        final float lineSpacingExtra = layout.getSpacingAdd();
        final float lineSpacingMultiplier = layout.getSpacingMultiplier();

        // simplified check
        final boolean hasLineSpacing = lineSpacingExtra != DEFAULT_EXTRA
                || lineSpacingMultiplier != DEFAULT_MULTIPLIER;

        if (!hasLineSpacing
                || (isSpanLastLine && lastLineSpacingNotAdded)) {
            lineBottom = bottom;
        } else {
            final float extra;
            if (Float.compare(DEFAULT_MULTIPLIER, lineSpacingMultiplier) != 0) {
                final int lineHeight = getLineHeight(layout, line);
                extra = lineHeight -
                        ((lineHeight - lineSpacingExtra) / lineSpacingMultiplier);
            } else {
                extra = lineSpacingExtra;
            }
            lineBottom = (int) (bottom - extra + .5F);
        }

        // check if it is the last line that span is occupying **and** that this line is the last
        //  one in TextView
        if (isSpanLastLine
                && (line == layout.getLineCount() - 1)) {
            return lineBottom - layout.getBottomPadding();
        }

        return lineBottom;
    }

    public static int getLineTopWithoutPadding(@NonNull Layout layout, int line) {
        final int top = layout.getLineTop(line);
        if (line == 0) {
            return top - layout.getTopPadding();
        }
        return top;
    }

    public static int getLineHeight(@NonNull Layout layout, int line) {
        return layout.getLineTop(line + 1) - layout.getLineTop(line);
    }

    private LayoutUtils() {
    }
}
