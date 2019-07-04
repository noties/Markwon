package io.noties.markwon.ext.latex;

import androidx.annotation.NonNull;

import org.commonmark.parser.Parser;
import org.commonmark.parser.block.BlockParserFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.concurrent.ExecutorService;

import io.noties.markwon.MarkwonConfiguration;
import io.noties.markwon.MarkwonVisitor;
import io.noties.markwon.SpannableBuilder;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
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
}