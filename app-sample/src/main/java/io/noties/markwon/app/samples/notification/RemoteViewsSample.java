package io.noties.markwon.app.samples.notification;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.style.BackgroundColorSpan;
import android.text.style.BulletSpan;
import android.text.style.QuoteSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;

import com.vladsch.flexmark.ast.BlockQuote;
import com.vladsch.flexmark.ast.Code;
import com.vladsch.flexmark.ast.Emphasis;
import com.vladsch.flexmark.ast.Heading;
import com.vladsch.flexmark.ast.ListItem;
import com.vladsch.flexmark.ast.StrongEmphasis;
import com.vladsch.flexmark.ext.gfm.strikethrough.Strikethrough;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.MarkwonSpansFactory;
import io.noties.markwon.app.R;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.app.samples.notification.shared.NotificationUtils;
import io.noties.markwon.core.CoreProps;
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;
import io.noties.markwon.sample.annotations.Tag;

@MarkwonSampleInfo(
  id = "20200702090140",
  title = "RemoteViews in notification",
  description = "Display markdown with platform (system) spans in notification via `RemoteViews`",
  artifacts = MarkwonArtifact.CORE,
  tags = Tag.hack
)
public class RemoteViewsSample extends MarkwonTextViewSample {
  @Override
  public void render() {
    final String md = "" +
      "# Heading 1\n" +
//      "## Heading 2\n" +
//      "### Heading 3\n" +
//      "#### Heading 4\n" +
//      "##### Heading 5\n" +
//      "###### Heading 6\n" +
      "**bold _italic_ bold** `code` [link](#) ~~strike~~\n" +
      "* Bullet 1\n" +
      "* * Bullet 2\n" +
      "  * Bullet 3\n" +
      "> A quote **here**";

    final float[] headingSizes = {
      2.F, 1.5F, 1.17F, 1.F, .83F, .67F,
    };

    final int bulletGapWidth = (int) (8 * context.getResources().getDisplayMetrics().density + 0.5F);

    final Markwon markwon = Markwon.builder(context)
      .usePlugin(StrikethroughPlugin.create())
      .usePlugin(new AbstractMarkwonPlugin() {
        @Override
        public void configureSpansFactory(@NonNull MarkwonSpansFactory.Builder builder) {
          builder
            .setFactory(Heading.class, (configuration, props) -> new Object[]{
              new StyleSpan(Typeface.BOLD),
              new RelativeSizeSpan(headingSizes[CoreProps.HEADING_LEVEL.require(props) - 1])
            })
            .setFactory(StrongEmphasis.class, (configuration, props) -> new StyleSpan(Typeface.BOLD))
            .setFactory(Emphasis.class, (configuration, props) -> new StyleSpan(Typeface.ITALIC))
            .setFactory(Code.class, (configuration, props) -> new Object[]{
              new BackgroundColorSpan(Color.GRAY),
              new TypefaceSpan("monospace")
            })
            .setFactory(Strikethrough.class, (configuration, props) -> new StrikethroughSpan())
            .setFactory(ListItem.class, (configuration, props) -> new BulletSpan(bulletGapWidth))
            .setFactory(BlockQuote.class, (configuration, props) -> new QuoteSpan());
        }
      })
      .build();

    final RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.sample_remote_view);
    remoteViews.setTextViewText(R.id.text_view, markwon.toMarkdown(md));

    NotificationUtils.display(context, remoteViews);
  }
}
