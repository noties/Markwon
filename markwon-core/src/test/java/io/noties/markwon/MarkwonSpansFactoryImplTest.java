package io.noties.markwon;

import org.commonmark.node.Block;
import org.commonmark.node.Emphasis;
import org.commonmark.node.Image;
import org.commonmark.node.Link;
import org.commonmark.node.ListItem;
import org.commonmark.node.Node;
import org.commonmark.node.Paragraph;
import org.commonmark.node.Text;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class MarkwonSpansFactoryImplTest {

    @Test
    public void get_class() {

        // register one TextNode
        final MarkwonSpansFactoryImpl impl = new MarkwonSpansFactoryImpl(
                Collections.<Class<? extends Node>, SpanFactory>singletonMap(Text.class, mock(SpanFactory.class)));

        // text must be present
        assertNotNull(impl.get(Text.class));

        // we haven't registered ListItem, so null here
        assertNull(impl.get(ListItem.class));
    }

    @Test
    public void require_class() {

        // register one TextNode
        final MarkwonSpansFactoryImpl impl = new MarkwonSpansFactoryImpl(
                Collections.<Class<? extends Node>, SpanFactory>singletonMap(Text.class, mock(SpanFactory.class)));

        // text must be present
        assertNotNull(impl.require(Text.class));

        // we haven't registered ListItem, so null here
        try {
            impl.require(ListItem.class);
            fail();
        } catch (NullPointerException e) {
            assertTrue(true);
        }
    }

    @Test
    public void builder() {
        // all passed to builder will be in factory

        final SpanFactory text = mock(SpanFactory.class);
        final SpanFactory link = mock(SpanFactory.class);

        final MarkwonSpansFactory factory = new MarkwonSpansFactoryImpl.BuilderImpl()
                .setFactory(Text.class, text)
                .setFactory(Link.class, link)
                .build();

        assertNotNull(factory.get(Text.class));

        assertNotNull(factory.get(Link.class));

        // a bunch of non-present factories
        //noinspection unchecked
        final Class<? extends Node>[] types = new Class[]{
                Image.class,
                Block.class,
                Emphasis.class,
                Paragraph.class
        };

        for (Class<? extends Node> type : types) {
            assertNull(factory.get(type));
        }
    }

    @Test
    public void composite_span_factory() {
        // validate that composite span factory returns (calls) all span-factories

        final SpanFactory first = mock(SpanFactory.class);
        final SpanFactory second = mock(SpanFactory.class);

        final MarkwonSpansFactoryImpl.CompositeSpanFactory factory =
                new MarkwonSpansFactoryImpl.CompositeSpanFactory(first, second);

        final Object spans = factory.getSpans(mock(MarkwonConfiguration.class), mock(RenderProps.class));
        assertNotNull(spans);
        assertTrue(spans.getClass().isArray());
        assertEquals(2, ((Object[]) spans).length);

        verify(first, times(1)).getSpans(any(MarkwonConfiguration.class), any(RenderProps.class));
        verify(second, times(1)).getSpans(any(MarkwonConfiguration.class), any(RenderProps.class));
    }

    @Test
    @Deprecated
    public void builder_add_factory() {
        // here is what we should validate:
        // * if we call addFactory and there is none already -> supplied factory
        // * if there is
        // * * if not composite -> make composite
        // * * if composite -> add to it

        final MarkwonSpansFactoryImpl.BuilderImpl builder = new MarkwonSpansFactoryImpl.BuilderImpl();

        final SpanFactory first = mock(SpanFactory.class);
        final SpanFactory second = mock(SpanFactory.class);
        final SpanFactory third = mock(SpanFactory.class);

        final Class<Node> node = Node.class;

        // assert none yet
        assertNull(builder.getFactory(node));

        // add first, none yet -> it should be added without modifications
        builder.addFactory(node, first);
        assertEquals(first, builder.getFactory(node));

        // add second -> composite factory will be created
        builder.addFactory(node, second);
        final MarkwonSpansFactoryImpl.CompositeSpanFactory compositeSpanFactory =
                (MarkwonSpansFactoryImpl.CompositeSpanFactory) builder.getFactory(node);
        assertNotNull(compositeSpanFactory);
        assertEquals(Arrays.asList(first, second), compositeSpanFactory.factories);

        builder.addFactory(node, third);
        assertEquals(compositeSpanFactory, builder.getFactory(node));
        assertEquals(Arrays.asList(first, second, third), compositeSpanFactory.factories);
    }

    @Test
    public void builder_prepend_factory() {
        // here is what we should validate:
        // * if we call prependFactory and there is none already -> supplied factory
        // * if there is
        // * * if not composite -> make composite
        // * * if composite -> add to it

        final MarkwonSpansFactoryImpl.BuilderImpl builder = new MarkwonSpansFactoryImpl.BuilderImpl();

        final SpanFactory first = mock(SpanFactory.class);
        final SpanFactory second = mock(SpanFactory.class);
        final SpanFactory third = mock(SpanFactory.class);

        final Class<Node> node = Node.class;

        // assert none yet
        assertNull(builder.getFactory(node));

        // add first, none yet -> it should be added without modifications
        builder.prependFactory(node, first);
        assertEquals(first, builder.getFactory(node));

        // add second -> composite factory will be created
        builder.prependFactory(node, second);
        final MarkwonSpansFactoryImpl.CompositeSpanFactory compositeSpanFactory =
                (MarkwonSpansFactoryImpl.CompositeSpanFactory) builder.getFactory(node);
        assertNotNull(compositeSpanFactory);
        assertEquals(Arrays.asList(first, second), compositeSpanFactory.factories);

        builder.prependFactory(node, third);
        assertEquals(compositeSpanFactory, builder.getFactory(node));
        assertEquals(Arrays.asList(first, second, third), compositeSpanFactory.factories);
    }

    @Test
    public void builder_append_factory() {
        // here is what we should validate:
        // * if we call appendFactory and there is none already -> supplied factory
        // * if there is
        // * * if not composite -> make composite
        // * * if composite -> add to it

        final MarkwonSpansFactoryImpl.BuilderImpl builder = new MarkwonSpansFactoryImpl.BuilderImpl();

        final SpanFactory first = mock(SpanFactory.class);
        final SpanFactory second = mock(SpanFactory.class);
        final SpanFactory third = mock(SpanFactory.class);

        final Class<Node> node = Node.class;

        // assert none yet
        assertNull(builder.getFactory(node));

        // add first, none yet -> it should be added without modifications
        builder.appendFactory(node, first);
        assertEquals(first, builder.getFactory(node));

        // add second -> composite factory will be created
        builder.appendFactory(node, second);
        final MarkwonSpansFactoryImpl.CompositeSpanFactory compositeSpanFactory =
                (MarkwonSpansFactoryImpl.CompositeSpanFactory) builder.getFactory(node);
        assertNotNull(compositeSpanFactory);
        assertEquals(Arrays.asList(second, first), compositeSpanFactory.factories);

        builder.appendFactory(node, third);
        assertEquals(compositeSpanFactory, builder.getFactory(node));
        assertEquals(Arrays.asList(third, second, first), compositeSpanFactory.factories);
    }
}