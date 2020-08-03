package io.noties.markwon.core;

import android.text.method.MovementMethod;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.commonmark.node.BlockQuote;
import org.commonmark.node.BulletList;
import org.commonmark.node.Code;
import org.commonmark.node.Emphasis;
import org.commonmark.node.FencedCodeBlock;
import org.commonmark.node.HardLineBreak;
import org.commonmark.node.Heading;
import org.commonmark.node.Image;
import org.commonmark.node.IndentedCodeBlock;
import org.commonmark.node.Link;
import org.commonmark.node.ListItem;
import org.commonmark.node.Node;
import org.commonmark.node.OrderedList;
import org.commonmark.node.Paragraph;
import org.commonmark.node.SoftLineBreak;
import org.commonmark.node.StrongEmphasis;
import org.commonmark.node.Text;
import org.commonmark.node.ThematicBreak;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.HashMap;
import java.util.Map;

import io.noties.markwon.MarkwonConfiguration;
import io.noties.markwon.MarkwonSpansFactory;
import io.noties.markwon.MarkwonVisitor;
import io.noties.markwon.RenderProps;
import io.noties.markwon.SpanFactory;
import io.noties.markwon.SpannableBuilder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.RETURNS_MOCKS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class CorePluginTest {

    @Test
    public void visitors_registered() {

        // only these must be registered (everything else is an error)
        // Paragraph has registered visitor but no Span by default

        //noinspection unchecked
        final Class<? extends Node>[] expected = new Class[]{
                BlockQuote.class,
                BulletList.class,
                Code.class,
                Emphasis.class,
                FencedCodeBlock.class,
                HardLineBreak.class,
                Heading.class,
                IndentedCodeBlock.class,
                Link.class,
                ListItem.class,
                OrderedList.class,
                Paragraph.class,
                SoftLineBreak.class,
                StrongEmphasis.class,
                Text.class,
                ThematicBreak.class,
                Image.class
        };

        final CorePlugin plugin = CorePlugin.create();

        final class BuilderImpl implements MarkwonVisitor.Builder {

            private final Map<Class<? extends Node>, MarkwonVisitor.NodeVisitor> map =
                    new HashMap<>();

            @NonNull
            @Override
            public <N extends Node> MarkwonVisitor.Builder on(@NonNull Class<N> node, @Nullable MarkwonVisitor.NodeVisitor<? super N> nodeVisitor) {
                if (map.put(node, nodeVisitor) != null) {
                    throw new RuntimeException("Multiple visitors registered for the node: " + node.getClass().getName());
                }
                return this;
            }

            @NonNull
            @Override
            public MarkwonVisitor.Builder blockHandler(@NonNull MarkwonVisitor.BlockHandler blockHandler) {
                throw new RuntimeException();
            }

            @NonNull
            @Override
            public MarkwonVisitor build(@NonNull MarkwonConfiguration configuration, @NonNull RenderProps renderProps) {
                throw new RuntimeException();
            }
        }
        final BuilderImpl impl = new BuilderImpl();

        plugin.configureVisitor(impl);

        for (Class<? extends Node> node : expected) {
            assertNotNull("Node visitor registered: " + node.getName(), impl.map.remove(node));
        }

        // all other nodes (that could've been registered is an error)
        assertEquals(impl.map.toString(), 0, impl.map.size());
    }

    @Test
    public void spans_registered() {

        // paragraph has visitor registered, but no span associated by default

        //noinspection unchecked
        final Class<? extends Node>[] expected = new Class[]{
                BlockQuote.class,
                Code.class,
                Emphasis.class,
                FencedCodeBlock.class,
                Heading.class,
                IndentedCodeBlock.class,
                Link.class,
                ListItem.class,
                StrongEmphasis.class,
                ThematicBreak.class
        };

        final CorePlugin plugin = CorePlugin.create();

        final class BuilderImpl implements MarkwonSpansFactory.Builder {

            private final Map<Class<? extends Node>, SpanFactory> map =
                    new HashMap<>();

            @NonNull
            @Override
            public <N extends Node> MarkwonSpansFactory.Builder setFactory(@NonNull Class<N> node, @NonNull SpanFactory factory) {
                if (map.put(node, factory) != null) {
                    throw new RuntimeException("Multiple SpanFactories registered for the node: " + node.getName());
                }
                return this;
            }

            @NonNull
            @Override
            public <N extends Node> MarkwonSpansFactory.Builder addFactory(@NonNull Class<N> node, @NonNull SpanFactory factory) {
                throw new RuntimeException();
            }

            @NonNull
            @Override
            public <N extends Node> MarkwonSpansFactory.Builder appendFactory(@NonNull Class<N> node, @NonNull SpanFactory factory) {
                throw new RuntimeException();
            }

            @NonNull
            @Override
            public <N extends Node> MarkwonSpansFactory.Builder prependFactory(@NonNull Class<N> node, @NonNull SpanFactory factory) {
                throw new RuntimeException();
            }

            @Nullable
            @Override
            public <N extends Node> SpanFactory getFactory(@NonNull Class<N> node) {
                throw new RuntimeException();
            }

            @NonNull
            @Override
            public <N extends Node> SpanFactory requireFactory(@NonNull Class<N> node) {
                throw new RuntimeException();
            }

            @NonNull
            @Override
            public MarkwonSpansFactory build() {
                throw new RuntimeException();
            }
        }
        final BuilderImpl impl = new BuilderImpl();

        plugin.configureSpansFactory(impl);

        for (Class<? extends Node> node : expected) {
            assertNotNull("SpanFactory registered: " + node.getName(), impl.map.remove(node));
        }

        assertEquals(impl.map.toString(), 0, impl.map.size());
    }

    @Test
    public void softbreak() {

        final CorePlugin plugin = CorePlugin.create();

        final MarkwonVisitor.Builder builder = mock(MarkwonVisitor.Builder.class);
        when(builder.on(any(Class.class), any(MarkwonVisitor.NodeVisitor.class))).thenReturn(builder);

        final ArgumentCaptor<MarkwonVisitor.NodeVisitor> captor =
                ArgumentCaptor.forClass(MarkwonVisitor.NodeVisitor.class);

        plugin.configureVisitor(builder);

        //noinspection unchecked
        verify(builder).on(eq(SoftLineBreak.class), captor.capture());

        //noinspection unchecked
        final MarkwonVisitor.NodeVisitor<SoftLineBreak> nodeVisitor = captor.getValue();
        final MarkwonVisitor visitor = mock(MarkwonVisitor.class);

        // we must mock SpannableBuilder and verify that it has a space character appended
        final SpannableBuilder spannableBuilder = mock(SpannableBuilder.class);
        when(visitor.builder()).thenReturn(spannableBuilder);
        nodeVisitor.visit(visitor, mock(SoftLineBreak.class));

        verify(visitor, times(1)).builder();
        verify(spannableBuilder, times(1)).append(eq(' '));
    }

    @Test
    public void implicit_movement_method_after_set_text_added() {
        // validate that CorePlugin will implicitly add LinkMovementMethod if one is missing
        final TextView textView = mock(TextView.class);
        when(textView.getMovementMethod()).thenReturn(null);

        final CorePlugin plugin = CorePlugin.create();

        assertNull(textView.getMovementMethod());

        plugin.afterSetText(textView);

        final ArgumentCaptor<MovementMethod> captor = ArgumentCaptor.forClass(MovementMethod.class);
        verify(textView, times(1)).setMovementMethod(captor.capture());

        assertNotNull(captor.getValue());
    }

    @Test
    public void implicit_movement_method_after_set_text_no_op() {
        // validate that CorePlugin won't change movement method if one is present on a TextView

        final TextView textView = mock(TextView.class);
        when(textView.getMovementMethod()).thenReturn(mock(MovementMethod.class));

        final CorePlugin plugin = CorePlugin.create();

        plugin.afterSetText(textView);

        verify(textView, times(0)).setMovementMethod(any(MovementMethod.class));
    }

    @Test
    public void explicit_movement_method() {
        final TextView textView = mock(TextView.class);
        final CorePlugin plugin = CorePlugin.create()
                .hasExplicitMovementMethod(true);
        plugin.afterSetText(textView);
        verify(textView, never()).setMovementMethod(any(MovementMethod.class));
    }

    @Test
    public void code_block_info_prop() {
        final CorePlugin plugin = CorePlugin.create();
        final MarkwonVisitor.Builder builder = mock(MarkwonVisitor.Builder.class);
        plugin.configureVisitor(builder);

        final ArgumentCaptor<MarkwonVisitor.NodeVisitor> fencedCaptor =
                ArgumentCaptor.forClass(MarkwonVisitor.NodeVisitor.class);
        final ArgumentCaptor<MarkwonVisitor.NodeVisitor> indendedCaptor =
                ArgumentCaptor.forClass(MarkwonVisitor.NodeVisitor.class);

        //noinspection unchecked
        verify(builder, times(1)).on(eq(FencedCodeBlock.class), fencedCaptor.capture());
        //noinspection unchecked
        verify(builder, times(1)).on(eq(IndentedCodeBlock.class), indendedCaptor.capture());

        final RenderProps renderProps = mock(RenderProps.class);
        final MarkwonVisitor visitor = mock(MarkwonVisitor.class, RETURNS_MOCKS);

        when(visitor.renderProps()).thenReturn(renderProps);

        // fenced
        {
            final FencedCodeBlock block = new FencedCodeBlock();
            block.setInfo("testing-fenced");
            //noinspection unchecked
            fencedCaptor.getValue().visit(visitor, block);

            verify(renderProps, times(1)).set(eq(CoreProps.CODE_BLOCK_INFO), eq("testing-fenced"));
        }

        // indended
        {
            final IndentedCodeBlock block = new IndentedCodeBlock();
            //noinspection unchecked
            indendedCaptor.getValue().visit(visitor, block);

            verify(renderProps, times(1)).set(eq(CoreProps.CODE_BLOCK_INFO), eq((String) null));
        }
    }
}