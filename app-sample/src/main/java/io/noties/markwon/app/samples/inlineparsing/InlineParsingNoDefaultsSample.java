package io.noties.markwon.app.samples.inlineparsing;

import androidx.annotation.NonNull;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.inlineparser.BackticksInlineProcessor;
import io.noties.markwon.inlineparser.MarkwonInlineParser;
import io.noties.markwon.inlineparser.MarkwonInlineParserPlugin;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;
import io.noties.markwon.sample.annotations.Tag;

@MarkwonSampleInfo(
  id = "20200630170823",
  title = "Inline parsing no defaults",
  description = "Parsing only inline code and disable all the rest",
  artifacts = MarkwonArtifact.INLINE_PARSER,
  tags = {Tag.inline, Tag.parsing}
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
