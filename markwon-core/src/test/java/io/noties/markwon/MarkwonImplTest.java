package io.noties.markwon;

import android.text.Spanned;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.RETURNS_MOCKS;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
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
                mock(Parser.class),
                mock(MarkwonVisitor.class),
                Collections.singletonList(plugin));

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
                parser,
                mock(MarkwonVisitor.class),
                Arrays.asList(first, second));

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

        final MarkwonVisitor visitor = mock(MarkwonVisitor.class);
        final SpannableBuilder builder = mock(SpannableBuilder.class);

        final MarkwonImpl impl = new MarkwonImpl(
                TextView.BufferType.SPANNABLE,
                mock(Parser.class),
                visitor,
                Collections.singletonList(plugin));

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

        final MarkwonVisitor visitor = mock(MarkwonVisitor.class, RETURNS_MOCKS);

        final MarkwonImpl impl = new MarkwonImpl(
                TextView.BufferType.SPANNABLE,
                mock(Parser.class),
                visitor,
                Collections.<MarkwonPlugin>emptyList());

        impl.render(mock(Node.class));

        verify(visitor, times(1)).clear();
    }

    @Test
    public void render_props() {
        // render props are configured properly and cleared after render function

        final MarkwonVisitor visitor = mock(MarkwonVisitor.class, RETURNS_MOCKS);

        final RenderProps renderProps = mock(RenderProps.class);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                renderProps.clearAll();
                return null;
            }
        }).when(visitor).clear();

        when(visitor.renderProps()).thenReturn(renderProps);

        final MarkwonPlugin plugin = mock(MarkwonPlugin.class);

        final MarkwonImpl impl = new MarkwonImpl(
                TextView.BufferType.SPANNABLE,
                mock(Parser.class),
                visitor,
                Collections.singletonList(plugin));

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

        verify(renderProps, times(1)).clearAll();
    }

    @Test
    public void set_parsed_markdown() {
        // calls `beforeSetText` on plugins
        // calls `TextView#setText(text, BUFFER_TYPE)`
        // calls `afterSetText` on plugins

        final MarkwonPlugin plugin = mock(MarkwonPlugin.class);
        final MarkwonImpl impl = new MarkwonImpl(
                TextView.BufferType.EDITABLE,
                mock(Parser.class),
                mock(MarkwonVisitor.class, RETURNS_MOCKS),
                Collections.singletonList(plugin));

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
                mock(Parser.class),
                mock(MarkwonVisitor.class),
                plugins);

        assertTrue("First", impl.hasPlugin(First.class));
        assertFalse("Second", impl.hasPlugin(Second.class));

        // can use super types. So if we ask if CorePlugin is registered,
        // but it was subclassed, we would still have true returned from this method
        assertTrue("AbstractMarkwonPlugin", impl.hasPlugin(AbstractMarkwonPlugin.class));
        assertTrue("MarkwonPlugin", impl.hasPlugin(MarkwonPlugin.class));
    }
}