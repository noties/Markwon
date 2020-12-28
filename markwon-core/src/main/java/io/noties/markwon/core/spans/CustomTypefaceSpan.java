package io.noties.markwon.core.spans;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

import androidx.annotation.NonNull;

/**
 * A span implementation that allow applying custom Typeface. Although it is
 * not used directly by the library, it\'s helpful for customizations.
 * <p>
 * Please note that this implementation does not validate current paint state
 * and won\'t be updating/modifying supplied Typeface unless {@code mergeStyles} is specified
 *
 * @since 3.0.0
 */
public class CustomTypefaceSpan extends MetricAffectingSpan {

    @NonNull
    public static CustomTypefaceSpan create(@NonNull Typeface typeface) {
        return create(typeface, false);
    }

    /**
     * <strong>NB!</strong> in order to <em>merge</em> typeface styles, supplied typeface must be
     * able to be created via {@code Typeface.create(Typeface, int)} method. This would mean that bundled fonts
     * inside {@code assets} folder would be able to display styles properly.
     *
     * @param mergeStyles control if typeface styles must be merged, for example, if
     *                    this span (bold) is contained by other span (italic),
     *                    {@code mergeStyles=true} would result in bold-italic
     * @since 4.6.1
     */
    @NonNull
    public static CustomTypefaceSpan create(@NonNull Typeface typeface, boolean mergeStyles) {
        return new CustomTypefaceSpan(typeface, mergeStyles);
    }

    private final Typeface typeface;

    private final boolean mergeStyles;

    /**
     * @deprecated 4.6.1 use {{@link #create(Typeface)}}
     * or {@link #create(Typeface, boolean)} factory method
     */
    @Deprecated
    public CustomTypefaceSpan(@NonNull Typeface typeface) {
        this(typeface, false);
    }

    // @since 4.6.1
    CustomTypefaceSpan(@NonNull Typeface typeface, boolean mergeStyles) {
        this.typeface = typeface;
        this.mergeStyles = mergeStyles;
    }


    @Override
    public void updateMeasureState(@NonNull TextPaint paint) {
        updatePaint(paint);
    }

    @Override
    public void updateDrawState(@NonNull TextPaint paint) {
        updatePaint(paint);
    }

    private void updatePaint(@NonNull TextPaint paint) {
        final Typeface oldTypeface = paint.getTypeface();
        if (!mergeStyles ||
                oldTypeface == null ||
                oldTypeface.getStyle() == Typeface.NORMAL) {
            paint.setTypeface(typeface);
        } else {
            final int oldStyle = oldTypeface.getStyle();

            @SuppressLint("WrongConstant") final int want = oldStyle | typeface.getStyle();
            final Typeface styledTypeface = Typeface.create(typeface, want);

            paint.setTypeface(styledTypeface);
        }
    }
}
