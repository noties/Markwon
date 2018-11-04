package ru.noties.markwon;

import android.support.annotation.NonNull;

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
import static ru.noties.markwon.SpannableBuilder.isPositionValid;

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

//    @Test
//    public void set_spans_position_invalid() {
//        // will be silently ignored
//    }

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