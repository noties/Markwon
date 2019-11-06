package io.noties.markwon.app.edit;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;
import android.text.style.MetricAffectingSpan;
import android.text.style.UnderlineSpan;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.concurrent.Executors;

import io.noties.debug.AndroidLogDebugOutput;
import io.noties.debug.Debug;
import io.noties.markwon.Markwon;
import io.noties.markwon.app.R;
import io.noties.markwon.core.MarkwonTheme;
import io.noties.markwon.core.spans.BlockQuoteSpan;
import io.noties.markwon.core.spans.CodeBlockSpan;
import io.noties.markwon.core.spans.CodeSpan;
import io.noties.markwon.core.spans.LinkSpan;
import io.noties.markwon.core.spans.StrongEmphasisSpan;
import io.noties.markwon.editor.MarkwonEditor;
import io.noties.markwon.editor.MarkwonEditorTextWatcher;

public class EditActivity extends Activity {

    static {
        Debug.init(new AndroidLogDebugOutput(true));
    }
//
//    private static final String S = "**bold** it seems to **work** for now, new lines are cool to certain extend **yo**!\n" +
//            "\n" +
//            "> quote!\n" +
//            "> > nested quote!\n" +
//            "\n" +
//            "**bold** man, make it bold!\n" +
//            "\n" +
//            "# Head\n" +
//            "## Head\n" +
//            "\n" +
//            "man, **crazy** thing called love.... **work**, **work** **work** man, super weird,\n" +
//            "\n" +
//            "`code`, yeah and code doesn't work\n" +
//            "\n" +
//            "* one\n" +
//            "* two\n" +
//            "* three\n" +
//            "* * hey!\n" +
//            "   * super hey\n" +
//            "\n" +
//            "it does seem good **bold**, now shifted... **bold** man, now restored **bold** *em* sd\n" +
//            "\n" +
//            "[link](#) is it? ![image](./png) hey! **bold**\n" +
//            "\n";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        final EditText editText = findViewById(R.id.edit_text);

        final Markwon markwon = Markwon.create(this);
        final MarkwonTheme theme = markwon.configuration().theme();

        final MarkwonEditor editor = MarkwonEditor.builder(markwon)
                .withPunctuationSpan(MarkdownPunctuationSpan.class, MarkdownPunctuationSpan::new)
                .includeEditSpan(Bold.class, Bold::new)
                .includeEditSpan(CodeSpan.class, () -> new CodeSpan(theme))
                .includeEditSpan(UnderlineSpan.class, UnderlineSpan::new)
                .includeEditSpan(CodeBlockSpan.class, () -> new CodeBlockSpan(theme))
                .includeEditSpan(BlockQuoteSpan.class, () -> new BlockQuoteSpan(theme))
                .withEditSpanHandlerFor(StrongEmphasisSpan.class, new MarkwonEditor.EditSpanHandler<StrongEmphasisSpan>() {
                    @Override
                    public void handle(@NonNull MarkwonEditor.SpanStore store, @NonNull Editable editable, @NonNull String input, @NonNull StrongEmphasisSpan span, int spanStart, int spanTextLength) {
                        editable.setSpan(
                                store.get(Bold.class),
                                spanStart,
                                // we know that strong emphasis is delimited with 2 characters on both sides
                                spanStart + spanTextLength + 4,
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        );
                    }
                })
                .withEditSpanHandlerFor(LinkSpan.class, new MarkwonEditor.EditSpanHandler<LinkSpan>() {
                    @Override
                    public void handle(@NonNull MarkwonEditor.SpanStore store, @NonNull Editable editable, @NonNull String input, @NonNull LinkSpan span, int spanStart, int spanTextLength) {
                        editable.setSpan(
                                store.get(UnderlineSpan.class),
                                // add underline only for link text
                                spanStart + 1,
                                spanStart + 1 + spanTextLength,
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        );
                    }
                })
                .withEditSpanHandlerFor(CodeSpan.class, new MarkwonEditor.EditSpanHandler<CodeSpan>() {
                    @Override
                    public void handle(@NonNull MarkwonEditor.SpanStore store, @NonNull Editable editable, @NonNull String input, @NonNull CodeSpan span, int spanStart, int spanTextLength) {
                        editable.setSpan(
                                store.get(CodeSpan.class),
                                spanStart,
                                spanStart + spanTextLength,
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        );
                    }
                })
                .withEditSpanHandlerFor(CodeBlockSpan.class, new MarkwonEditor.EditSpanHandler<CodeBlockSpan>() {
                    @Override
                    public void handle(@NonNull MarkwonEditor.SpanStore store, @NonNull Editable editable, @NonNull String input, @NonNull CodeBlockSpan span, int spanStart, int spanTextLength) {
                        // if starts with backticks -> count them and then add everything until the line end
                        if (input.charAt(spanStart) == '`') {
                            final int firstLineEnd = input.indexOf('\n', spanStart);
                            if (firstLineEnd == -1) return;
                            int lastLineEnd = input.indexOf('\n', spanStart + (firstLineEnd - spanStart) + spanTextLength + 1);
                            if (lastLineEnd == -1) lastLineEnd = input.length();

                            editable.setSpan(
                                    store.get(CodeBlockSpan.class),
                                    spanStart,
                                    lastLineEnd,
                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                            );
                        } else {
                            // todo: just everything until the end
                            editable.setSpan(
                                    store.get(CodeBlockSpan.class),
                                    spanStart,
                                    spanStart + spanTextLength,
                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                            );
                        }
                    }
                })
                .withEditSpanHandlerFor(BlockQuoteSpan.class, new MarkwonEditor.EditSpanHandler<BlockQuoteSpan>() {
                    @Override
                    public void handle(@NonNull MarkwonEditor.SpanStore store, @NonNull Editable editable, @NonNull String input, @NonNull BlockQuoteSpan span, int spanStart, int spanTextLength) {
                        editable.setSpan(
                                store.get(BlockQuoteSpan.class),
                                spanStart,
                                spanStart + spanTextLength,
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        );
                    }
                })
                .build();

        editText.addTextChangedListener(MarkwonEditorTextWatcher.withPreRender(
                editor,
                Executors.newCachedThreadPool(),
                editText));
    }

    private static class MarkdownPunctuationSpan extends ForegroundColorSpan {
        MarkdownPunctuationSpan() {
            super(0xFFFF0000);
        }
    }

    private static class Bold extends MetricAffectingSpan {
        public Bold() {
            super();
        }

        @Override
        public void updateDrawState(TextPaint tp) {
            update(tp);
        }

        @Override
        public void updateMeasureState(@NonNull TextPaint textPaint) {
            update(textPaint);
        }

        private void update(@NonNull TextPaint paint) {
            paint.setFakeBoldText(true);
        }
    }
}
