package io.noties.markwon.app.samples.inlineparsing;

import androidx.annotation.NonNull;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.app.sample.Tags;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.inlineparser.BackticksInlineProcessor;
import io.noties.markwon.inlineparser.MarkwonInlineParser;
import io.noties.markwon.inlineparser.MarkwonInlineParserPlugin;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;

@MarkwonSampleInfo(
  id = "202006182170823",
  title = "Inline parsing with defaults",
  artifacts = MarkwonArtifact.INLINE_PARSER,
  tags = {Tags.inline, Tags.parsing}
)
public class InlineParsingNoDefaultsSample extends MarkwonTextViewSample {
  @Override
  public void render() {
    // a plugin with NO defaults registered

    final String md = "no [links](#) for **you** `code`!";

    final Markwon markwon = Markwon.builder(context)
      // pass `MarkwonInlineParser.factoryBuilderNoDefaults()` no disable all
      .usePlugin(MarkwonInlineParserPlugin.create(MarkwonInlineParser.factoryBuilderNoDefaults()))
      .usePlugin(new AbstractMarkwonPlugin() {
        @Override
        public void configure(@NonNull Registry registry) {
          registry.require(MarkwonInlineParserPlugin.class, plugin -> {
            plugin.factoryBuilder()
              .addInlineProcessor(new BackticksInlineProcessor());
          });
        }
      })
      .build();

    markwon.setMarkdown(textView, md);
  }
}
