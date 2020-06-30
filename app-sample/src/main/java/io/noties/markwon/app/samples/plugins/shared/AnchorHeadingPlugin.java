package io.noties.markwon.app.samples.plugins.shared;

import android.text.Spannable;
import android.text.Spanned;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.LinkResolverDef;
import io.noties.markwon.MarkwonConfiguration;
import io.noties.markwon.core.spans.HeadingSpan;

public class AnchorHeadingPlugin extends AbstractMarkwonPlugin {

  public interface ScrollTo {
    void scrollTo(@NonNull TextView view, int top);
  }

  private final AnchorHeadingPlugin.ScrollTo scrollTo;

  public AnchorHeadingPlugin(@NonNull AnchorHeadingPlugin.ScrollTo scrollTo) {
    this.scrollTo = scrollTo;
  }

  @Override
  public void configureConfiguration(@NonNull MarkwonConfiguration.Builder builder) {
    builder.linkResolver(new AnchorHeadingPlugin.AnchorLinkResolver(scrollTo));
  }

  @Override
  public void afterSetText(@NonNull TextView textView) {
    final Spannable spannable = (Spannable) textView.getText();
    // obtain heading spans
    final HeadingSpan[] spans = spannable.getSpans(0, spannable.length(), HeadingSpan.class);
    if (spans != null) {
      for (HeadingSpan span : spans) {
        final int start = spannable.getSpanStart(span);
        final int end = spannable.getSpanEnd(span);
        final int flags = spannable.getSpanFlags(span);
        spannable.setSpan(
          new AnchorHeadingPlugin.AnchorSpan(createAnchor(spannable.subSequence(start, end))),
          start,
          end,
          flags
        );
      }
    }
  }

  private static class AnchorLinkResolver extends LinkResolverDef {

    private final AnchorHeadingPlugin.ScrollTo scrollTo;

    AnchorLinkResolver(@NonNull AnchorHeadingPlugin.ScrollTo scrollTo) {
      this.scrollTo = scrollTo;
    }

    @Override
    public void resolve(@NonNull View view, @NonNull String link) {
      if (link.startsWith("#")) {
        final TextView textView = (TextView) view;
        final Spanned spanned = (Spannable) textView.getText();
        final AnchorHeadingPlugin.AnchorSpan[] spans = spanned.getSpans(0, spanned.length(), AnchorHeadingPlugin.AnchorSpan.class);
        if (spans != null) {
          final String anchor = link.substring(1);
          for (AnchorHeadingPlugin.AnchorSpan span : spans) {
            if (anchor.equals(span.anchor)) {
              final int start = spanned.getSpanStart(span);
              final int line = textView.getLayout().getLineForOffset(start);
              final int top = textView.getLayout().getLineTop(line);
              scrollTo.scrollTo(textView, top);
              return;
            }
          }
        }
      }
      super.resolve(view, link);
    }
  }

  private static class AnchorSpan {
    final String anchor;

    AnchorSpan(@NonNull String anchor) {
      this.anchor = anchor;
    }
  }

  @NonNull
  public static String createAnchor(@NonNull CharSequence content) {
    return String.valueOf(content)
      .replaceAll("[^\\w]", "")
      .toLowerCase();
  }
}
