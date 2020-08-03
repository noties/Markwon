package io.noties.markwon.app.samples.editor.shared;

import android.text.Editable;
import android.text.Spanned;

import androidx.annotation.NonNull;

import io.noties.markwon.Markwon;
import io.noties.markwon.core.MarkwonTheme;
import io.noties.markwon.core.spans.BlockQuoteSpan;
import io.noties.markwon.editor.EditHandler;
import io.noties.markwon.editor.PersistedSpans;

public class BlockQuoteEditHandler implements EditHandler<BlockQuoteSpan> {

  private MarkwonTheme theme;

  @Override
  public void init(@NonNull Markwon markwon) {
    this.theme = markwon.configuration().theme();
  }

  @Override
  public void configurePersistedSpans(@NonNull PersistedSpans.Builder builder) {
    builder.persistSpan(BlockQuoteSpan.class, () -> new BlockQuoteSpan(theme));
  }

  @Override
  public void handleMarkdownSpan(
    @NonNull PersistedSpans persistedSpans,
    @NonNull Editable editable,
    @NonNull String input,
    @NonNull BlockQuoteSpan span,
    int spanStart,
    int spanTextLength) {
    // todo: here we should actually find a proper ending of a block quote...
    editable.setSpan(
      persistedSpans.get(BlockQuoteSpan.class),
      spanStart,
      spanStart + spanTextLength,
      Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    );
  }

  @NonNull
  @Override
  public Class<BlockQuoteSpan> markdownSpanType() {
    return BlockQuoteSpan.class;
  }
}
