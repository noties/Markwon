package io.noties.markwon;

import androidx.annotation.NonNull;
import android.text.SpannableStringBuilder;
import android.text.Spanned;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.List;

import ix.Ix;
import ix.IxFunction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static io.noties.markwon.SpannableBuilder.isPositionValid;
import static io.noties.markwon.SpannableBuilder.setSpans;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class SpannableBuilderTest {

    private SpannableBuilder builder;

    @Before
    public void before() {
        builder = new SpannableBuilder();
    }

    @Test
    public void position_invalid() {

        final Position[] positions = {
                Position.of(0, 0, 0),
                Position.of(-1, -1, -1),
                Position.of(0, -1, 1),
                Position.of(1, 1, 1),
                Position.of(0, 0, 10),
                Position.of(10, 10, 0),
                Position.of(10, 5, 2),
                Position.of(5, 1, 1)
        };

        for (Position position : positions) {
            assertFalse(position.toString(), isPositionValid(position.length, position.start, position.end));
        }
    }

    @Test
    public void position_valid() {

        final Position[] positions = {
                Position.of(1, 0, 1),
                Position.of(2, 0, 1),
                Position.of(2, 1, 2),
                Position.of(10, 0, 10),
                Position.of(7, 6, 7)
        };

        for (Position position : positions) {
            assertTrue(position.toString(), isPositionValid(position.length, position.start, position.end));
        }
    }

    @Test
    public void get_spans() {

        // all spans that overlap with specified range or spans that include it fully -> should be returned

        final int length = 10;

        for (int i = 0; i < length; i++) {
            builder.append(String.valueOf(i));
        }

        for (int start = 0, end = length - 1; start < end; start++, end--) {
            builder.setSpan("" + start + "-" + end, start, end);
        }

        // all (simple check that spans that take range greater that supplied range are also returned)
        final List<String> all = Arrays.asList("0-9", "1-8", "2-7", "3-6", "4-5");
        for (int start = 0, end = length - 1; start < end; start++, end--) {
            assertEquals(
                    "" + start + "-" + end,
                    all,
                    getSpans(start, end)
            );
        }

        assertEquals(
                "1-3",
                Arrays.asList("0-9", "1-8", "2-7"),
                getSpans(1, 3)
        );

        assertEquals(
                "1-10",
                all,
                getSpans(1, 10)
        );

        assertEquals(
                "5-10",
                Arrays.asList("0-9", "1-8", "2-7", "3-6"),
                getSpans(5, 10)
        );

        assertEquals(
                "7-10",
                Arrays.asList("0-9", "1-8"),
                getSpans(7, 10)
        );
    }

    @Test
    public void get_spans_out_of_range() {

        // let's test that if span.start >= range.start -> it will be less than range.end
        // if span.end <= end -> it will be greater than range.start

        for (int i = 0; i < 10; i++) {
            builder.append(String.valueOf(i));
            builder.setSpan("" + i + "-" + (i + 1), i, i + 1);
        }

        assertEquals(10, getSpans(0, 10).size());

        // so
        //  0-1
        //  1-2
        //  2-3
        //  etc

        //noinspection ArraysAsListWithZeroOrOneArgument
        assertEquals(
                "0-1",
                Arrays.asList("0-1"),
                getSpans(0, 1)
        );

        assertEquals(
                "1-5",
                Arrays.asList("1-2", "2-3", "3-4", "4-5"),
                getSpans(1, 5)
        );
    }

    @NonNull
    private List<String> getSpans(int start, int end) {
        return Ix.from(builder.getSpans(start, end))
                .map(new IxFunction<SpannableBuilder.Span, String>() {
                    @Override
                    public String apply(SpannableBuilder.Span span) {
                        return (String) span.what;
                    }
                })
                .toList();
    }

    @Test
    public void set_spans_position_invalid() {
        // if supplied position is invalid, no spans should be added

        builder.append('0');

        assertTrue(builder.getSpans(0, builder.length()).isEmpty());

        setSpans(builder, new Object(), -1, -1);

        assertTrue(builder.getSpans(0, builder.length()).isEmpty());
    }

    @Test
    public void set_spans_single() {
        // single span as `spans` argument correctly added

        builder.append('0');

        assertTrue(builder.getSpans(0, builder.length()).isEmpty());

        final Object span = new Object();
        setSpans(builder, span, 0, 1);

        final List<SpannableBuilder.Span> spans = builder.getSpans(0, builder.length());
        assertEquals(1, spans.size());
        assertEquals(span, spans.get(0).what);
    }

    @Test
    public void set_spans_array_detected() {
        // if supplied `spans` argument is an array -> it should be expanded

        builder.append('0');

        assertTrue(builder.getSpans(0, builder.length()).isEmpty());

        final Object[] spans = {
                new Object(),
                new Object(),
                new Object()
        };

        setSpans(builder, spans, 0, 1);

        final List<SpannableBuilder.Span> actual = builder.getSpans(0, builder.length());
        assertEquals(spans.length, actual.size());

        for (int i = 0, length = spans.length; i < length; i++) {
            assertEquals(spans[i], actual.get(i).what);
        }
    }

    @Test
    public void set_spans_array_of_arrays() {
        // if array of arrays is supplied -> it won't be expanded to single elements

        builder.append('0');

        assertTrue(builder.getSpans(0, builder.length()).isEmpty());

        final Object[] flatSpans = {
                new Object(),
                new Object(),
                new Object(),
                new Object(),
                new Object()
        };

        final Object[] spans = {
                new Object[]{
                        flatSpans[0], flatSpans[1]
                },
                new Object[]{
                        flatSpans[2], flatSpans[3], flatSpans[4]
                }
        };

        setSpans(builder, spans, 0, 1);

        final List<SpannableBuilder.Span> actual = builder.getSpans(0, builder.length());
        assertEquals(flatSpans.length, actual.size());

        for (int i = 0, length = spans.length; i < length; i++) {
            assertEquals(flatSpans[i], actual.get(i).what);
        }
    }

    @Test
    public void set_spans_null() {
        // if `spans` argument is null, then nothing will be added

        builder.append('0');

        assertTrue(builder.getSpans(0, builder.length()).isEmpty());

        setSpans(builder, null, 0, builder.length());

        assertTrue(builder.getSpans(0, builder.length()).isEmpty());
    }

    @Test
    public void spans_reversed() {
        // resulting SpannableStringBuilder should have spans reversed

        final Object[] spans = {
                0,
                1,
                2
        };

        for (Object span : spans) {
            builder.append(span.toString(), span);
        }

        final SpannableStringBuilder spannableStringBuilder = builder.spannableStringBuilder();
        final Object[] actual = spannableStringBuilder.getSpans(0, builder.length(), Object.class);

        for (int start = 0, length = spans.length, end = length - 1; start < length; start++, end--) {
            assertEquals(spans[start], actual[end]);
        }
    }

    @Test
    public void append_spanned_normal() {
        // #append is called with regular Spanned content -> spans should be added in reverse

        final SpannableStringBuilder ssb = new SpannableStringBuilder();
        for (int i = 0; i < 3; i++) {
            ssb.append(String.valueOf(i));
            ssb.setSpan(i, i, i + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        assertTrue(builder.getSpans(0, builder.length()).isEmpty());

        builder.append(ssb);

        assertEquals("012", builder.toString());

        // this one would return normal order as spans are reversed here
//        final List<SpannableBuilder.Span> spans = builder.getSpans(0, builder.length());

        final SpannableStringBuilder spannableStringBuilder = builder.spannableStringBuilder();
        final Object[] spans = spannableStringBuilder.getSpans(0, builder.length(), Object.class);
        assertEquals(3, spans.length);

        for (int i = 0, length = spans.length; i < length; i++) {
            assertEquals(length - 1 - i, spans[i]);
        }
    }

    @Test
    public void append_spanned_reversed() {
        // #append is called with reversed spanned content -> spans should be added as-are

        final SpannableBuilder spannableBuilder = new SpannableBuilder();
        for (int i = 0; i < 3; i++) {
            spannableBuilder.append(String.valueOf(i), i);
        }

        assertTrue(builder.getSpans(0, builder.length()).isEmpty());

        builder.append(spannableBuilder.spannableStringBuilder());

        final SpannableStringBuilder spannableStringBuilder = builder.spannableStringBuilder();
        final Object[] spans = spannableStringBuilder.getSpans(0, spannableStringBuilder.length(), Object.class);
        assertEquals(3, spans.length);

        for (int i = 0, length = spans.length; i < length; i++) {
            // in the end order should be as we expect in order to properly render it
            // (no matter if reversed is used or not)
            assertEquals(length - 1 - i, spans[i]);
        }
    }

    private static class Position {

        @NonNull
        static Position of(int length, int start, int end) {
            return new Position(length, start, end);
        }

        final int length;
        final int start;
        final int end;

        private Position(int length, int start, int end) {
            this.length = length;
            this.start = start;
            this.end = end;
        }

        @Override
        public String toString() {
            return "Position{" +
                    "length=" + length +
                    ", start=" + start +
                    ", end=" + end +
                    '}';
        }
    }
}