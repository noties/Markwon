package io.noties.markwon.editor;

import android.text.Spanned;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @since 4.2.0
 */
public abstract class MarkwonEditorUtils {

    @NonNull
    public static Map<Class<?>, List<Object>> extractSpans(@NonNull Spanned spanned, @NonNull Collection<Class<?>> types) {

        final Object[] spans = spanned.getSpans(0, spanned.length(), Object.class);
        final Map<Class<?>, List<Object>> map = new HashMap<>(3);

        Class<?> type;

        for (Object span : spans) {
            type = span.getClass();
            if (types.contains(type)) {
                List<Object> list = map.get(type);
                if (list == null) {
                    list = new ArrayList<>(3);
                    map.put(type, list);
                }
                list.add(span);
            }
        }

        return map;
    }

    public interface Match {

        @NonNull
        String delimiter();

        int start();

        int end();
    }

    @Nullable
    public static Match findDelimited(@NonNull String input, int startFrom, @NonNull String delimiter) {
        final int start = input.indexOf(delimiter, startFrom);
        if (start > -1) {
            final int length = delimiter.length();
            final int end = input.indexOf(delimiter, start + length);
            if (end > -1) {
                return new MatchImpl(delimiter, start, end + length);
            }
        }
        return null;
    }

    @Nullable
    public static Match findDelimited(
            @NonNull String input,
            int start,
            @NonNull String delimiter1,
            @NonNull String delimiter2) {

        final int l1 = delimiter1.length();
        final int l2 = delimiter2.length();

        final char c1 = delimiter1.charAt(0);
        final char c2 = delimiter2.charAt(0);

        char c;
        char previousC = 0;

        Match match;

        for (int i = start, length = input.length(); i < length; i++) {
            c = input.charAt(i);

            // if this char is the same as previous (and we obviously have no match) -> skip
            if (c == previousC) {
                continue;
            }

            if (c == c1) {
                match = matchDelimiter(input, i, length, delimiter1, l1);
                if (match != null) {
                    return match;
                }
            } else if (c == c2) {
                match = matchDelimiter(input, i, length, delimiter2, l2);
                if (match != null) {
                    return match;
                }
            }

            previousC = c;
        }

        return null;
    }

    // This method assumes that first char is matched already
    @Nullable
    private static Match matchDelimiter(
            @NonNull String input,
            int start,
            int length,
            @NonNull String delimiter,
            int delimiterLength) {

        if (start + delimiterLength < length) {

            boolean result = true;

            for (int i = 1; i < delimiterLength; i++) {
                if (input.charAt(start + i) != delimiter.charAt(i)) {
                    result = false;
                    break;
                }
            }

            if (result) {
                // find end
                final int end = input.indexOf(delimiter, start + delimiterLength);
                // it's important to check if match has content
                if (end > -1 && (end - start) > delimiterLength) {
                    return new MatchImpl(delimiter, start, end + delimiterLength);
                }
            }
        }

        return null;
    }

    private MarkwonEditorUtils() {
    }

    private static class MatchImpl implements Match {

        private final String delimiter;
        private final int start;
        private final int end;

        MatchImpl(@NonNull String delimiter, int start, int end) {
            this.delimiter = delimiter;
            this.start = start;
            this.end = end;
        }

        @NonNull
        @Override
        public String delimiter() {
            return delimiter;
        }

        @Override
        public int start() {
            return start;
        }

        @Override
        public int end() {
            return end;
        }

        @Override
        @NonNull
        public String toString() {
            return "MatchImpl{" +
                    "delimiter='" + delimiter + '\'' +
                    ", start=" + start +
                    ", end=" + end +
                    '}';
        }
    }
}
