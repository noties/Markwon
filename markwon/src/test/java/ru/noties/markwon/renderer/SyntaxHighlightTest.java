package ru.noties.markwon.renderer;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;

import org.commonmark.node.FencedCodeBlock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import ru.noties.markwon.MarkwonConfiguration;
import ru.noties.markwon.SpannableBuilder;
import ru.noties.markwon.SpannableFactory;
import ru.noties.markwon.SyntaxHighlight;
import ru.noties.markwon.spans.MarkwonTheme;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = {
        Build.VERSION_CODES.JELLY_BEAN,
        Build.VERSION_CODES.M,
        Build.VERSION_CODES.O
})
public class SyntaxHighlightTest {

    // codeSpan must be before actual highlight spans (true reverse of builder)

    // if we go with path of reversing spans inside SpannableBuilder (which
    // might extend SpannableStringBuilder like https://github.com/noties/Markwon/pull/71)
    // then on M (23) codeSpan will always be _before_ actual highlight and thus
    // no highlight will be present
    // note that bad behaviour is present on M (emulator/device/robolectric)
    // other SDKs are added to validate that they do not fail
    @Test
    public void test() {

        class Highlight {
        }

        final Object codeSpan = new Object();

        final SyntaxHighlight highlight = new SyntaxHighlight() {
            @NonNull
            @Override
            public CharSequence highlight(@Nullable String info, @NonNull String code) {
                final SpannableStringBuilder builder = new SpannableStringBuilder(code);
                for (int i = 0, length = code.length(); i < length; i++) {
                    builder.setSpan(new Highlight(), i, i + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                return builder;
            }
        };

        final SpannableFactory factory = mock(SpannableFactory.class);
        when(factory.code(any(MarkwonTheme.class), anyBoolean())).thenReturn(codeSpan);

        final MarkwonConfiguration configuration = MarkwonConfiguration.builder(mock(Context.class))
                .syntaxHighlight(highlight)
                .factory(factory)
                .theme(mock(MarkwonTheme.class))
                .build();

        final SpannableBuilder builder = new SpannableBuilder();

        append(builder, "# Header 1\n", new Object());
        append(builder, "## Header 2\n", new Object());
        append(builder, "### Header 3\n", new Object());

        final int start = builder.length();

        final SpannableMarkdownVisitor visitor = new SpannableMarkdownVisitor(configuration, builder);
        final FencedCodeBlock fencedCodeBlock = new FencedCodeBlock();
        fencedCodeBlock.setLiteral("{code}");

        visitor.visit(fencedCodeBlock);

        final int end = builder.length();

        append(builder, "### Footer 3\n", new Object());
        append(builder, "## Footer 2\n", new Object());
        append(builder, "# Footer 1\n", new Object());

        final Object[] spans = builder.spannableStringBuilder().getSpans(start, end, Object.class);

        // each character + code span
        final int length = fencedCodeBlock.getLiteral().length() + 1;
        assertEquals(length, spans.length);
        assertEquals(codeSpan, spans[0]);

        for (int i = 1; i < length; i++) {
            assertTrue(spans[i] instanceof Highlight);
        }
    }

    private static void append(@NonNull SpannableBuilder builder, @NonNull String text, @NonNull Object span) {
        final int start = builder.length();
        builder.append(text);
        builder.setSpan(span, start, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
}