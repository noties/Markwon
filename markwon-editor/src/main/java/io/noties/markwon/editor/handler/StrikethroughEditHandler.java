package io.noties.markwon.app.samples.editor.shared;

import android.text.Editable;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;

import androidx.annotation.NonNull;

import io.noties.markwon.editor.AbstractEditHandler;
import io.noties.markwon.editor.MarkwonEditorUtils;
import io.noties.markwon.editor.PersistedSpans;

public class StrikethroughEditHandler extends AbstractEditHandler<StrikethroughSpan> {

  @Override
  public void configurePersistedSpans(@NonNull PersistedSpans.Builder builder) {
    builder.persistSpan(StrikethroughSpan.class, StrikethroughSpan::new);
  }

  @Override
  public void handleMarkdownSpan(
    @NonNull PersistedSpans persistedSpans,
    @NonNull Editable editable,
    @NonNull String input,
    @NonNull StrikethroughSpan span,
    int spanStart,
    int spanTextLength) {
    final MarkwonEditorUtils.Match match =
      MarkwonEditorUtils.findDelimited(input, spanStart, "~~");
    if (match != null) {
      editable.setSpan(
        persistedSpans.get(StrikethroughSpan.class),
        match.start(),
        match.end(),
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
      );
    }
  }

  @NonNull
  @Override
  public Class<StrikethroughSpan> markdownSpanType() {
    return StrikethroughSpan.class;
  }
}
