package io.noties.markwon;

import android.text.Spanned;
import android.text.TextUtils;
import android.widget.TextView;

import org.commonmark.node.Node;
import org.commonmark.node.Visitor;
import org.commonmark.parser.Parser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.RETURNS_MOCKS;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class MarkwonImplTest {

    @Test
    public void parse_calls_plugin_process_markdown() {

        final MarkwonPlugin plugin = mock(MarkwonPlugin.class);
        final MarkwonImpl impl = new MarkwonImpl(
                TextView.BufferType.SPANNABLE,
                null,
                mock(Parser.class),
                mock(MarkwonVisitorFactory.class),
                mock(MarkwonConfiguration.class),
                Collections.singletonList(plugin),
                true
        );

        impl.parse("whatever");

        verify(plugin, times(1)).processMarkdown(eq("whatever"));
    }

    @Test
    public void parse_markwon_processed() {

        final Parser parser = mock(Parser.class);

        final MarkwonPlugin first = mock(MarkwonPlugin.class);
        final MarkwonPlugin second = mock(MarkwonPlugin.class);

        when(first.processMarkdown(anyString())).thenReturn("first");
        when(second.processMarkdown(anyString())).thenReturn("second");

        final MarkwonImpl impl = new MarkwonImpl(
                TextView.BufferType.SPANNABLE,
                null,
                parser,
                mock(MarkwonVisitorFactory.class),
                mock(MarkwonConfiguration.class),
                Arrays.asList(first, second),
                true
        );

        impl.parse("zero");

        verify(first, times(1)).processMarkdown(eq("zero"));
        verify(second, times(1)).processMarkdown(eq("first"));

        // verify parser has `second` as input
        verify(parser, times(1)).parse(eq("second"));
    }

    @Test
    public void render_calls_plugins() {
        // before parsing each plugin is called `configureRenderProps` and `beforeRender`
        // after parsing each plugin is called `afterRender`

        final MarkwonPlugin plugin = mock(MarkwonPlugin.class);

        final MarkwonVisitorFactory visitorFactory = mock(MarkwonVisitorFactory.class);
        final MarkwonVisitor visitor = mock(MarkwonVisitor.class);
        final SpannableBuilder builder = mock(SpannableBuilder.class);

        final MarkwonImpl impl = new MarkwonImpl(
                TextView.BufferType.SPANNABLE,
                null,
                mock(Parser.class),
                visitorFactory,
                mock(MarkwonConfiguration.class),
                Collections.singletonList(plugin),
                true
        );

        when(visitorFactory.create()).thenReturn(visitor);
        when(visitor.builder()).thenReturn(builder);

        final Node node = mock(Node.class);

        final AtomicBoolean flag = new AtomicBoolean(false);

        // we will validate _before_ part here
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {

                // mark this flag (we must ensure that this method body is executed)
                flag.set(true);

                verify(plugin, times(1)).beforeRender(eq(node));
                verify(plugin, times(0)).afterRender(any(Node.class), any(MarkwonVisitor.class));

                return null;
            }
        }).when(node).accept(any(Visitor.class));

        impl.render(node);

        // validate that Answer was called (it has assertions about _before_ part
        assertTrue(flag.get());

        verify(plugin, times(1)).afterRender(eq(node), eq(visitor));
    }

    @Test
    public void render_clears_visitor() {
        // each render call should have empty-state visitor (no previous rendering info)

        final MarkwonVisitorFactory visitorFactory = mock(MarkwonVisitorFactory.class);
        final MarkwonVisitor visitor = mock(MarkwonVisitor.class, RETURNS_MOCKS);

        when(visitorFactory.create()).thenReturn(visitor);

        final MarkwonImpl impl = new MarkwonImpl(
                TextView.BufferType.SPANNABLE,
                null,
                mock(Parser.class),
                visitorFactory,
                mock(MarkwonConfiguration.class),
                Collections.<MarkwonPlugin>emptyList(),
                true
        );

        impl.render(mock(Node.class));

        // obsolete starting with 4.1.1
//        verify(visitor, times(1)).clear();
        verify(visitor, never()).clear();
    }

    @Test
    public void render_props() {
        // render props are configured properly and cleared after render function

        final MarkwonVisitorFactory visitorFactory = mock(MarkwonVisitorFactory.class);
        final MarkwonVisitor visitor = mock(MarkwonVisitor.class, RETURNS_MOCKS);

        final RenderProps renderProps = mock(RenderProps.class);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                renderProps.clearAll();
                return null;
            }
        }).when(visitor).clear();

        when(visitorFactory.create()).thenReturn(visitor);
        when(visitor.renderProps()).thenReturn(renderProps);

        final MarkwonPlugin plugin = mock(MarkwonPlugin.class);

        final MarkwonImpl impl = new MarkwonImpl(
                TextView.BufferType.SPANNABLE,
                null,
                mock(Parser.class),
                visitorFactory,
                mock(MarkwonConfiguration.class),
                Collections.singletonList(plugin),
                true
        );

        final AtomicBoolean flag = new AtomicBoolean(false);
        final Node node = mock(Node.class);

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {

                flag.set(true);

                verify(renderProps, times(0)).clearAll();

                return null;
            }
        }).when(node).accept(any(Visitor.class));

        impl.render(node);

        assertTrue(flag.get());

        // obsolete starting with 4.1.1
//        verify(renderProps, times(1)).clearAll();
        verify(renderProps, never()).clearAll();
    }

    @Test
    public void set_parsed_markdown() {
        // calls `beforeSetText` on plugins
        // calls `TextView#setText(text, BUFFER_TYPE)`
        // calls `afterSetText` on plugins

        final MarkwonPlugin plugin = mock(MarkwonPlugin.class);
        final MarkwonImpl impl = new MarkwonImpl(
                TextView.BufferType.EDITABLE,
                null,
                mock(Parser.class),
                mock(MarkwonVisitorFactory.class, RETURNS_MOCKS),
                mock(MarkwonConfiguration.class),
                Collections.singletonList(plugin),
                true
        );

        final TextView textView = mock(TextView.class);
        final AtomicBoolean flag = new AtomicBoolean(false);

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {

                flag.set(true);

                verify(plugin, times(1)).beforeSetText(eq(textView), any(Spanned.class));
                verify(plugin, times(0)).afterSetText(any(TextView.class));

                final ArgumentCaptor<TextView.BufferType> captor =
                        ArgumentCaptor.forClass(TextView.BufferType.class);

                verify(textView).setText(any(CharSequence.class), captor.capture());
                assertEquals(TextView.BufferType.EDITABLE, captor.getValue());

                return null;
            }
        }).when(textView).setText(any(CharSequence.class), any(TextView.BufferType.class));

        impl.setParsedMarkdown(textView, mock(Spanned.class));

        assertTrue(flag.get());

        verify(plugin, times(1)).afterSetText(eq(textView));
    }

    @Test
    public void has_plugin() {

        final class First extends AbstractMarkwonPlugin {
        }

        final class Second extends AbstractMarkwonPlugin {
        }

        final List<MarkwonPlugin> plugins = Collections.singletonList((MarkwonPlugin) new First());

        final MarkwonImpl impl = new MarkwonImpl(
                TextView.BufferType.SPANNABLE,
                null,
                mock(Parser.class),
                mock(MarkwonVisitorFactory.class),
                mock(MarkwonConfiguration.class),
                plugins,
                true
        );

        assertTrue("First", impl.hasPlugin(First.class));
        assertFalse("Second", impl.hasPlugin(Second.class));

        // can use super types. So if we ask if CorePlugin is registered,
        // but it was subclassed, we would still have true returned from this method
        assertTrue("AbstractMarkwonPlugin", impl.hasPlugin(AbstractMarkwonPlugin.class));
        assertTrue("MarkwonPlugin", impl.hasPlugin(MarkwonPlugin.class));
    }

    @Test
    public void text_setter() {

        final Markwon.TextSetter textSetter = mock(Markwon.TextSetter.class);
        final MarkwonPlugin plugin = mock(MarkwonPlugin.class);

        final MarkwonImpl impl = new MarkwonImpl(
                TextView.BufferType.EDITABLE,
                textSetter,
                mock(Parser.class),
                mock(MarkwonVisitorFactory.class),
                mock(MarkwonConfiguration.class),
                Collections.singletonList(plugin),
                true
        );

        final TextView textView = mock(TextView.class);
        final Spanned spanned = mock(Spanned.class);

        impl.setParsedMarkdown(textView, spanned);

        final ArgumentCaptor<TextView> textViewArgumentCaptor =
                ArgumentCaptor.forClass(TextView.class);
        final ArgumentCaptor<Spanned> spannedArgumentCaptor =
                ArgumentCaptor.forClass(Spanned.class);
        final ArgumentCaptor<TextView.BufferType> bufferTypeArgumentCaptor =
                ArgumentCaptor.forClass(TextView.BufferType.class);
        final ArgumentCaptor<Runnable> runnableArgumentCaptor =
                ArgumentCaptor.forClass(Runnable.class);

        verify(textSetter, times(1)).setText(
                textViewArgumentCaptor.capture(),
                spannedArgumentCaptor.capture(),
                bufferTypeArgumentCaptor.capture(),
                runnableArgumentCaptor.capture());

        assertEquals(textView, textViewArgumentCaptor.getValue());
        assertEquals(spanned, spannedArgumentCaptor.getValue());
        assertEquals(TextView.BufferType.EDITABLE, bufferTypeArgumentCaptor.getValue());
        assertNotNull(runnableArgumentCaptor.getValue());
    }

    @Test
    public void require_plugin_throws() {
        // if plugin is `required`, but it's not added -> an exception is thrown

        final class NotPresent extends AbstractMarkwonPlugin {
        }

        final List<MarkwonPlugin> plugins =
                Arrays.asList(mock(MarkwonPlugin.class), mock(MarkwonPlugin.class));

        final MarkwonImpl impl = new MarkwonImpl(
                TextView.BufferType.SPANNABLE,
                null,
                mock(Parser.class),
                mock(MarkwonVisitorFactory.class),
                mock(MarkwonConfiguration.class),
                plugins,
                true
        );

        // should be returned
        assertNotNull(impl.requirePlugin(MarkwonPlugin.class));

        try {
            impl.requirePlugin(NotPresent.class);
            fail();
        } catch (Throwable t) {
            assertTrue(t.getMessage(), t.getMessage().contains(NotPresent.class.getName()));
        }
    }

    @Test
    public void plugins_unmodifiable() {
        // returned plugins list must not be modifiable

        // modifiable list (created from Arrays.asList -> which returns non)
        final List<MarkwonPlugin> plugins = new ArrayList<>(
                Arrays.asList(mock(MarkwonPlugin.class), mock(MarkwonPlugin.class)));

        // validate that list is modifiable
        plugins.add(mock(MarkwonPlugin.class));
        assertEquals(3, plugins.size());

        final MarkwonImpl impl = new MarkwonImpl(
                TextView.BufferType.SPANNABLE,
                null,
                mock(Parser.class),
                mock(MarkwonVisitorFactory.class),
                mock(MarkwonConfiguration.class),
                plugins,
                true
        );

        final List<? extends MarkwonPlugin> list = impl.getPlugins();

        // instance check (different list)
        //noinspection SimplifiableJUnitAssertion
        assertTrue(plugins != list);

        try {
            list.add(null);
            fail();
        } catch (UnsupportedOperationException e) {
            assertTrue(e.getMessage(), true);
        }
    }

    @Test
    public void fallback_to_raw() {
        final String md = "*";

        final MarkwonImpl impl = new MarkwonImpl(
                TextView.BufferType.SPANNABLE,
                null,
                mock(Parser.class, RETURNS_MOCKS),
                // it must be sufficient to just return mocks and thus empty rendering result
                mock(MarkwonVisitorFactory.class, RETURNS_MOCKS),
                mock(MarkwonConfiguration.class),
                Collections.<MarkwonPlugin>emptyList(),
                true
        );

        final Spanned spanned = impl.toMarkdown(md);
        assertEquals(md, spanned.toString());
    }

    @Test
    public void fallback_to_raw_false() {
        final String md = "*";

        final MarkwonImpl impl = new MarkwonImpl(
                TextView.BufferType.SPANNABLE,
                null,
                mock(Parser.class, RETURNS_MOCKS),
                // it must be sufficient to just return mocks and thus empty rendering result
                mock(MarkwonVisitorFactory.class, RETURNS_MOCKS),
                mock(MarkwonConfiguration.class),
                Collections.<MarkwonPlugin>emptyList(),
                false
        );

        final Spanned spanned = impl.toMarkdown(md);
        assertTrue(spanned.toString(), TextUtils.isEmpty(spanned));
    }
}