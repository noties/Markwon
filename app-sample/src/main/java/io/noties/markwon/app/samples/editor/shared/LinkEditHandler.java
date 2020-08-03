package io.noties.markwon.app.samples.editor.shared;

import android.text.Editable;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.view.View;

import androidx.annotation.NonNull;

import io.noties.markwon.core.spans.LinkSpan;
import io.noties.markwon.editor.AbstractEditHandler;
import io.noties.markwon.editor.PersistedSpans;

public class LinkEditHandler extends AbstractEditHandler<LinkSpan> {

  public interface OnClick {
    void onClick(@NonNull View widget, @NonNull String link);
  }

  private final OnClick onClick;

  public LinkEditHandler(@NonNull OnClick onClick) {
    this.onClick = onClick;
  }

  @Override
  public void configurePersistedSpans(@NonNull PersistedSpans.Builder builder) {
    builder.persistSpan(EditLinkSpan.class, () -> new EditLinkSpan(onClick));
  }

  @Override
  public void handleMarkdownSpan(
    @NonNull PersistedSpans persistedSpans,
    @NonNull Editable editable,
    @NonNull String input,
    @NonNull LinkSpan span,
    int spanStart,
    int spanTextLength) {

    final EditLinkSpan editLinkSpan = persistedSpans.get(EditLinkSpan.class);
    editLinkSpan.link = span.getLink();

    // First first __letter__ to find link content (scheme start in URL, receiver in email address)
    // NB! do not use phone number auto-link (via LinkifyPlugin) as we cannot guarantee proper link
    //  display. For example, we _could_ also look for a digit, but:
    //  * if phone number start with special symbol, we won't have it (`+`, `(`)
    //  * it might interfere with an ordered-list
    int start = -1;

    for (int i = spanStart, length = input.length(); i < length; i++) {
      if (Character.isLetter(input.charAt(i))) {
        start = i;
        break;
      }
    }

    if (start > -1) {
      editable.setSpan(
        editLinkSpan,
        start,
        start + spanTextLength,
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
      );
    }
  }

  @NonNull
  @Override
  public Class<LinkSpan> markdownSpanType() {
    return LinkSpan.class;
  }

  static class EditLinkSpan extends ClickableSpan {

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
