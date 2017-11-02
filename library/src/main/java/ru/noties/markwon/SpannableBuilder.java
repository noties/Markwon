package ru.noties.markwon;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * This class is used to _revert_ order of applied spans. Original SpannableStringBuilder
 * is using an array to store all the information about spans. So, a span that is added first
 * will be drawn first, which leads to subtle bugs (spans receive wrong `x` values when
 * requested to draw itself)
 */
public class SpannableBuilder {

    // do not implement CharSequence (or any of Spanned interfaces)

    // we will be using SpannableStringBuilder anyway as a backing store
    // as it has tight connection with system (implements some hidden methods, etc)
    private final SpannableStringBuilder builder;
    private final Deque<Span> spans = new ArrayDeque<>(8);

    public SpannableBuilder() {
        this(null);
    }

    public SpannableBuilder(@Nullable CharSequence cs) {

        final CharSequence text;

        if (cs != null) {
            text = cs;
        } else {
            text = null;
        }

        if (text == null) {
            this.builder = new SpannableStringBuilderImpl();
        } else {
            this.builder = new SpannableStringBuilderImpl(text.toString());
            copySpans(text);
        }
    }

    /**
     * Additional method that takes a String, which is proven to NOT contain any spans
     *
     * @param text String to append
     * @return this instance
     */
    @NonNull
    public SpannableBuilder append(@NonNull String text) {
        builder.append(text);
        return this;
    }

    @NonNull
    public SpannableBuilder append(char c) {
        builder.append(c);
        return this;
    }

    @NonNull
    public SpannableBuilder append(@NonNull CharSequence cs) {

        copySpans(cs);

        builder.append(cs.toString());

        return this;
    }

    @NonNull
    public SpannableBuilder append(@NonNull CharSequence cs, @NonNull Object span) {
        final int length = length();
        append(cs);
        setSpan(span, length);
        return this;
    }

    @NonNull
    public SpannableBuilder append(@NonNull CharSequence cs, @NonNull Object span, int flags) {
        final int length = length();
        append(cs);
        setSpan(span, length, length(), flags);
        return this;
    }

    @NonNull
    public SpannableBuilder setSpan(@NonNull Object span, int start) {
        return setSpan(span, start, length());
    }

    @NonNull
    public SpannableBuilder setSpan(@NonNull Object span, int start, int end) {
        return setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    @NonNull
    public SpannableBuilder setSpan(@NonNull Object span, int start, int end, int flags) {
        spans.push(new Span(span, start, end, flags));
        return this;
    }

    public int length() {
        return builder.length();
    }

    public char charAt(int index) {
        return builder.charAt(index);
    }

    public char lastChar() {
        return builder.charAt(length() - 1);
    }

    @Override
    @NonNull
    public String toString() {
        return builder.toString();
    }

    // Unfortunately I cannot see any way to NOT expose this internal value, which opens a gate
    // to external modification (first of all InputFilters, that potentially break span indexes
    // as we keep track of them independently). Must warn user to NOT apply inputFilters
    @NonNull
    public CharSequence text() {
        applySpans();
        return builder;
    }

    private void copySpans(@Nullable CharSequence cs) {

        // we must identify already reversed Spanned...
        // and (!) iterate backwards when adding (to preserve order)

        if (cs instanceof Spanned) {

            final Spanned spanned = (Spanned) cs;
            final boolean reverse = spanned instanceof SpannedReversed;
            final int index = length();

            final Object[] spans = spanned.getSpans(0, spanned.length(), Object.class);

            iterate(reverse, spans, new Action() {
                @Override
                public void apply(Object o) {
                    setSpan(
                            o,
                            index + spanned.getSpanStart(o),
                            index + spanned.getSpanEnd(o),
                            spanned.getSpanFlags(o)
                    );
                }
            });
        }
    }

    private void applySpans() {

        // will apply appended spans in reverse order
        // clear the stack (that keeps track of them)

        Span span;
        while ((span = spans.poll()) != null) {
            builder.setSpan(span.what, span.start, span.end, span.flags);
        }
    }

    private static class Span {

        final Object what;
        final int start;
        final int end;
        final int flags;

        Span(@NonNull Object what, int start, int end, int flags) {
            this.what = what;
            this.start = start;
            this.end = end;
            this.flags = flags;
        }
    }

    private interface Action {
        void apply(Object o);
    }

    private static void iterate(boolean reverse, @Nullable Object[] array, @NonNull Action action) {
        final int length = array != null
                ? array.length
                : 0;
        if (length > 0) {
            if (reverse) {
                for (int i = length - 1; i >= 0; i--) {
                    action.apply(array[i]);
                }
            } else {
                for (int i = 0; i < length; i++) {
                    action.apply(array[i]);
                }
            }
        }
    }
}
