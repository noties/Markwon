package io.noties.markwon.app.samples;

import android.graphics.Color;
import android.text.style.ForegroundColorSpan;

import androidx.annotation.NonNull;

import org.commonmark.node.Heading;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.MarkwonSpansFactory;
import io.noties.markwon.app.sample.Tags;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.core.CoreProps;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;

@MarkwonSampleInfo(
  id = "20201203224611",
  title = "Color of heading",
  artifacts = MarkwonArtifact.CORE,
  tags = Tags.rendering
)
public class HeadingColorSample extends MarkwonTextViewSample {
  @Override
  public void render() {

    final String md = "" +
      "# Heading 1\n" +
      "## Heading 2\n" +
      "### Heading 3\n" +
      "#### Heading 4";

    final Markwon markwon = Markwon.builder(context)
      .usePlugin(new AbstractMarkwonPlugin() {
        @Override
        public void configureSpansFactory(@NonNull MarkwonSpansFactory.Builder builder) {
          builder.appendFactory(Heading.class, (configuration, props) -> {
            // here you can also inspect heading level
            final int level = CoreProps.HEADING_LEVEL.require(props);
            final int color;
            if (level == 1) {
              color = Color.RED;
            } else if (level == 2) {
              color = Color.GREEN;
            } else {
              color = Color.BLUE;
            }
            return new ForegroundColorSpan(color);
          });
        }
      })
      .build();

    markwon.setMarkdown(textView, md);
  }
}
