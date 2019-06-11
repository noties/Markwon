package io.noties.markwon.ext.strikethrough;

import androidx.annotation.NonNull;
import android.text.style.StrikethroughSpan;

import org.commonmark.Extension;
import org.commonmark.ext.gfm.strikethrough.Strikethrough;
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension;
import org.commonmark.parser.Parser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.List;

import ix.Ix;
import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.MarkwonConfiguration;
import io.noties.markwon.MarkwonSpansFactory;
import io.noties.markwon.MarkwonVisitor;
import io.noties.markwon.RenderProps;
import io.noties.markwon.SpanFactory;
import io.noties.markwon.core.CorePlugin;
import io.noties.markwon.test.TestSpan;
import io.noties.markwon.test.TestSpanMatcher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static io.noties.markwon.test.TestSpan.document;
import static io.noties.markwon.test.TestSpan.span;
import static io.noties.markwon.test.TestSpan.text;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class StrikethroughPluginTest {

    @Test
    public void plugin_parser_extension_registered() {
        // configure parser is called with proper parser extension

        final StrikethroughPlugin plugin = StrikethroughPlugin.create();
        final Parser.Builder parserBuilder = mock(Parser.Builder.class);
        plugin.configureParser(parserBuilder);

        //noinspection unchecked
        final ArgumentCaptor<Iterable<Extension>> captor = ArgumentCaptor.forClass(Iterable.class);

        //noinspection unchecked
        verify(parserBuilder, times(1)).extensions(captor.capture());

        final List<Extension> list = Ix.from(captor.getValue()).toList();
        assertEquals(1, list.size());

        assertTrue(list.get(0) instanceof StrikethroughExtension);
    }

    @Test
    public void plugin_span_factory_registered() {
        // strikethrough has proper spanFactory registered

        final StrikethroughPlugin plugin = StrikethroughPlugin.create();
        final MarkwonSpansFactory.Builder spansFactoryBuilder = mock(MarkwonSpansFactory.Builder.class);
        plugin.configureSpansFactory(spansFactoryBuilder);

        final ArgumentCaptor<SpanFactory> captor = ArgumentCaptor.forClass(SpanFactory.class);

        verify(spansFactoryBuilder, times(1))
                .setFactory(eq(Strikethrough.class), captor.capture());

        assertTrue(captor.getValue().getSpans(mock(MarkwonConfiguration.class), mock(RenderProps.class)) instanceof StrikethroughSpan);
    }

    @Test
    public void plugin_node_visitor_registered() {
        // visit has strikethrough node visitor registered

        final StrikethroughPlugin plugin = StrikethroughPlugin.create();
        final MarkwonVisitor.Builder visitorBuilder = mock(MarkwonVisitor.Builder.class);
        plugin.configureVisitor(visitorBuilder);

        final ArgumentCaptor<MarkwonVisitor.NodeVisitor> captor =
                ArgumentCaptor.forClass(MarkwonVisitor.NodeVisitor.class);

        //noinspection unchecked
        verify(visitorBuilder, times(1)).on(eq(Strikethrough.class), captor.capture());

        assertNotNull(captor.getValue());
    }

    @Test
    public void markdown() {

        final String input = "Hello ~~strike~~ and ~~through~~";

        final Markwon markwon = Markwon.builder(RuntimeEnvironment.application)
                .usePlugin(CorePlugin.create())
                .usePlugin(StrikethroughPlugin.create())
                .usePlugin(new AbstractMarkwonPlugin() {
                    @Override
                    public void configureSpansFactory(@NonNull MarkwonSpansFactory.Builder builder) {
                        builder.setFactory(Strikethrough.class, new SpanFactory() {
                            @Override
                            public Object getSpans(@NonNull MarkwonConfiguration configuration, @NonNull RenderProps props) {
                                return span("strikethrough");
                            }
                        });
                    }
                })
                .build();

        final TestSpan.Document document = document(
                text("Hello "),
                span("strikethrough", text("strike")),
                text(" and "),
                span("strikethrough", text("through"))
        );

        TestSpanMatcher.matches(markwon.toMarkdown(input), document);
    }
}