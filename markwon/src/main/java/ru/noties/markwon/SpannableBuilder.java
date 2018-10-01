package ru.noties.markwon;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;

/**
 * This class is used to _revert_ order of applied spans. Original SpannableStringBuilder
 * is using an array to store all the information about spans. So, a span that is added first
 * will be drawn first, which leads to subtle bugs (spans receive wrong `x` values when
 * requested to draw itself)
 */
public class SpannableBuilder extends SpannableStringBuilder {

    public SpannableBuilder() {
        super();
    }

    public SpannableBuilder(CharSequence text, int start, int end) {
        super(text, start, end);
    }

    @Override
    public <T> T[] getSpans(int queryStart, int queryEnd, Class<T> kind) {
        T[] ret = super.getSpans(queryStart, queryEnd, kind);
        reverse(ret);
        return ret;
    }

    /**
     * Convenience for allowing {@link Nullable} and {@link NonNull} spans, as well as
     * {@link NonNull} array of spans. {@link Spanned#SPAN_EXCLUSIVE_EXCLUSIVE} will be applied as
     * flag.
     *
     * @param spans A span object, an array of span objects or null
     * @param start Start index (inclusive)
     * @param end   End index (exclusive)
     */
    public void setSpans(@Nullable Object spans, int start, int end) {
        if (spans != null) {
            // setting a span for an invalid position can lead to silent fail (no exception,
            // but execution is stopped)
            if (!isPositionValid(length(), start, end)) {
                return;
            }

            if (spans.getClass().isArray()) {
                for (Object o : ((Object[]) spans)) {
                    setSpan(o, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            } else {
                setSpan(spans, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    private static boolean isPositionValid(int length, int start, int end) {
        return end > start
                && start >= 0
                && end <= length;
    }

    private static void reverse(Object[] arr) {
        if (arr == null) {
            return;
        }

        int i = 0;
        int j = arr.length - 1;
        Object tmp;
        while (j > i) {
            tmp = arr[j];
            arr[j] = arr[i];
            arr[i] = tmp;
            j--;
            i++;
        }
    }
}