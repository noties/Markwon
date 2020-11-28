package io.noties.markwon.app.samples.editor.shared;

import android.text.Editable;
import android.text.Spanned;

import androidx.annotation.NonNull;

import io.noties.markwon.Markwon;
import io.noties.markwon.core.MarkwonTheme;
import io.noties.markwon.core.spans.HeadingSpan;
import io.noties.markwon.editor.EditHandler;
import io.noties.markwon.editor.PersistedSpans;

public class HeadingEditHandler implements EditHandler<HeadingSpan> {

  private MarkwonTheme theme;

  @Override
  public void init(@NonNull Markwon markwon) {
    this.theme = markwon.configuration().theme();
  }

  @Override
  public void configurePersistedSpans(@NonNull PersistedSpans.Builder builder) {
    builder
      .persistSpan(Head1.class, () -> new Head1(theme))
      .persistSpan(Head2.class, () -> new Head2(theme));
  }

  @Override
  public void handleMarkdownSpan(
    @NonNull PersistedSpans persistedSpans,
    @NonNull Editable editable,
    @NonNull String input,
    @NonNull HeadingSpan span,
    int spanStart,
    int spanTextLength
  ) {
    final Class<?> type;
    switch (span.getLevel()) {
      case 1:
        type = Head1.class;
        break;
      case 2:
        type = Head2.class;
        break;
      default:
        type = null;
    }

    if (type != null) {
      final int index = input.indexOf('\n', spanStart + spanTextLength);
      final int end = index < 0
        ? input.length()
        : index;
      editable.setSpan(
        persistedSpans.get(type),
        spanStart,
        end,
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
      );
    }
  }

  @NonNull
  @Override
  public Class<HeadingSpan> markdownSpanType() {
    return HeadingSpan.class;
  }

  private static class Head1 extends HeadingSpan {
    Head1(@NonNull MarkwonTheme theme) {
      super(theme, 1);
    }
  }

  private static class Head2 extends HeadingSpan {
    Head2(@NonNull MarkwonTheme theme) {
      super(theme, 2);
    }
  }
}
