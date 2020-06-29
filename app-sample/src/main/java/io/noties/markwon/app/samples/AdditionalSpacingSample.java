package io.noties.markwon.app.samples;

import androidx.annotation.NonNull;

import org.commonmark.node.Heading;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.MarkwonSpansFactory;
import io.noties.markwon.app.sample.Tags;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.core.MarkwonTheme;
import io.noties.markwon.core.spans.LastLineSpacingSpan;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;

@MarkwonSampleInfo(
  id = "202006181125321",
  title = "Additional spacing after block",
  description = "Add additional spacing (padding) after last line of a block",
  artifacts = MarkwonArtifact.CORE,
  tags = {Tags.spacing, Tags.padding, Tags.span}
)
public class AdditionalSpacingSample extends MarkwonTextViewSample {
  @Override
  public void render() {
    final String md = "" +
      "# Title title title title title title title title title title \n\ntext text text text";

    // please note that bottom line (after 1 & 2 levels) will be drawn _AFTER_ padding
    final int spacing = (int) (128 * context.getResources().getDisplayMetrics().density + .5F);

    final Markwon markwon = Markwon.builder(context)
      .usePlugin(new AbstractMarkwonPlugin() {
        @Override
        public void configureTheme(@NonNull MarkwonTheme.Builder builder) {
          builder.headingBreakHeight(0);
        }

        @Override
        public void configureSpansFactory(@NonNull MarkwonSpansFactory.Builder builder) {
          builder.appendFactory(
            Heading.class,
            (configuration, props) -> new LastLineSpacingSpan(spacing));
        }
      })
      .build();
  }
}
