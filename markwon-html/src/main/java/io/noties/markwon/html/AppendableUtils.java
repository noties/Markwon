package io.noties.markwon.html;

import androidx.annotation.NonNull;

import java.io.IOException;

abstract class AppendableUtils {

    static void appendQuietly(@NonNull Appendable appendable, char c) {
        try {
            appendable.append(c);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static void appendQuietly(@NonNull Appendable appendable, @NonNull CharSequence cs) {
        try {
            appendable.append(cs);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static void appendQuietly(@NonNull Appendable appendable, @NonNull CharSequence cs, int start, int end) {
        try {
            appendable.append(cs, start, end);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private AppendableUtils() {
    }
}
