package io.noties.markwon.test;

import android.text.Spanned;

import androidx.annotation.NonNull;

import org.junit.Assert;
import org.junit.ComparisonFailure;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import ix.Ix;
import ix.IxPredicate;

public abstract class TestSpanMatcher {

    public static void matches(@NonNull final Spanned spanned, @NonNull TestSpan.Document document) {

        // assert number for spans
        // assert raw text

        final TestSpanEnumerator enumerator = new TestSpanEnumerator();

        // keep track of total spans encountered
        final AtomicInteger counter = new AtomicInteger();

        enumerator.enumerate(document, new TestSpanEnumerator.Listener() {
            @Override
            public void onNext(final int start, final int end, @NonNull TestSpan span) {
                if (span instanceof TestSpan.Document) {

                    TestSpanMatcher.documentMatches(spanned, (TestSpan.Document) span);
                } else if (span instanceof TestSpan.Span) {

                    // increment span count so after enumeration we match total number of spans
                    counter.incrementAndGet();

                    TestSpanMatcher.spanMatches(spanned, start, end, (TestSpan.Span) span);

                } else if (span instanceof TestSpan.Text) {
                    TestSpanMatcher.textMatches(spanned, start, end, (TestSpan.Text) span);
                } else {
                    // in case we add a new type
                    throw new IllegalStateException("Unexpected type of a TestSpan: `"
                            + span.getClass().getName() + "`, " + span);
                }
            }
        });

        final Object[] spans = spanned.getSpans(0, spanned.length(), Object.class);
        Assert.assertEquals("Total spans count", counter.get(), spans.length);
    }

    public static void documentMatches(
            @NonNull Spanned spanned,
            @NonNull TestSpan.Document document) {

        // match full text

        final String expected = document.wholeText();
        final String actual = spanned.toString();

        if (!expected.equals(actual)) {
            throw new ComparisonFailure(
                    "Document text mismatch",
                    expected,
                    actual);
        }
    }

    public static void spanMatches(
            @NonNull final Spanned spanned,
            final int start,
            final int end,
            @NonNull final TestSpan.Span expected) {

        // when queried multiple spans can be returned (for example if one span
        // wraps another one. so [0 1 [2 3] 4 5] where [] represents start/end of
        // a span of same type, when queried for spans at 2-3 position, both will be returned
        final TestSpan.Span actual = Ix.fromArray(spanned.getSpans(start, end, Object.class))
                .cast(TestSpan.Span.class)
                .filter(new IxPredicate<TestSpan.Span>() {
                    @Override
                    public boolean test(TestSpan.Span span) {
                        return expected.name().equals(span.name())
                                && start == spanned.getSpanStart(span)
                                && end == spanned.getSpanEnd(span)
                                && expected.arguments().equals(span.arguments());
                    }
                })
                .first(null);

        if (!expected.equals(actual)) {

            final String expectedSpan = expected.arguments().isEmpty()
                    ? expected.name()
                    : expected.name() + ": " + expected.arguments();

            final String actualSpan;
            if (actual == null) {
                actualSpan = "null";
            } else {
                actualSpan = actual.arguments().isEmpty()
                        ? actual.name()
                        : actual.name() + ": " + actual.arguments();
            }

            throw new AssertionError(
                    String.format(Locale.US, "Expected span{%s} at {start: %d, end: %d}, found: %s, text: \"%s\"",
                            expectedSpan, start, end, actualSpan, spanned.subSequence(start, end)));
        }
    }

    public static void textMatches(
            @NonNull Spanned spanned,
            int start,
            int end,
            @NonNull TestSpan.Text text) {

        final String expected = text.literal();
        final String actual = spanned.subSequence(start, end).toString();

        if (!expected.equals(actual)) {
            throw new ComparisonFailure(
                    String.format(Locale.US, "Text mismatch at {start: %d, end: %d}", start, end),
                    expected,
                    actual
            );
        }
    }

    private TestSpanMatcher() {
    }
}
