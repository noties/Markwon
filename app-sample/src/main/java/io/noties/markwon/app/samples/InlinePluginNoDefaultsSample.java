package io.noties.markwon.app.samples;

import io.noties.markwon.Markwon;
import io.noties.markwon.app.sample.Tags;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.inlineparser.MarkwonInlineParser;
import io.noties.markwon.inlineparser.MarkwonInlineParserPlugin;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;

@MarkwonSampleInfo(
  id = "20200629170857",
  title = "Inline parsing without defaults",
  description = "Configure inline parser plugin to **not** have any **inline** parsing",
  artifacts = {MarkwonArtifact.INLINE_PARSER},
  tags = {Tags.parsing}
)
public class InlinePluginNoDefaultsSample extends MarkwonTextViewSample {
  @Override
  public void render() {
    final String md = "" +
      "# Heading\n" +
      "`code` inlined and **bold** here";

    final Markwon markwon = Markwon.builder(context)
      .usePlugin(MarkwonInlineParserPlugin.create(MarkwonInlineParser.factoryBuilderNoDefaults()))
//                .usePlugin(MarkwonInlineParserPlugin.create(MarkwonInlineParser.factoryBuilderNoDefaults(), factoryBuilder -> {
//                    // if anything, they can be included here
////                    factoryBuilder.includeDefaults()
//                }))
      .build();

    markwon.setMarkdown(textView, md);
  }
}
