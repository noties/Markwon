package ru.noties.markwon.renderer;

import android.support.annotation.Nullable;
import android.text.SpannableStringBuilder;

/**
 * Copied with modification and renaming from @see <a href="https://github.com/Uncodin/bypass/blob/master/platform/android/
 * library/src/in/uncod/android/bypass/ReverseSpannableStringBuilder.java">Uncodin/bypass</a>
 */
public class SpannableBuilder extends SpannableStringBuilder {

    public SpannableBuilder() {
        super();
    }

    SpannableBuilder(CharSequence text, int start, int end) {
        super(text, start, end);
    }

    @Override
    public <T> T[] getSpans(int queryStart, int queryEnd, Class<T> kind) {
        T[] ret = super.getSpans(queryStart, queryEnd, kind);
        reverse(ret);
        return ret;
    }

    @Override
    public void setSpan(@Nullable Object what, int start, int end, int flags) {
        if (what != null && isRangeValid(start, end)) {
            if (what.getClass().isArray()) {
                for (final Object span : (Object[]) what) {
                    setSpan(span, start, end, flags);
                }
            } else {
                super.setSpan(what, start, end, flags);
            }
        }
    }

    private boolean isRangeValid(int start, int end) {
        final int len;
        return end >= start &&
                (start <= (len = length()) && end <= len) &&
                start >= 0 && end >= 0;
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
