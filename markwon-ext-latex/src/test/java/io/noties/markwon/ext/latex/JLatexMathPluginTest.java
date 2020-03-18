package io.noties.markwon.ext.latex;

import androidx.annotation.NonNull;

import org.commonmark.parser.Parser;
import org.commonmark.parser.block.BlockParserFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;
import java.util.concurrent.ExecutorService;

import io.noties.markwon.MarkwonConfiguration;
import io.noties.markwon.MarkwonPlugin;
import io.noties.markwon.MarkwonVisitor;
import io.noties.markwon.SpannableBuilder;
import io.noties.markwon.inlineparser.InlineProcessor;
import io.noties.markwon.inlineparser.MarkwonInlineParser;
import io.noties.markwon.inlineparser.MarkwonInlineParserPlugin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class JLatexMathPluginTest {

    @Test
    public void latex_text_placeholder() {
        // text placeholder cannot have new-line characters and should be trimmed from ends

        final String[] in = {
                "hello",
                "he\nllo",
                " hello\n\n",
                "\n\nhello\n\n",
                "\n",
                " \nhello\n "
        };

        for (String latex : in) {
            final String placeholder = JLatexMathPlugin.prepareLatexTextPlaceholder(latex);
            assertTrue(placeholder, placeholder.indexOf('\n') < 0);
            if (placeholder.length() > 0) {
                assertFalse(placeholder, Character.isWhitespace(placeholder.charAt(0)));
                assertFalse(placeholder, Character.isWhitespace(placeholder.charAt(placeholder.length() - 1)));
            }
        }
    }

    @Test
    public void block_parser_registered() {
        final JLatexMathPlugin plugin = JLatexMathPlugin.create(0);
        final Parser.Builder builder = mock(Parser.Builder.class);
        plugin.configureParser(builder);
        verify(builder, times(1)).customBlockParserFactory(any(BlockParserFactory.class));
    }

    @Test
    public void visitor_registered() {
        final JLatexMathPlugin plugin = JLatexMathPlugin.create(0);
        final MarkwonVisitor.Builder builder = mock(MarkwonVisitor.Builder.class);
        plugin.configureVisitor(builder);
        //noinspection unchecked
        verify(builder, times(1))
                .on(eq(JLatexMathBlock.class), any(MarkwonVisitor.NodeVisitor.class));
    }

    @Test
    public void visit() {
        final JLatexMathPlugin plugin = JLatexMathPlugin.create(0, new JLatexMathPlugin.BuilderConfigure() {
            @Override
            public void configureBuilder(@NonNull JLatexMathPlugin.Builder builder) {
                // no async in test (nooped for this test)
                builder.executorService(mock(ExecutorService.class));
            }
        });
        final MarkwonVisitor.Builder builder = mock(MarkwonVisitor.Builder.class);
        final ArgumentCaptor<MarkwonVisitor.NodeVisitor> captor =
                ArgumentCaptor.forClass(MarkwonVisitor.NodeVisitor.class);
        plugin.configureVisitor(builder);
        //noinspection unchecked
        verify(builder, times(1))
                .on(eq(JLatexMathBlock.class), captor.capture());
        final MarkwonVisitor.NodeVisitor nodeVisitor = captor.getValue();

        final MarkwonVisitor visitor = mock(MarkwonVisitor.class);
        final JLatexMathBlock block = mock(JLatexMathBlock.class);
        when(block.latex()).thenReturn(" first\nsecond\n ");

        final SpannableBuilder spannableBuilder = mock(SpannableBuilder.class);
        when(visitor.builder()).thenReturn(spannableBuilder);
        when(visitor.configuration()).thenReturn(mock(MarkwonConfiguration.class));

        //noinspection unchecked
        nodeVisitor.visit(visitor, block);

        verify(block, times(1)).latex();
        verify(visitor, times(1)).length();

        final ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(spannableBuilder, times(1)).append(stringArgumentCaptor.capture());

        final String placeholder = stringArgumentCaptor.getValue();
        assertTrue(placeholder, placeholder.indexOf('\n') < 0);

        verify(visitor, times(1)).setSpans(eq(0), any());
    }

    @Test
    public void legacy() {
        // if render mode is legacy:
        //  - no inline plugin is required,
        //  - parser has legacy block parser factory
        //  - no inline node is registered (node)

        final JLatexMathPlugin plugin = JLatexMathPlugin.create(1, new JLatexMathPlugin.BuilderConfigure() {
            @Override
            public void configureBuilder(@NonNull JLatexMathPlugin.Builder builder) {
                builder.blocksLegacy(true);
                builder.inlinesEnabled(false);
            }
        });

        // registry
        {
            final MarkwonPlugin.Registry registry = mock(MarkwonPlugin.Registry.class);
            plugin.configure(registry);
            verify(registry, never()).require(any(Class.class));
        }

        // parser
        {
            final Parser.Builder builder = mock(Parser.Builder.class);
            plugin.configureParser(builder);

            final ArgumentCaptor<BlockParserFactory> captor =
                    ArgumentCaptor.forClass(BlockParserFactory.class);
            verify(builder, times(1)).customBlockParserFactory(captor.capture());
            final BlockParserFactory factory = captor.getValue();
            assertTrue(factory.getClass().getName(), factory instanceof JLatexMathBlockParserLegacy.Factory);
        }

        // visitor
        {
            final MarkwonVisitor.Builder builder = mock(MarkwonVisitor.Builder.class);
            plugin.configureVisitor(builder);

            final ArgumentCaptor<Class> captor = ArgumentCaptor.forClass(Class.class);
            verify(builder, times(1)).on(captor.capture(), any(MarkwonVisitor.NodeVisitor.class));

            assertEquals(JLatexMathBlock.class, captor.getValue());
        }
    }

    @Test
    public void blocks_inlines_implicit() {
        final JLatexMathPlugin plugin = JLatexMathPlugin.create(1);
        final JLatexMathPlugin.Config config = plugin.config;
        assertTrue("blocksEnabled", config.blocksEnabled);
        assertFalse("blocksLegacy", config.blocksLegacy);
        assertFalse("inlinesEnabled", config.inlinesEnabled);
    }

    @Test
    public void blocks_inlines() {
        final JLatexMathPlugin plugin = JLatexMathPlugin.create(12, new JLatexMathPlugin.BuilderConfigure() {
            @Override
            public void configureBuilder(@NonNull JLatexMathPlugin.Builder builder) {
                builder.inlinesEnabled(true);
            }
        });

        // registry
        {
            final MarkwonInlineParser.FactoryBuilder factoryBuilder = mock(MarkwonInlineParser.FactoryBuilder.class);
            final MarkwonInlineParserPlugin inlineParserPlugin = mock(MarkwonInlineParserPlugin.class);
            final MarkwonPlugin.Registry registry = mock(MarkwonPlugin.Registry.class);
            when(inlineParserPlugin.factoryBuilder()).thenReturn(factoryBuilder);
            when(registry.require(eq(MarkwonInlineParserPlugin.class))).thenReturn(inlineParserPlugin);
            plugin.configure(registry);

            verify(registry, times(1)).require(eq(MarkwonInlineParserPlugin.class));
            verify(inlineParserPlugin, times(1)).factoryBuilder();

            final ArgumentCaptor<InlineProcessor> captor = ArgumentCaptor.forClass(InlineProcessor.class);
            verify(factoryBuilder, times(1)).addInlineProcessor(captor.capture());

            final InlineProcessor inlineProcessor = captor.getValue();
            assertTrue(inlineParserPlugin.getClass().getName(), inlineProcessor instanceof JLatexMathInlineProcessor);
        }

        // parser
        {
            final Parser.Builder builder = mock(Parser.Builder.class);
            plugin.configureParser(builder);

            final ArgumentCaptor<BlockParserFactory> captor =
                    ArgumentCaptor.forClass(BlockParserFactory.class);
            verify(builder, times(1)).customBlockParserFactory(captor.capture());
            final BlockParserFactory factory = captor.getValue();
            assertTrue(factory.getClass().getName(), factory instanceof JLatexMathBlockParser.Factory);
        }

        // visitor
        {
            final MarkwonVisitor.Builder builder = mock(MarkwonVisitor.Builder.class);
            plugin.configureVisitor(builder);

            final ArgumentCaptor<Class> captor = ArgumentCaptor.forClass(Class.class);
            verify(builder, times(2)).on(captor.capture(), any(MarkwonVisitor.NodeVisitor.class));

            final List<Class> nodes = captor.getAllValues();
            assertEquals(2, nodes.size());
            assertTrue(nodes.toString(), nodes.contains(JLatexMathNode.class));
            assertTrue(nodes.toString(), nodes.contains(JLatexMathBlock.class));
        }
    }
}