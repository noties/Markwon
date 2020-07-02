package io.noties.markwon.app.samples.notification;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.style.BackgroundColorSpan;
import android.text.style.BulletSpan;
import android.text.style.QuoteSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;

import androidx.annotation.NonNull;

import org.commonmark.ext.gfm.strikethrough.Strikethrough;
import org.commonmark.node.BlockQuote;
import org.commonmark.node.Code;
import org.commonmark.node.Emphasis;
import org.commonmark.node.ListItem;
import org.commonmark.node.StrongEmphasis;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.MarkwonSpansFactory;
import io.noties.markwon.app.sample.Tags;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.app.samples.notification.shared.NotificationUtils;
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;

@MarkwonSampleInfo(
  id = "202007183130729",
  title = "Markdown in Notification",
  description = "Proof of concept of using `Markwon` with `android.app.Notification`",
  artifacts = MarkwonArtifact.CORE,
  tags = Tags.hack
)
public class NotificationSample extends MarkwonTextViewSample {
  @Override
  public void render() {
    // supports:
    // * bold -> StyleSpan(BOLD)
    // * italic -> StyleSpan(ITALIC)
    // * quote -> QuoteSpan()
    // * strikethrough -> StrikethroughSpan()
    // * bullet list -> BulletSpan()

    // * link -> is styled but not clickable
    // * code -> typeface monospace works, background is not

    final String md = "" +
      "**bold _bold-italic_ bold** ~~strike~~ `code` [link](#)\n\n" +
      "* bullet-one\n" +
      "* * bullet-two\n" +
      "  * bullet-three\n\n" +
      "> a quote\n\n" +
      "";

    final Markwon markwon = Markwon.builder(context)
      .usePlugin(StrikethroughPlugin.create())
      .usePlugin(new AbstractMarkwonPlugin() {
        @Override
        public void configureSpansFactory(@NonNull MarkwonSpansFactory.Builder builder) {
          builder
            .setFactory(Emphasis.class, (configuration, props) -> new StyleSpan(Typeface.ITALIC))
            .setFactory(StrongEmphasis.class, (configuration, props) -> new StyleSpan(Typeface.BOLD))
            .setFactory(BlockQuote.class, (configuration, props) -> new QuoteSpan())
            .setFactory(Strikethrough.class, (configuration, props) -> new StrikethroughSpan())
            // NB! notification does not handle background color
            .setFactory(Code.class, (configuration, props) -> new Object[]{
              new BackgroundColorSpan(Color.GRAY),
              new TypefaceSpan("monospace")
            })
            // NB! both ordered and bullet list items
            .setFactory(ListItem.class, (configuration, props) -> new BulletSpan());
        }
      })
      .build();

    markwon.setMarkdown(textView, md);

    NotificationUtils.display(context, markwon.toMarkdown(md));
  }
}
