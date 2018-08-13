package ru.noties.markwon;

import android.text.SpannableStringBuilder;

/**
 * Copied as is from @see <a href = "https://github.com/Uncodin/bypass/blob/master/platform/android/
 * library/src/in/uncod/android/bypass/ReverseSpannableStringBuilder.java">Uncodin/bypass</a>
 */
public class ReverseSpannableStringBuilder extends SpannableStringBuilder {

    public ReverseSpannableStringBuilder() {
        super();
    }

    public ReverseSpannableStringBuilder(CharSequence text, int start, int end) {
        super(text, start, end);
    }

    @Override
    public <T> T[] getSpans(int queryStart, int queryEnd, Class<T> kind) {
        T[] ret = super.getSpans(queryStart, queryEnd, kind);
        reverse(ret);
        return ret;
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
