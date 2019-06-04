package io.noties.markwon;

import org.commonmark.node.BlockQuote;
import org.commonmark.node.Image;
import org.commonmark.node.Link;
import org.commonmark.node.ListItem;
import org.commonmark.node.Text;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

public class MarkwonSpansFactoryTest {

    private MarkwonSpansFactoryImpl.BuilderImpl builder;

    @Before
    public void before() {
        builder = new MarkwonSpansFactoryImpl.BuilderImpl();
    }

    @Test
    public void builder_set() {
        final SpanFactory factory = mock(SpanFactory.class);
        builder.setFactory(Text.class, factory);
        builder.setFactory(Text.class, factory);
        assertEquals(factory, builder.build().get(Text.class));
        assertEquals(factory, builder.build().require(Text.class));
    }

    @Test
    public void builder_get_absent() {
        // nothing is present
        assertNull(builder.getFactory(Image.class));
    }

    @Test
    public void builder_get_present() {
        final SpanFactory factory = mock(SpanFactory.class);
        builder.setFactory(ListItem.class, factory);
        assertEquals(factory, builder.getFactory(ListItem.class));
        assertEquals(factory, builder.requireFactory(ListItem.class));
    }

    @Test
    public void builder_require_fail() {
        try {
            builder.requireFactory(Link.class);
            fail();
        } catch (NullPointerException e) {
            assertTrue(e.getMessage(), e.getMessage().contains(Link.class.getName()));
        }
    }

    @Test
    public void instance_require_fail() {
        try {
            builder.build().require(BlockQuote.class);
            fail();
        } catch (NullPointerException e) {
            assertTrue(e.getMessage(), e.getMessage().contains(BlockQuote.class.getName()));
        }
    }
}