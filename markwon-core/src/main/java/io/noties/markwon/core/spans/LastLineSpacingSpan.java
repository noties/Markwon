package io.noties.markwon.core.spans;

import android.graphics.Paint;
import android.text.Spanned;
import android.text.style.LineHeightSpan;

import androidx.annotation.NonNull;
import androidx.annotation.Px;

/**
 * @since 4.0.0
 */
public class LastLineSpacingSpan implements LineHeightSpan {

    @NonNull
    public static LastLineSpacingSpan create(@Px int spacing) {
        return new LastLineSpacingSpan(spacing);
    }

    private final int spacing;

    public LastLineSpacingSpan(@Px int spacing) {
        this.spacing = spacing;
    }

    @Override
    public void chooseHeight(CharSequence text, int start, int end, int spanstartv, int v, Paint.FontMetricsInt fm) {
        if (selfEnd(end, text, this)) {
            // let's just add what we want
            fm.descent += spacing;
            fm.bottom += spacing;
        }
    }

    private static boolean selfEnd(int end, CharSequence text, Object span) {
        // this is some kind of interesting magic here... only the last
        // span will receive correct _end_ argument, but previous spans
        // receive it tilted by one (1). Most likely it's just a new-line character... and
        // if needed we could check for that
        final int spanEnd = ((Spanned) text).getSpanEnd(span);
        return spanEnd == end || spanEnd == end - 1;
    }
}
