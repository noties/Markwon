package io.noties.markwon.app.samples;

import androidx.annotation.NonNull;

import org.commonmark.node.Node;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.BlockHandlerDef;
import io.noties.markwon.Markwon;
import io.noties.markwon.MarkwonVisitor;
import io.noties.markwon.app.sample.Tags;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;

@MarkwonSampleInfo(
  id = "20200629130227",
  title = "All blocks no padding",
  description = "Do not render new lines (padding) after all blocks",
  artifacts = MarkwonArtifact.CORE,
  tags = {Tags.block, Tags.spacing, Tags.padding, Tags.rendering}
)
public class AllBlocksNoForcedNewLineSample extends MarkwonTextViewSample {
  @Override
  public void render() {
    final String md = "" +
      "# Hello there!\n\n" +
      "* a first\n" +
      "* second\n" +
      "- third\n" +
      "* * nested one\n\n" +
      "> block quote\n\n" +
      "> > and nested one\n\n" +
      "```java\n" +
      "final int i = 0;\n" +
      "```\n\n";

    // extend default block handler
    final MarkwonVisitor.BlockHandler blockHandler = new BlockHandlerDef() {
      @Override
      public void blockEnd(@NonNull MarkwonVisitor visitor, @NonNull Node node) {
        if (visitor.hasNext(node)) {
          visitor.ensureNewLine();
        }
      }
    };

    final Markwon markwon = Markwon.builder(context)
      .usePlugin(new AbstractMarkwonPlugin() {
        @Override
        public void configureVisitor(@NonNull MarkwonVisitor.Builder builder) {
          builder.blockHandler(blockHandler);
        }
      })
      .build();

    markwon.setMarkdown(textView, md);
  }
}
