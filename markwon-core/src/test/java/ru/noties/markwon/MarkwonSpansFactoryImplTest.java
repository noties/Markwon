package ru.noties.markwon;

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

import java.util.Collections;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

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
}