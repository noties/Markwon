package io.noties.markwon.app.samples;

import androidx.annotation.NonNull;

import org.commonmark.node.Heading;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.MarkwonVisitor;
import io.noties.markwon.app.sample.Tags;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.core.CoreProps;
import io.noties.markwon.core.MarkwonTheme;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;

@MarkwonSampleInfo(
  id = "202006181125622",
  title = "Heading no padding",
  description = "Do not add a new line after heading node",
  artifacts = MarkwonArtifact.CORE,
  tags = {Tags.spacing, Tags.padding, Tags.spacing, Tags.rendering}
)
public class HeadingNoSpaceSample extends MarkwonTextViewSample {
  @Override
  public void render() {
    final String md = "" +
      "# Title title title title title title title title title title" +
      "\n\ntext text text text" +
      "";

    final Markwon markwon = Markwon.builder(context)
      .usePlugin(new AbstractMarkwonPlugin() {
        @Override
        public void configureTheme(@NonNull MarkwonTheme.Builder builder) {
          builder.headingBreakHeight(0);
        }

        @Override
        public void configureVisitor(@NonNull MarkwonVisitor.Builder builder) {
          builder.on(Heading.class, (visitor, heading) -> {

            visitor.ensureNewLine();

            final int length = visitor.length();
            visitor.visitChildren(heading);

            CoreProps.HEADING_LEVEL.set(visitor.renderProps(), heading.getLevel());

            visitor.setSpansForNodeOptional(heading, length);

            if (visitor.hasNext(heading)) {
              visitor.ensureNewLine();
              // by default Markwon adds a new line here
//              visitor.forceNewLine();
            }
          });
        }
      })
      .build();

    markwon.setMarkdown(textView, md);
  }
}
