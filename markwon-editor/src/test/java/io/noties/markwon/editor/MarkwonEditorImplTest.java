package io.noties.markwon.editor;

import android.text.SpannableStringBuilder;
import android.text.Spanned;

import androidx.annotation.NonNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.noties.markwon.Markwon;
import io.noties.markwon.editor.MarkwonEditorImpl.EditSpanStoreImpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class MarkwonEditorImplTest {

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

        final Map<Class<?>, List<Object>> map = MarkwonEditorImpl.extractSpans(
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

    private static void append(@NonNull SpannableStringBuilder builder, @NonNull String text, Object... spans) {
        final int start = builder.length();
        builder.append(text);
        final int end = builder.length();
        for (Object span : spans) {
            builder.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    @Test
    public void edit_span_store_span_not_included() {
        // When store is requesting a span that is not included -> exception is raised

        final Map<Class<?>, MarkwonEditor.EditSpanFactory> map = Collections.emptyMap();

        final EditSpanStoreImpl impl = new EditSpanStoreImpl(new SpannableStringBuilder(), map);

        try {
            impl.get(Object.class);
            fail();
        } catch (IllegalStateException e) {
            assertTrue(e.getMessage(), e.getMessage().contains("not registered, use Builder#includeEditSpan method to register"));
        }
    }

    @Test
    public void edit_span_store_reuse() {
        // when a span is present in supplied spannable -> it will be used

        final class One {
        }
        final SpannableStringBuilder builder = new SpannableStringBuilder();
        final One one = new One();
        append(builder, "One", one);

        final Map<Class<?>, MarkwonEditor.EditSpanFactory> map = new HashMap<Class<?>, MarkwonEditor.EditSpanFactory>() {{
            // null in case it _will_ be used -> thus NPE
            put(One.class, null);
        }};

        final EditSpanStoreImpl impl = new EditSpanStoreImpl(builder, map);

        assertEquals(one, impl.get(One.class));
    }

    @Test
    public void edit_span_store_factory_create() {
        // when span is not present in spannable -> new one will be created via factory

        final class Two {
        }

        final SpannableStringBuilder builder = new SpannableStringBuilder();
        final Two two = new Two();
        append(builder, "two", two);

        final MarkwonEditor.EditSpanFactory factory = mock(MarkwonEditor.EditSpanFactory.class);

        final Map<Class<?>, MarkwonEditor.EditSpanFactory> map = new HashMap<Class<?>, MarkwonEditor.EditSpanFactory>() {{
            put(Two.class, factory);
        }};

        final EditSpanStoreImpl impl = new EditSpanStoreImpl(builder, map);

        // first one will be the same as we had created before,
        // second one will be created via factory

        assertEquals(two, impl.get(Two.class));

        verify(factory, never()).create();

        impl.get(Two.class);
        verify(factory, times(1)).create();
    }

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
