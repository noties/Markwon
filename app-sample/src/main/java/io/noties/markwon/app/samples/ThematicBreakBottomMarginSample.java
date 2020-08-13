package io.noties.markwon.app.samples;

import androidx.annotation.NonNull;

import org.commonmark.node.Node;
import org.commonmark.node.ThematicBreak;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.BlockHandlerDef;
import io.noties.markwon.Markwon;
import io.noties.markwon.MarkwonVisitor;
import io.noties.markwon.app.sample.Tags;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;

@MarkwonSampleInfo(
  id = "20200813154415",
  title = "Thematic break bottom margin",
  description = "Do not add a new line after thematic break (with the `BlockHandler`)",
  artifacts = MarkwonArtifact.CORE,
  tags = Tags.rendering
)
public class ThematicBreakBottomMarginSample extends MarkwonTextViewSample {
  @Override
  public void render() {
    final String md = "" +
      "# Thematic break and margin\n\n" +
      "So, what if....\n\n" +
      "---\n\n" +
      "And **now**";

    final Markwon markwon = Markwon.builder(context)
      .usePlugin(new AbstractMarkwonPlugin() {
        @Override
        public void configureVisitor(@NonNull MarkwonVisitor.Builder builder) {
          builder.blockHandler(new BlockHandlerDef() {
            @Override
            public void blockStart(@NonNull MarkwonVisitor visitor, @NonNull Node node) {
              // also can control block start
              super.blockStart(visitor, node);
            }

            @Override
            public void blockEnd(@NonNull MarkwonVisitor visitor, @NonNull Node node) {
              if (visitor.hasNext(node)) {
                visitor.ensureNewLine();

                // thematic break won't have a new line
                // similarly you can control other blocks
                if (!(node instanceof ThematicBreak)) {
                  visitor.forceNewLine();
                }
              }
            }
          });
        }
      })
      .build();

    markwon.setMarkdown(textView, md);
  }
}
