package io.noties.markwon.app.samples;

import androidx.annotation.NonNull;

import org.commonmark.parser.Parser;

import java.util.Collections;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.inlineparser.MarkwonInlineParser;
import io.noties.markwon.inlineparser.MarkwonInlineParserPlugin;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;
import io.noties.markwon.sample.annotations.Tag;

@MarkwonSampleInfo(
  id = "20200629171212",
  title = "No parsing",
  description = "All commonmark parsing is disabled (both inlines and blocks)",
  artifacts = MarkwonArtifact.CORE,
  tags = {Tag.parsing, Tag.rendering}
)
public class NoParsingSample extends MarkwonTextViewSample {
  @Override
  public void render() {
    final String md = "" +
      "# Heading\n" +
      "[link](#) was _here_ and `then` and it was:\n" +
      "> a quote\n" +
      "```java\n" +
      "final int someJavaCode = 0;\n" +
      "```\n";

    final Markwon markwon = Markwon.builder(context)
      // disable inline parsing
      .usePlugin(MarkwonInlineParserPlugin.create(MarkwonInlineParser.factoryBuilderNoDefaults()))
      .usePlugin(new AbstractMarkwonPlugin() {
        @Override
        public void configureParser(@NonNull Parser.Builder builder) {
          builder.enabledBlockTypes(Collections.emptySet());
        }
      })
      .build();

    markwon.setMarkdown(textView, md);
  }
}
