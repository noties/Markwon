package ru.noties.markwon.renderer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;

import org.commonmark.node.FencedCodeBlock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import ru.noties.markwon.SpannableBuilder;
import ru.noties.markwon.SpannableConfiguration;
import ru.noties.markwon.SpannableFactory;
import ru.noties.markwon.SyntaxHighlight;
import ru.noties.markwon.spans.SpannableTheme;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class SyntaxHighlightTest {

    // codeSpan must be before actual highlight spans (true reverse of builder)

    @Test
    public void test() {

        final Object highlightSpan = new Object();
        final Object codeSpan = new Object();

        final SyntaxHighlight highlight = new SyntaxHighlight() {
            @NonNull
            @Override
            public CharSequence highlight(@Nullable String info, @NonNull String code) {
                final SpannableStringBuilder builder = new SpannableStringBuilder(code);
                builder.setSpan(highlightSpan, 0, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                return builder;
            }
        };

        final SpannableFactory factory = mock(SpannableFactory.class);
        when(factory.code(any(SpannableTheme.class), anyBoolean())).thenReturn(codeSpan);

        final SpannableConfiguration configuration = SpannableConfiguration.builder(mock(Context.class))
                .syntaxHighlight(highlight)
                .factory(factory)
                .theme(mock(SpannableTheme.class))
                .build();

        final SpannableBuilder builder = new SpannableBuilder();

        final SpannableMarkdownVisitor visitor = new SpannableMarkdownVisitor(configuration, builder);
        final FencedCodeBlock fencedCodeBlock = new FencedCodeBlock();
        fencedCodeBlock.setLiteral("{code}");

        visitor.visit(fencedCodeBlock);

        final Object[] spans = builder.spannableStringBuilder().getSpans(0, builder.length(), Object.class);

        assertEquals(2, spans.length);
        assertEquals(codeSpan, spans[0]);
        assertEquals(highlightSpan, spans[1]);
    }
}
