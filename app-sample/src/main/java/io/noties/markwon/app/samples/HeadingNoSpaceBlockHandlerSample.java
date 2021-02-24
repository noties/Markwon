package io.noties.markwon.app.samples;

import androidx.annotation.NonNull;

import org.commonmark.node.Heading;
import org.commonmark.node.Node;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.BlockHandlerDef;
import io.noties.markwon.Markwon;
import io.noties.markwon.MarkwonVisitor;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;
import io.noties.markwon.sample.annotations.Tag;

@MarkwonSampleInfo(
  id = "20200629125924",
  title = "Heading no padding (block handler)",
  description = "Process padding (spacing) after heading with a " +
    "`BlockHandler`",
  artifacts = MarkwonArtifact.CORE,
  tags = {Tag.block, Tag.spacing, Tag.padding, Tag.heading, Tag.rendering}
)
public class HeadingNoSpaceBlockHandlerSample extends MarkwonTextViewSample {
  @Override
  public void render() {
    final String md = "" +
      "# Title title title title title title title title title title\n\n" +
      "text text text text" +
      "";

    final Markwon markwon = Markwon.builder(context)
      .usePlugin(new AbstractMarkwonPlugin() {
        @Override
        public void configureVisitor(@NonNull MarkwonVisitor.Builder builder) {
          builder.blockHandler(new BlockHandlerDef() {
            @Override
            public void blockEnd(@NonNull MarkwonVisitor visitor, @NonNull Node node) {
              if (node instanceof Heading) {
                if (visitor.hasNext(node)) {
                  visitor.ensureNewLine();
                  // ensure new line but do not force insert one
                }
              } else {
                super.blockEnd(visitor, node);
              }
            }
          });
        }
      })
      .build();

    markwon.setMarkdown(textView, md);
  }
}
