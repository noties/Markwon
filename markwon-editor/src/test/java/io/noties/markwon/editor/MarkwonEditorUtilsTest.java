package io.noties.markwon.editor;

import android.text.SpannableStringBuilder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.noties.markwon.editor.MarkwonEditorUtils.Match;

import static io.noties.markwon.editor.MarkwonEditorUtils.findDelimited;
import static io.noties.markwon.editor.SpannableUtils.append;
import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class MarkwonEditorUtilsTest {

    @Test
    public void extract_spans() {

        final class One {
        }
        final class Two {
        }
        final class Three {
        }

        final SpannableStringBuilder builder = new SpannableStringBuilder();
        append(builder, "one", new One());
        append(builder, "two", new Two(), new Two());
        append(builder, "three", new Three(), new Three(), new Three());

        final Map<Class<?>, List<Object>> map = MarkwonEditorUtils.extractSpans(
                builder,
                Arrays.asList(One.class, Three.class));

        assertEquals(2, map.size());

        assertNotNull(map.get(One.class));
        assertNull(map.get(Two.class));
        assertNotNull(map.get(Three.class));

        //noinspection ConstantConditions
        assertEquals(1, map.get(One.class).size());
        //noinspection ConstantConditions
        assertEquals(3, map.get(Three.class).size());
    }

    @Test
    public void delimited_single() {
        final String input = "**bold**";
        final Match match = findDelimited(input, 0, "**");
        assertMatched(input, match, "**", 0, input.length());
    }

    @Test
    public void delimited_multiple() {
        final String input = "**bold**";
        final Match match = findDelimited(input, 0, "**", "__");
        assertMatched(input, match, "**", 0, input.length());
    }

    @Test
    public void delimited_em() {
        // for example we will try to match `*` or `_` and our implementation will find first
        final String input = "**_em_**"; // problematic for em...
        final Match match = findDelimited(input, 0, "_", "*");
        assertMatched(input, match, "_", 2, 6);
    }

    @Test
    public void delimited_bold_em_strike() {
        final String input = "**_~~dude~~_**";

        final Match bold = findDelimited(input, 0, "**", "__");
        final Match em = findDelimited(input, 0, "*", "_");
        final Match strike = findDelimited(input, 0, "~~");

        assertMatched(input, bold, "**", 0, input.length());
        assertMatched(input, em, "_", 2, 12);
        assertMatched(input, strike, "~~", 3, 11);
    }

    @Test
    public void delimited_triple_asterisks() {
        final String input = "***italic bold bold***";

        final Match bold = findDelimited(input, 0, "**", "__");
        final Match em = findDelimited(input, 0, "*", "_");

        assertMatched(input, bold, "**", 0, input.length());
        assertMatched(input, em, "*", 0, input.length());
    }

    @Test
    public void delimited_triple_asterisks_2() {
        final String input = "***italic bold* bold**";

        final Match bold = findDelimited(input, 0, "**", "__");
        final Match em = findDelimited(input, 0, "*", "_");

        assertMatched(input, bold, "**", 0, input.length());
        assertMatched(input, em, "*", 0, 15);
    }

    private static void assertMatched(
            @NonNull String input,
            @Nullable Match match,
            @NonNull String delimiter,
            int start,
            int end) {
        assertNotNull(format(Locale.ROOT, "delimiter: '%s', input: '%s'", delimiter, input), match);
        final String m = format(Locale.ROOT, "input: '%s', match: %s", input, match);
        assertEquals(m, delimiter, match.delimiter());
        assertEquals(m, start, match.start());
        assertEquals(m, end, match.end());
    }
}
