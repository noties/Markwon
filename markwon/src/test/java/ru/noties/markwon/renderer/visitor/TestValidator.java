package ru.noties.markwon.renderer.visitor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;

import java.util.Map;

import ix.Ix;
import ix.IxPredicate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

abstract class TestValidator {

    abstract int validate(
            @NonNull SpannableStringBuilder builder,
            int index,
            @NonNull TestNode node);

    abstract int processedSpanNodesCount();


    @NonNull
    static TestValidator create(@NonNull String id) {
        return new Impl(id);
    }

    static class Impl extends TestValidator {

        private final String id;

        private int processedCount;

        Impl(@NonNull String id) {
            this.id = id;
        }

        @Override
        int validate(
                @NonNull final SpannableStringBuilder builder,
                final int index,
                @NonNull TestNode node) {

            if (node.isText()) {

                final String text;
                {
                    final String content = node.getAsText().text();

                    // code is a special case as we wrap it around non-breakable spaces
                    final TestNode parent = node.parent();
                    if (parent != null) {
                        final TestNode.Span span = parent.getAsSpan();
                        if (TestSpan.CODE.equals(span.name())) {
                            text = "\u00a0" + content + "\u00a0";
                        } else if (TestSpan.CODE_BLOCK.equals(span.name())) {
                            text = "\u00a0\n" + content + "\n\u00a0";
                        } else {
                            text = content;
                        }
                    } else {
                        text = content;
                    }
                }

                assertEquals(
                        String.format("text: %s, position: {%d-%d}", text, index, index + text.length()),
                        text,
                        builder.subSequence(index, index + text.length()).toString());

                return index + text.length();
            }

            final TestNode.Span span = node.getAsSpan();
            processedCount += 1;

            int out = index;

            for (TestNode child : span.children()) {
                out = validate(builder, out, child);
            }

            final int end = out;

            // we can possibly have parent spans here, should filter them
            final Object[] spans = builder.getSpans(index, out, Object.class);

            // expected span{name, attributes} at position{start-end}, with text: `%s`, spans: []


            assertTrue(
                    message(span, index, end, builder, spans),
                    spans != null
            );

            final TestSpan testSpan = Ix.fromArray(spans)
                    .filter(new IxPredicate<Object>() {
                        @Override
                        public boolean test(Object o) {
                            return o instanceof TestSpan;
                        }
                    })
                    .cast(TestSpan.class)
                    .filter(new IxPredicate<TestSpan>() {
                        @Override
                        public boolean test(TestSpan testSpan) {

                            // in case of nested spans with the same name (lists)
                            // we also must validate attributes
                            // and thus we are moving most of assertions to this filter method
                            return span.name().equals(testSpan.name())
                                    && index == builder.getSpanStart(testSpan)
                                    && end == builder.getSpanEnd(testSpan)
                                    && mapEquals(span.attributes(), testSpan.attributes());
                        }
                    })
                    .first(null);

            assertNotNull(
                    message(span, index, end, builder, spans),
                    testSpan
            );

            return out;
        }

        @Override
        int processedSpanNodesCount() {
            return processedCount;
        }

        private static boolean mapEquals(
                @NonNull Map<String, String> expected,
                @NonNull Map<String, String> actual) {

            if (expected.size() != actual.size()) {
                return false;
            }

            boolean result = true;

            for (Map.Entry<String, String> entry : expected.entrySet()) {
                if (!actual.containsKey(entry.getKey())
                        || !equals(entry.getValue(), actual.get(entry.getKey()))) {
                    result = false;
                    break;
                }
            }

            return result;
        }

        private static boolean equals(@Nullable Object o1, @Nullable Object o2) {
            return o1 != null
                    ? o1.equals(o2)
                    : o2 == null;
        }

        @NonNull
        private static String message(
                @NonNull TestNode.Span span,
                int start,
                int end,
                @NonNull Spanned text,
                @Nullable Object[] spans) {
            final String spansText;
            if (spans == null
                    || spans.length == 0) {
                spansText = "[]";
            } else {
                final StringBuilder builder = new StringBuilder();
                for (Object o : spans) {
                    final TestSpan testSpan = (TestSpan) o;
                    if (builder.length() > 0) {
                        builder.append(", ");
                    }

                    builder
                            .append("{name: '").append(testSpan.name()).append('\'')
                            .append(", position{").append(start).append(", ").append(end).append('}');

                    if (testSpan.attributes().size() > 0) {
                        builder.append(", attributes: ").append(testSpan.attributes());
                    }

                    builder.append('}');
                }
                spansText = builder.toString();
            }
            return String.format("Expected span: %s at position{%d-%d} with text `%s`, spans: %s",
                    span, start, end, text.subSequence(start, end), spansText
            );
        }
    }
}
