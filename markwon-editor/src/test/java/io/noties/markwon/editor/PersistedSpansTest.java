package io.noties.markwon.editor;

import android.text.SpannableStringBuilder;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import io.noties.markwon.editor.PersistedSpans.Impl;
import io.noties.markwon.editor.PersistedSpans.SpanFactory;

import static io.noties.markwon.editor.SpannableUtils.append;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class PersistedSpansTest {

    @Test
    public void not_included() {
        // When a span that is not included is requested -> exception is raised

        final Map<Class<?>, SpanFactory> map = Collections.emptyMap();

        final Impl impl = new Impl(new SpannableStringBuilder(), map);

        try {
            impl.get(Object.class);
            fail();
        } catch (IllegalStateException e) {
            assertTrue(e.getMessage(), e.getMessage().contains("not registered, use PersistedSpans.Builder#persistSpan method to register"));
        }
    }

    @Test
    public void re_use() {
        // when a span is present in supplied spannable -> it will be used

        final class One {
        }

        final SpannableStringBuilder builder = new SpannableStringBuilder();
        final One one = new One();
        append(builder, "One", one);

        final Map<Class<?>, SpanFactory> map = new HashMap<Class<?>, SpanFactory>() {{
            // null in case it _will_ be used -> thus NPE
            put(One.class, null);
        }};

        final Impl impl = new Impl(builder, map);

        assertEquals(one, impl.get(One.class));
    }

    @Test
    public void factory_create() {
        // when span is not present in spannable -> new one will be created via factory

        final class Two {
        }

        final SpannableStringBuilder builder = new SpannableStringBuilder();
        final Two two = new Two();
        append(builder, "two", two);

        final SpanFactory factory = mock(SpanFactory.class);

        final Map<Class<?>, SpanFactory> map = new HashMap<Class<?>, SpanFactory>() {{
            put(Two.class, factory);
        }};

        final Impl impl = new Impl(builder, map);

        // first one will be the same as we had created before,
        // second one will be created via factory

        assertEquals(two, impl.get(Two.class));

        verify(factory, never()).create();

        impl.get(Two.class);
        verify(factory, times(1)).create();
    }
}