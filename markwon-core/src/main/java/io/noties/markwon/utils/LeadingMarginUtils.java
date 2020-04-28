package io.noties.markwon.utils;

import android.text.Spanned;

public abstract class LeadingMarginUtils {

    public static boolean selfStart(int start, CharSequence text, Object span) {
        return text instanceof Spanned && ((Spanned) text).getSpanStart(span) == start;
    }

    public static boolean selfEnd(int end, CharSequence text, Object span) {
        return text instanceof Spanned && ((Spanned) text).getSpanEnd(span) == end;
    }

    private LeadingMarginUtils() {
    }
}
