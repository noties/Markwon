package ru.noties.markwon;

import org.commonmark.node.Node;
import org.commonmark.node.Visitor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import ix.Ix;
import ix.IxPredicate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.RETURNS_MOCKS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class MarkwonVisitorImplTest {

    @Test
    public void clear() {
        // clear method will clear renderProps and spannableBuilder

        final RenderProps renderProps = mock(RenderProps.class);
        final SpannableBuilder spannableBuilder = mock(SpannableBuilder.class);

        final MarkwonVisitorImpl impl = new MarkwonVisitorImpl(
                mock(MarkwonConfiguration.class),
                renderProps,
                spannableBuilder,
                Collections.<Class<? extends Node>, MarkwonVisitor.NodeVisitor<? extends Node>>emptyMap());

        impl.clear();

        verify(renderProps, times(1)).clearAll();
        verify(spannableBuilder, times(1)).clear();
    }

    @Test
    public void ensure_new_line() {
        // new line will be inserted if length > 0 && last character is not a new line

        final SpannableBuilder builder = new SpannableBuilder();

        final MarkwonVisitorImpl impl = new MarkwonVisitorImpl(
                mock(MarkwonConfiguration.class),
                mock(RenderProps.class),
                builder,
                Collections.<Class<? extends Node>, MarkwonVisitor.NodeVisitor<? extends Node>>emptyMap());

        // at the start - won't add anything
        impl.ensureNewLine();
        assertEquals(0, builder.length());

        // last char is new line -> won't add anything
        builder.append('\n');
        assertEquals(1, builder.length());
        impl.ensureNewLine();
        assertEquals(1, builder.length());

        // not-empty and last char is not new-line -> add new line
        builder.clear();
        assertEquals(0, builder.length());
        builder.append('a');
        assertEquals(1, builder.length());
        impl.ensureNewLine();
        assertEquals(2, builder.length());
        assertEquals('\n', builder.lastChar());
    }

    @Test
    public void force_new_line() {
        // force new line always add new-line

        final SpannableBuilder builder = new SpannableBuilder();
        final MarkwonVisitorImpl impl = new MarkwonVisitorImpl(
                mock(MarkwonConfiguration.class),
                mock(RenderProps.class),
                builder,
                Collections.<Class<? extends Node>, MarkwonVisitor.NodeVisitor<? extends Node>>emptyMap());

        assertEquals(0, builder.length());

        for (int i = 0; i < 9; i++) {
            impl.forceNewLine();
        }

        assertEquals(9, builder.length());

        // all characters are new lines
        for (int i = 0; i < builder.length(); i++) {
            assertEquals('\n', builder.charAt(i));
        }
    }

    @Test
    public void all_known_nodes_visit_methods_are_overridden() {
        // checks that all methods from Visitor (commonmark-java) interface are implemented

        final List<Method> methods = Ix.fromArray(Visitor.class.getDeclaredMethods())
                .filter(new IxPredicate<Method>() {

                    @Override
                    public boolean test(Method method) {

                        // if it's present in our impl -> remove
                        // else keep (to report)

                        try {
                            MarkwonVisitorImpl.class
                                    .getDeclaredMethod(method.getName(), method.getParameterTypes());
                            return false;
                        } catch (NoSuchMethodException e) {
                            return true;
                        }
                    }
                })
                .toList();

        assertEquals(methods.toString(), 0, methods.size());
    }

    @Test
    public void non_registered_nodes_children_visited() {
        fail();
    }

    @Test
    public void has_next() {

        final MarkwonVisitorImpl impl = new MarkwonVisitorImpl(
                mock(MarkwonConfiguration.class),
                mock(RenderProps.class),
                mock(SpannableBuilder.class),
                Collections.<Class<? extends Node>, MarkwonVisitor.NodeVisitor<? extends Node>>emptyMap());

        final Node noNext = mock(Node.class);
        assertFalse(impl.hasNext(noNext));

        final Node hasNext = mock(Node.class, RETURNS_MOCKS);
        assertTrue(impl.hasNext(hasNext));
    }

    @Test
    public void length() {
        // redirects call to SpannableBuilder (no internal caching)

        final class BuilderImpl extends SpannableBuilder {

            private int length;

            private void setLength(int length) {
                this.length = length;
            }

            @Override
            public int length() {
                return length;
            }
        }
        final BuilderImpl builder = new BuilderImpl();

        final MarkwonVisitorImpl impl = new MarkwonVisitorImpl(
                mock(MarkwonConfiguration.class),
                mock(RenderProps.class),
                builder,
                Collections.<Class<? extends Node>, MarkwonVisitor.NodeVisitor<? extends Node>>emptyMap());

        for (int i = 0; i < 13; i++) {
            builder.setLength(i);
            assertEquals(i, builder.length());
            assertEquals(builder.length(), impl.length());
        }
    }

    @Test
    public void set_spans_for_node() {
        fail();
    }

    @Test
    public void set_spans_for_node_optional() {
        fail();
    }
}