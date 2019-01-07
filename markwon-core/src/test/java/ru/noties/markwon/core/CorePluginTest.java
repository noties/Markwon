package ru.noties.markwon.core;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.commonmark.node.BlockQuote;
import org.commonmark.node.BulletList;
import org.commonmark.node.Code;
import org.commonmark.node.Emphasis;
import org.commonmark.node.FencedCodeBlock;
import org.commonmark.node.HardLineBreak;
import org.commonmark.node.Heading;
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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ix.Ix;
import ix.IxFunction;
import ix.IxPredicate;
import ru.noties.markwon.MarkwonConfiguration;
import ru.noties.markwon.MarkwonSpansFactory;
import ru.noties.markwon.MarkwonVisitor;
import ru.noties.markwon.RenderProps;
import ru.noties.markwon.SpanFactory;
import ru.noties.markwon.SpannableBuilder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
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
                ThematicBreak.class
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
    public void priority_none() {
        // CorePlugin returns none as priority (thus 0)

        assertEquals(0, CorePlugin.create().priority().after().size());
    }

    @Test
    public void plugin_methods() {
        // checks that only expected plugin methods are overridden

        // these represent actual methods that are present (we expect them to be present)
        final Set<String> usedMethods = new HashSet<String>() {{
            add("configureVisitor");
            add("configureSpansFactory");
            add("beforeSetText");
            add("priority");
        }};

        // we will use declaredMethods because it won't return inherited ones
        final Method[] declaredMethods = CorePlugin.class.getDeclaredMethods();
        assertNotNull(declaredMethods);
        assertTrue(declaredMethods.length > 0);

        final List<String> methods = Ix.fromArray(declaredMethods)
                .filter(new IxPredicate<Method>() {
                    @Override
                    public boolean test(Method method) {
                        // ignore private, static
                        final int modifiers = method.getModifiers();
                        return !Modifier.isStatic(modifiers)
                                && !Modifier.isPrivate(modifiers);
                    }
                })
                .map(new IxFunction<Method, String>() {
                    @Override
                    public String apply(Method method) {
                        return method.getName();
                    }
                })
                .filter(new IxPredicate<String>() {
                    @Override
                    public boolean test(String s) {
                        return !usedMethods.contains(s);
                    }
                })
                .toList();

        assertEquals(methods.toString(), 0, methods.size());
    }

    @Test
    public void softbreak_adds_new_line_default() {
        // default is false
        softbreak_adds_new_line(CorePlugin.create(), false);
    }

    @Test
    public void softbreak_adds_new_line_false() {
        // a space character will be added
        softbreak_adds_new_line(CorePlugin.create(false), false);
    }

    @Test
    public void softbreak_adds_new_line_true() {
        // a new line will be added
        softbreak_adds_new_line(CorePlugin.create(true), true);
    }

    private static void softbreak_adds_new_line(
            @NonNull CorePlugin plugin,
            boolean softBreakAddsNewLine) {

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

        if (!softBreakAddsNewLine) {

            // we must mock SpannableBuilder and verify that it has a space character appended
            final SpannableBuilder spannableBuilder = mock(SpannableBuilder.class);
            when(visitor.builder()).thenReturn(spannableBuilder);
            nodeVisitor.visit(visitor, mock(SoftLineBreak.class));

            verify(visitor, times(1)).builder();
            verify(spannableBuilder, times(1)).append(eq(' '));

        } else {

            nodeVisitor.visit(visitor, mock(SoftLineBreak.class));

            verify(visitor, times(1)).ensureNewLine();
        }
    }
}