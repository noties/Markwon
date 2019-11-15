package io.noties.markwon.editor;

import android.text.SpannableStringBuilder;
import android.text.Spanned;

import androidx.annotation.NonNull;

abstract class SpannableUtils {

    static void append(@NonNull SpannableStringBuilder builder, @NonNull String text, Object... spans) {
        final int start = builder.length();
        builder.append(text);
        final int end = builder.length();
        for (Object span : spans) {
            builder.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    private SpannableUtils() {
    }
}
