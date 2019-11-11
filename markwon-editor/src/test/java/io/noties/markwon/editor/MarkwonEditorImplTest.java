package io.noties.markwon.editor;

import android.text.SpannableStringBuilder;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import io.noties.markwon.Markwon;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class MarkwonEditorImplTest {

    @Test
    public void process() {
        // create markwon
        final Markwon markwon = Markwon.create(RuntimeEnvironment.application);

        // default punctuation
        final MarkwonEditor editor = MarkwonEditor.create(markwon);

        final SpannableStringBuilder builder = new SpannableStringBuilder("**bold**");

        editor.process(builder);

        final PunctuationSpan[] spans = builder.getSpans(0, builder.length(), PunctuationSpan.class);
        assertEquals(2, spans.length);

        final PunctuationSpan first = spans[0];
        assertEquals(0, builder.getSpanStart(first));
        assertEquals(2, builder.getSpanEnd(first));

        final PunctuationSpan second = spans[1];
        assertEquals(6, builder.getSpanStart(second));
        assertEquals(8, builder.getSpanEnd(second));
    }
}
