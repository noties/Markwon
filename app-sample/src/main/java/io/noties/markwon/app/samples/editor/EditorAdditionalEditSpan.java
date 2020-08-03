package io.noties.markwon.app.samples.editor;

import android.text.Editable;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

import androidx.annotation.NonNull;

import io.noties.markwon.Markwon;
import io.noties.markwon.app.sample.Tags;
import io.noties.markwon.app.samples.editor.shared.MarkwonEditTextSample;
import io.noties.markwon.core.spans.StrongEmphasisSpan;
import io.noties.markwon.editor.AbstractEditHandler;
import io.noties.markwon.editor.MarkwonEditor;
import io.noties.markwon.editor.MarkwonEditorTextWatcher;
import io.noties.markwon.editor.MarkwonEditorUtils;
import io.noties.markwon.editor.PersistedSpans;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;

@MarkwonSampleInfo(
  id = "20200629165136",
  title = "Additional edit span",
  description = "Additional _edit_ span (span that is present in " +
    "`EditText` along with punctuation",
  artifacts = {MarkwonArtifact.EDITOR, MarkwonArtifact.INLINE_PARSER},
  tags = {Tags.editor, Tags.span}
)
public class EditorAdditionalEditSpan extends MarkwonEditTextSample {
  @Override
  public void render() {
    // An additional span is used to highlight strong-emphasis

    final MarkwonEditor editor = MarkwonEditor.builder(Markwon.create(context))
      .useEditHandler(new BoldEditHandler())
      .build();

    editText.addTextChangedListener(MarkwonEditorTextWatcher.withProcess(editor));
  }
}

class BoldEditHandler extends AbstractEditHandler<StrongEmphasisSpan> {
  @Override
  public void configurePersistedSpans(@NonNull PersistedSpans.Builder builder) {
    // Here we define which span is _persisted_ in EditText, it is not removed
    //  from EditText between text changes, but instead - reused (by changing
    //  position). Consider it as a cache for spans. We could use `StrongEmphasisSpan`
    //  here also, but I chose Bold to indicate that this span is not the same
    //  as in off-screen rendered markdown
    builder.persistSpan(Bold.class, Bold::new);
  }

  @Override
  public void handleMarkdownSpan(
    @NonNull PersistedSpans persistedSpans,
    @NonNull Editable editable,
    @NonNull String input,
    @NonNull StrongEmphasisSpan span,
    int spanStart,
    int spanTextLength) {
    // Unfortunately we cannot hardcode delimiters length here (aka spanTextLength + 4)
    //  because multiple inline markdown nodes can refer to the same text.
    //  For example, `**_~~hey~~_**` - we will receive `**_~~` in this method,
    //  and thus will have to manually find actual position in raw user input
    final MarkwonEditorUtils.Match match =
      MarkwonEditorUtils.findDelimited(input, spanStart, "**", "__");
    if (match != null) {
      editable.setSpan(
        // we handle StrongEmphasisSpan and represent it with Bold in EditText
        //  we still could use StrongEmphasisSpan, but it must be accessed
        //  via persistedSpans
        persistedSpans.get(Bold.class),
        match.start(),
        match.end(),
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
      );
    }
  }

  @NonNull
  @Override
  public Class<StrongEmphasisSpan> markdownSpanType() {
    return StrongEmphasisSpan.class;
  }
}

class Bold extends MetricAffectingSpan {
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
