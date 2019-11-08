package io.noties.markwon.sample.editor;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.MetricAffectingSpan;
import android.text.style.StrikethroughSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import io.noties.markwon.Markwon;
import io.noties.markwon.core.MarkwonTheme;
import io.noties.markwon.core.spans.BlockQuoteSpan;
import io.noties.markwon.core.spans.CodeSpan;
import io.noties.markwon.core.spans.EmphasisSpan;
import io.noties.markwon.core.spans.LinkSpan;
import io.noties.markwon.core.spans.StrongEmphasisSpan;
import io.noties.markwon.editor.EditSpanHandlerBuilder;
import io.noties.markwon.editor.MarkwonEditor;
import io.noties.markwon.editor.MarkwonEditorTextWatcher;
import io.noties.markwon.editor.MarkwonEditorUtils;
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin;
import io.noties.markwon.linkify.LinkifyPlugin;
import io.noties.markwon.sample.R;

public class EditorActivity extends Activity {

    private EditText editText;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        this.editText = findViewById(R.id.edit_text);
        initBottomBar();

//        simple_process();

//        simple_pre_render();

//        custom_punctuation_span();

//        additional_edit_span();

//        additional_plugins();

        multiple_edit_spans();
    }

    private void simple_process() {
        // Process highlight in-place (right after text has changed)

        // obtain Markwon instance
        final Markwon markwon = Markwon.create(this);

        // create editor
        final MarkwonEditor editor = MarkwonEditor.create(markwon);

        // set edit listener
        editText.addTextChangedListener(MarkwonEditorTextWatcher.withProcess(editor));
    }

    private void simple_pre_render() {
        // Process highlight in background thread

        final Markwon markwon = Markwon.create(this);
        final MarkwonEditor editor = MarkwonEditor.create(markwon);

        editText.addTextChangedListener(MarkwonEditorTextWatcher.withPreRender(
                editor,
                Executors.newCachedThreadPool(),
                editText));
    }

    private void custom_punctuation_span() {
        // Use own punctuation span

        final MarkwonEditor editor = MarkwonEditor.builder(Markwon.create(this))
                .withPunctuationSpan(CustomPunctuationSpan.class, CustomPunctuationSpan::new)
                .build();

        editText.addTextChangedListener(MarkwonEditorTextWatcher.withProcess(editor));
    }

    private void additional_edit_span() {
        // An additional span is used to highlight strong-emphasis

        final MarkwonEditor editor = MarkwonEditor.builder(Markwon.create(this))
                // This is required for edit-span cache
                // We could use Markwon `StrongEmphasisSpan` here, but I use a different
                //  one to indicate that those are completely unrelated spans and must be
                //  treated differently.
                .includeEditSpan(Bold.class, Bold::new)
                .withEditSpanHandler(new MarkwonEditor.EditSpanHandler() {
                    @Override
                    public void handle(
                            @NonNull MarkwonEditor.EditSpanStore store,
                            @NonNull Editable editable,
                            @NonNull String input,
                            @NonNull Object span,
                            int spanStart,
                            int spanTextLength) {
                        if (span instanceof StrongEmphasisSpan) {
                            editable.setSpan(
                                    // `includeEditSpan(Bold.class, Bold::new)` ensured that we have
                                    //      a span here to use (either reuse existing or create a new one)
                                    store.get(Bold.class),
                                    spanStart,
                                    // we know that strong emphasis is delimited with 2 characters on both sides
                                    spanStart + spanTextLength + 4,
                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                            );
                        }
                    }
                })
                .build();

        editText.addTextChangedListener(MarkwonEditorTextWatcher.withProcess(editor));
    }

    private void additional_plugins() {
        // As highlight works based on text-diff, everything that is present in input,
        // but missing in resulting markdown is considered to be punctuation, this is why
        // additional plugins do not need special handling

        final Markwon markwon = Markwon.builder(this)
                .usePlugin(StrikethroughPlugin.create())
                .build();

        final MarkwonEditor editor = MarkwonEditor.create(markwon);

        editText.addTextChangedListener(MarkwonEditorTextWatcher.withProcess(editor));
    }

    private void multiple_edit_spans() {

        // for links to be clickable
        editText.setMovementMethod(LinkMovementMethod.getInstance());

        final Markwon markwon = Markwon.builder(this)
                .usePlugin(StrikethroughPlugin.create())
                .usePlugin(LinkifyPlugin.create())
                .build();

        final MarkwonTheme theme = markwon.configuration().theme();

        final EditLinkSpan.OnClick onClick = (widget, link) -> markwon.configuration().linkResolver().resolve(widget, link);

        final MarkwonEditor editor = MarkwonEditor.builder(markwon)
                .includeEditSpan(StrongEmphasisSpan.class, StrongEmphasisSpan::new)
                .includeEditSpan(EmphasisSpan.class, EmphasisSpan::new)
                .includeEditSpan(StrikethroughSpan.class, StrikethroughSpan::new)
                .includeEditSpan(CodeSpan.class, () -> new CodeSpan(theme))
                .includeEditSpan(BlockQuoteSpan.class, () -> new BlockQuoteSpan(theme))
                .includeEditSpan(EditLinkSpan.class, () -> new EditLinkSpan(onClick))
                .withEditSpanHandler(createEditSpanHandler())
                .build();

//        editText.addTextChangedListener(MarkwonEditorTextWatcher.withProcess(editor));
        editText.addTextChangedListener(MarkwonEditorTextWatcher.withPreRender(
                editor, Executors.newSingleThreadExecutor(), editText));
    }

    private static MarkwonEditor.EditSpanHandler createEditSpanHandler() {
        // Please note that here we specify spans THAT ARE USED IN MARKDOWN
        return EditSpanHandlerBuilder.create()
                .handleMarkdownSpan(StrongEmphasisSpan.class, (store, editable, input, span, spanStart, spanTextLength) -> {
                    // inline spans can delimit other inline spans,
                    //  for example: `**_~~hey~~_**`, this is why we must additionally find delimiter used
                    //  and its actual start/end positions
                    final MarkwonEditorUtils.Match match =
                            MarkwonEditorUtils.findDelimited(input, spanStart, "**", "__");
                    if (match != null) {
                        editable.setSpan(
                                store.get(StrongEmphasisSpan.class),
                                match.start(),
                                match.end(),
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        );
                    }
                })
                .handleMarkdownSpan(EmphasisSpan.class, (store, editable, input, span, spanStart, spanTextLength) -> {
                    final MarkwonEditorUtils.Match match =
                            MarkwonEditorUtils.findDelimited(input, spanStart, "*", "_");
                    if (match != null) {
                        editable.setSpan(
                                store.get(EmphasisSpan.class),
                                match.start(),
                                match.end(),
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        );
                    }
                })
                .handleMarkdownSpan(StrikethroughSpan.class, (store, editable, input, span, spanStart, spanTextLength) -> {
                    final MarkwonEditorUtils.Match match =
                            MarkwonEditorUtils.findDelimited(input, spanStart, "~~");
                    if (match != null) {
                        editable.setSpan(
                                store.get(StrikethroughSpan.class),
                                match.start(),
                                match.end(),
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        );
                    }
                })
                .handleMarkdownSpan(CodeSpan.class, (store, editable, input, span, spanStart, spanTextLength) -> {
                    // we do not add offset here because markwon (by default) adds spaces
                    // around inline code
                    final MarkwonEditorUtils.Match match =
                            MarkwonEditorUtils.findDelimited(input, spanStart, "`");
                    if (match != null) {
                        editable.setSpan(
                                store.get(CodeSpan.class),
                                match.start(),
                                match.end(),
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        );
                    }
                })
                .handleMarkdownSpan(BlockQuoteSpan.class, (store, editable, input, span, spanStart, spanTextLength) -> {
                    // todo: here we should actually find a proper ending of a block quote...
                    editable.setSpan(
                            store.get(BlockQuoteSpan.class),
                            spanStart,
                            spanStart + spanTextLength,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    );
                })
                .handleMarkdownSpan(LinkSpan.class, (store, editable, input, span, spanStart, spanTextLength) -> {

                    final EditLinkSpan editLinkSpan = store.get(EditLinkSpan.class);
                    editLinkSpan.link = span.getLink();

                    final int s;
                    final int e;

                    // markdown link vs. autolink
                    if ('[' == input.charAt(spanStart)) {
                        s = spanStart + 1;
                        e = spanStart + 1 + spanTextLength;
                    } else {
                        s = spanStart;
                        e = spanStart + spanTextLength;
                    }

                    editable.setSpan(
                            editLinkSpan,
                            // add underline only for link text
                            s,
                            e,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    );
                })
                // returns nullable type
                .build();
    }

    private void initBottomBar() {
        // all except block-quote wraps if have selection, or inserts at current cursor position

        final Button bold = findViewById(R.id.bold);
        final Button italic = findViewById(R.id.italic);
        final Button strike = findViewById(R.id.strike);
        final Button quote = findViewById(R.id.quote);
        final Button code = findViewById(R.id.code);

        addSpan(bold, new StrongEmphasisSpan());
        addSpan(italic, new EmphasisSpan());
        addSpan(strike, new StrikethroughSpan());

        bold.setOnClickListener(new InsertOrWrapClickListener(editText, "**"));
        italic.setOnClickListener(new InsertOrWrapClickListener(editText, "_"));
        strike.setOnClickListener(new InsertOrWrapClickListener(editText, "~~"));
        code.setOnClickListener(new InsertOrWrapClickListener(editText, "`"));

        quote.setOnClickListener(v -> {

            final int start = editText.getSelectionStart();
            final int end = editText.getSelectionEnd();

            if (start < 0) {
                return;
            }

            if (start == end) {
                editText.getText().insert(start, "> ");
            } else {
                // wrap the whole selected area in a quote
                final List<Integer> newLines = new ArrayList<>(3);
                newLines.add(start);

                final String text = editText.getText().subSequence(start, end).toString();
                int index = text.indexOf('\n');
                while (index != -1) {
                    newLines.add(start + index + 1);
                    index = text.indexOf('\n', index + 1);
                }

                for (int i = newLines.size() - 1; i >= 0; i--) {
                    editText.getText().insert(newLines.get(i), "> ");
                }
            }
        });
    }

    private static void addSpan(@NonNull TextView textView, Object... spans) {
        final SpannableStringBuilder builder = new SpannableStringBuilder(textView.getText());
        final int end = builder.length();
        for (Object span : spans) {
            builder.setSpan(span, 0, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        textView.setText(builder);
    }

    private static class InsertOrWrapClickListener implements View.OnClickListener {

        private final EditText editText;
        private final String text;

        InsertOrWrapClickListener(@NonNull EditText editText, @NonNull String text) {
            this.editText = editText;
            this.text = text;
        }

        @Override
        public void onClick(View v) {
            final int start = editText.getSelectionStart();
            final int end = editText.getSelectionEnd();

            if (start < 0) {
                return;
            }

            if (start == end) {
                // insert at current position
                editText.getText().insert(start, text);
            } else {
                editText.getText().insert(end, text);
                editText.getText().insert(start, text);
            }
        }
    }

    private static class CustomPunctuationSpan extends ForegroundColorSpan {
        CustomPunctuationSpan() {
            super(0xFFFF0000); // RED
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

    private static class EditLinkSpan extends ClickableSpan {

        interface OnClick {
            void onClick(@NonNull View widget, @NonNull String link);
        }

        private final OnClick onClick;

        String link;

        EditLinkSpan(@NonNull OnClick onClick) {
            this.onClick = onClick;
        }

        @Override
        public void onClick(@NonNull View widget) {
            if (link != null) {
                onClick.onClick(widget, link);
            }
        }
    }
}
