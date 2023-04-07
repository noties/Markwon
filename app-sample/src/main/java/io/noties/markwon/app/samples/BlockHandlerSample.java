package io.noties.markwon.app.samples;

import androidx.annotation.NonNull;

import com.vladsch.flexmark.util.ast.Node;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.MarkwonVisitor;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;
import io.noties.markwon.sample.annotations.Tag;

@MarkwonSampleInfo(
  id = "20200729090524",
  title = "Block handler",
  description = "Custom block delimiters that control new lines after block nodes",
  artifacts = MarkwonArtifact.CORE,
  tags = Tag.rendering
)
public class BlockHandlerSample extends MarkwonTextViewSample {
  @Override
  public void render() {
    final String md = "" +
      "# Heading\n" +
      "* one\n" +
      "* two\n" +
      "* three\n" +
      "---\n" +
      "> a quote\n\n" +
      "```\n" +
      "code\n" +
      "```\n" +
      "some text after";

    final Markwon markwon = Markwon.builder(context)
      .usePlugin(new AbstractMarkwonPlugin() {
        @Override
        public void configureVisitor(@NonNull MarkwonVisitor.Builder builder) {
          builder.blockHandler(new BlockHandlerNoAdditionalNewLines());
        }
      })
      .build();

    markwon.setMarkdown(textView, md);
  }
}

class BlockHandlerNoAdditionalNewLines implements MarkwonVisitor.BlockHandler {

  @Override
  public void blockStart(@NonNull MarkwonVisitor visitor, @NonNull Node node) {
    // ensure that content rendered on a new line
    visitor.ensureNewLine();
  }

  @Override
  public void blockEnd(@NonNull MarkwonVisitor visitor, @NonNull Node node) {
    if (visitor.hasNext(node)) {
      visitor.ensureNewLine();
      // by default markwon here also has:
      //  visitor.forceNewLine();
    }
  }
}
