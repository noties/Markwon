package io.noties.markwon;

import android.text.SpannableStringBuilder;
import android.text.Spanned;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

/**
 * This class is used to _revert_ order of applied spans. Original SpannableStringBuilder
 * is using an array to store all the information about spans. So, a span that is added first
 * will be drawn first, which leads to subtle bugs (spans receive wrong `x` values when
 * requested to draw itself)
 * <p>
 * since 2.0.0 implements Appendable and CharSequence
 *
 * @since 1.0.1
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class SpannableBuilder implements Appendable, CharSequence {

    /**
     * @since 2.0.0
     */
    public static void setSpans(@NonNull SpannableBuilder builder, @Nullable Object spans, int start, int end) {
        if (spans != null) {

            // setting a span for an invalid position can lead to silent fail (no exception,
            // but execution is stopped)
            if (!isPositionValid(builder.length(), start, end)) {
                return;
            }

            // @since 3.0.1 we introduce another method that recursively applies spans
            // allowing array of arrays (and more)
            setSpansInternal(builder, spans, start, end);
        }
    }

    // @since 2.0.1 package-private visibility for testing
    @VisibleForTesting
    static boolean isPositionValid(int length, int start, int end) {
        return end > start
                && start >= 0
                && end <= length;
    }


    private final StringBuilder builder;

    // actually we might be just using ArrayList
    private final Deque<Span> spans = new ArrayDeque<>(8);

    public SpannableBuilder() {
        this("");
    }

    public SpannableBuilder(@NonNull CharSequence cs) {
        this.builder = new StringBuilder(cs);
        copySpans(0, cs);
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
    @Override
    public SpannableBuilder append(char c) {
        builder.append(c);
        return this;
    }

    @NonNull
    @Override
    public SpannableBuilder append(@NonNull CharSequence cs) {

        copySpans(length(), cs);

        builder.append(cs);

        return this;
    }

    /**
     * @since 2.0.0 to follow Appendable interface
     */
    @NonNull
    @Override
    public SpannableBuilder append(CharSequence csq, int start, int end) {

        final CharSequence cs = csq.subSequence(start, end);
        copySpans(length(), cs);

        builder.append(cs);

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

    @Override
    public int length() {
        return builder.length();
    }

    @Override
    public char charAt(int index) {
        return builder.charAt(index);
    }

    /**
     * @since 2.0.0 to follow CharSequence interface
     */
    @Override
    public CharSequence subSequence(int start, int end) {

        final CharSequence out;

        // @since 2.0.1 we copy spans to resulting subSequence
        final List<Span> spans = getSpans(start, end);
        if (spans.isEmpty()) {
            out = builder.subSequence(start, end);
        } else {

            // we should not be SpannableStringBuilderReversed here
            final SpannableStringBuilder builder = new SpannableStringBuilder(this.builder.subSequence(start, end));

            final int length = builder.length();

            int s;
            int e;

            for (Span span : spans) {

                // we should limit start/end to resulting subSequence length
                //
                // for example, originally it was 5-7 and range 5-7 requested
                // span should have 0-2
                //
                // if a span was fully including resulting subSequence it's start and
                // end must be within 0..length bounds
                s = Math.max(0, span.start - start);
                e = Math.min(length, s + (span.end - span.start));

                builder.setSpan(
                        span.what,
                        s,
                        e,
                        span.flags
                );
            }
            out = builder;
        }

        return out;
    }

    /**
     * This method will return all {@link Span} spans that <em>overlap</em> specified range,
     * so if for example a 1..9 range is specified some spans might have 0..6 or 0..10 start/end ranges.
     * <<<<<<< HEAD:markwon-core/src/main/java/ru/noties/markwon/SpannableBuilder.java
     * NB spans are returned in reversed order (not in order that we store them internally)
     * =======
     * NB spans are returned in reversed order (no in order that we store them internally)
     * >>>>>>> master:markwon/src/main/java/ru/noties/markwon/SpannableBuilder.java
     *
     * @since 2.0.1
     */
    @NonNull
    public List<Span> getSpans(int start, int end) {

        final int length = length();

        if (!isPositionValid(length, start, end)) {
            // we might as well throw here
            return Collections.emptyList();
        }

        // all requested
        if (start == 0
                && length == end) {
            // but also copy (do not allow external modification)
            final List<Span> list = new ArrayList<>(spans);
            Collections.reverse(list);
            return Collections.unmodifiableList(list);
        }

        final List<Span> list = new ArrayList<>(0);

        final Iterator<Span> iterator = spans.descendingIterator();
        Span span;

        while (iterator.hasNext()) {
            span = iterator.next();
            // we must execute 2 checks: if overlap with specified range or fully include it
            // if span.start is >= range.start -> check if it's before range.end
            // if span.end is <= end -> check if it's after range.start
            if (
                    (span.start >= start && span.start < end)
                            || (span.end <= end && span.end > start)
                            || (span.start < start && span.end > end)) {
                list.add(span);
            }
        }

        return Collections.unmodifiableList(list);
    }

    public char lastChar() {
        return builder.charAt(length() - 1);
    }

    @NonNull
    public CharSequence removeFromEnd(int start) {

        // this method is not intended to be used by clients
        // it's a workaround to support tables

        final int end = length();

        // as we do not expose builder and do no apply spans to it, we are safe to NOT to convert to String
        final SpannableStringBuilderReversed impl = new SpannableStringBuilderReversed(builder.subSequence(start, end));

        final Iterator<Span> iterator = spans.iterator();

        Span span;

        while (iterator.hasNext() && ((span = iterator.next())) != null) {
            if (span.start >= start && span.end <= end) {
                impl.setSpan(span.what, span.start - start, span.end - start, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                iterator.remove();
            }
        }

        builder.replace(start, end, "");

        return impl;
    }

    @Override
    @NonNull
    public String toString() {
        return builder.toString();
    }

    @NonNull
    public CharSequence text() {
        // @since 2.0.0 redirects this call to `#spannableStringBuilder()`
        return spannableStringBuilder();
    }

    /**
     * Simple method to create a SpannableStringBuilder, which is created anyway. Unlike {@link #text()}
     * method which returns the same SpannableStringBuilder there is no need to cast the resulting
     * CharSequence and makes the thing more explicit
     *
     * @since 2.0.0
     */
    @NonNull
    public SpannableStringBuilder spannableStringBuilder() {

        // okay, in order to not allow external modification and keep our spans order
        // we should not return our builder
        //
        // plus, if this method was called -> all spans would be applied, which potentially
        // breaks the order that we intend to use
        // so, we will defensively copy builder

        // as we do not expose builder and do no apply spans to it, we are safe to NOT to convert to String

        final SpannableStringBuilderReversed reversed = new SpannableStringBuilderReversed(builder);

        // NB, as e are using Deque -> iteration will be started with last element
        // so, spans will be appearing in the for loop in reverse order
        for (Span span : spans) {
            reversed.setSpan(span.what, span.start, span.end, span.flags);
        }

        return reversed;
    }

    /**
     * @since 3.0.0
     */
    public void clear() {
        builder.setLength(0);
        spans.clear();
    }

    private void copySpans(final int index, @Nullable CharSequence cs) {

        // we must identify already reversed Spanned...
        // and (!) iterate backwards when adding (to preserve order)

        if (cs instanceof Spanned) {

            final Spanned spanned = (Spanned) cs;
            final boolean reversed = spanned instanceof SpannableStringBuilderReversed;

            final Object[] spans = spanned.getSpans(0, spanned.length(), Object.class);
            final int length = spans != null
                    ? spans.length
                    : 0;

            if (length > 0) {
                if (reversed) {
                    Object o;
                    for (int i = length - 1; i >= 0; i--) {
                        o = spans[i];
                        setSpan(
                                o,
                                index + spanned.getSpanStart(o),
                                index + spanned.getSpanEnd(o),
                                spanned.getSpanFlags(o)
                        );
                    }
                } else {
                    Object o;
                    for (int i = 0; i < length; i++) {
                        o = spans[i];
                        setSpan(
                                o,
                                index + spanned.getSpanStart(o),
                                index + spanned.getSpanEnd(o),
                                spanned.getSpanFlags(o)
                        );
                    }
                }
            }
        }
    }

    /**
     * @since 2.0.1 made public in order to be returned from `getSpans` method, initially added in 1.0.1
     */
    public static class Span {

        public final Object what;
        public int start;
        public int end;
        public final int flags;

        Span(@NonNull Object what, int start, int end, int flags) {
            this.what = what;
            this.start = start;
            this.end = end;
            this.flags = flags;
        }
    }

    /**
     * @since 2.0.1 made inner class of {@link SpannableBuilder}, initially added in 1.0.1
     */
    static class SpannableStringBuilderReversed extends SpannableStringBuilder {
        SpannableStringBuilderReversed(CharSequence text) {
            super(text);
        }
    }

    /**
     * @since 3.0.1
     */
    private static void setSpansInternal(
            @NonNull SpannableBuilder builder,
            @Nullable Object spans,
            int start,
            int end) {
        if (spans != null) {
            if (spans.getClass().isArray()) {
                for (Object o : ((Object[]) spans)) {
                    // @since 3.0.1 recursively apply spans (allow array of arrays)
                    setSpansInternal(builder, o, start, end);
                }
            } else {
                builder.setSpan(spans, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }
}
