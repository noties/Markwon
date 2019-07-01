package io.noties.markwon.test;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class to validate spannable content
 *
 * @since 3.0.0
 */
public abstract class TestSpan {

    @NonNull
    public static TestSpan.Document document(TestSpan... children) {
        return new TestSpanDocument(children(children));
    }

    @NonNull
    public static TestSpan.Span span(@NonNull String name, TestSpan... children) {
        return span(name, Collections.<String, Object>emptyMap(), children);
    }

    @NonNull
    public static TestSpan.Span span(@NonNull String name, @NonNull Map<String, Object> arguments, TestSpan... children) {
        return new TestSpanSpan(name, children(children), arguments);
    }

    @NonNull
    public static TestSpan.Text text(@NonNull String literal) {
        return new TestSpanText(literal);
    }

    @NonNull
    public static List<TestSpan> children(TestSpan... children) {
        final int length = children.length;
        final List<TestSpan> list;
        if (length == 0) {
            list = Collections.emptyList();
        } else if (length == 1) {
            list = Collections.singletonList(children[0]);
        } else {
            final List<TestSpan> spans = new ArrayList<>(length);
            Collections.addAll(spans, children);
            list = Collections.unmodifiableList(spans);
        }
        return list;
    }

    @NonNull
    public static Map<String, Object> args(Object... args) {

        final int length = args.length;
        if (length == 0) {
            return Collections.emptyMap();
        }

        // validate that length is even (k=v)
        if ((length % 2) != 0) {
            throw new IllegalStateException("Supplied key-values array must contain " +
                    "even number of arguments");
        }

        final Map<String, Object> map = new HashMap<>(length / 2 + 1);

        String key;
        Object value;

        for (int i = 0; i < length; i += 2) {
            // possible class-cast exception
            key = (String) args[i];
            value = args[i + 1];
            map.put(key, value);
        }

        return Collections.unmodifiableMap(map);
    }


    @NonNull
    public abstract List<TestSpan> children();

    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(Object o);


    public static abstract class Document extends TestSpan {

        @NonNull
        public abstract String wholeText();
    }

    public static abstract class Text extends TestSpan {

        @NonNull
        public abstract String literal();

        public abstract int length();
    }

    // important: children should not be included in equals...
    public static abstract class Span extends TestSpan {

        @NonNull
        public abstract String name();

        @NonNull
        public abstract Map<String, Object> arguments();

        @NonNull
        @Override
        public abstract List<TestSpan> children();
    }

    // package-private constructor
    TestSpan() {
    }
}
