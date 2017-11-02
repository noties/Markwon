package ru.noties.markwon;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

/**
 * This class is used to _revert_ order of applied spans. Original SpannableStringBuilder
 * is using an array to store all the information about spans. So, a span that is added first
 * will be drawn first, which leads to subtle bugs (spans receive wrong `x` values when
 * requested to draw itself)
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class SpannableBuilder {

    // do not implement CharSequence (or any of Spanned interfaces)

    // we will be using SpannableStringBuilder anyway as a backing store
    // as it has tight connection with system (implements some hidden methods, etc)
    private final SpannableStringBuilder builder;
    private final Deque<Span> spans = new ArrayDeque<>(8);

    public SpannableBuilder() {
        this("");
    }

    public SpannableBuilder(@NonNull CharSequence cs) {
        this.builder = new SpannableStringBuilderImpl(cs.toString());
        copySpans(cs);
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

    @NonNull
    public CharSequence remove(int start, int end) {

        // okay: here is what we will try to do:

        final SpannableStringBuilderImpl impl = new SpannableStringBuilderImpl(builder.subSequence(start, end));

        final Iterator<Span> iterator = spans.iterator();

        Span span;

        while (iterator.hasNext() && ((span = iterator.next())) != null) {
            if (span.start >= start && span.end <= end) {
                impl.setSpan(span.what, span.start - start, span.end - start, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                iterator.remove();
            }
        }

        // SHIFT EXISTING!

        if (spans.size() > 0) {

            for (Span s : spans) {

                // if end < start -> not affected
                if (s.end < start) {
                    continue;
                }

                // if end between start & end (which is really bad one) -> make end=start
                if (s.end >= start && s.end <= end) {
                    s.end = start;
                    continue;
                }

                // if start between start&end -> make start=end
                if (s.start >= start && s.start <= end) {
                    s.start = start;
                    // shift end by difference
                    s.end = s.end - (end - start);
                    continue;
                }

                // if after, just shift by difference
                final int diff = end - start;
                s.start = s.start - diff;
                s.end = s.end - diff;
            }
        }

        return impl;
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

        // if called once, it will apply spans, which will modify our state

        applySpans();

        // we could return here for example new SpannableStringBuilder(builder)
        // but, if returned value will be used in other SpannableBuilder,
        // we won't be able to detect in what order to store the spans

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
        int start;
        int end;
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
