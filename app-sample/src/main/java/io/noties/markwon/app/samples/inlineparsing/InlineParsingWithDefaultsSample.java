package io.noties.markwon.app.samples.inlineparsing;

import androidx.annotation.NonNull;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.inlineparser.MarkwonInlineParserPlugin;
import io.noties.markwon.inlineparser.OpenBracketInlineProcessor;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;
import io.noties.markwon.sample.annotations.Tag;

@MarkwonSampleInfo(
  id = "20200630170723",
  title = "Inline parsing with defaults",
  description = "Parsing with all defaults except links",
  artifacts = MarkwonArtifact.INLINE_PARSER,
  tags = {Tag.inline, Tag.parsing}
)
public class InlineParsingWithDefaultsSample extends MarkwonTextViewSample {
  @Override
  public void render() {
    // a plugin with defaults registered

    final String md = "no [links](#) for **you** `code`!";

    final Markwon markwon = Markwon.builder(context)
      .usePlugin(MarkwonInlineParserPlugin.create())
      // the same as:
//       .usePlugin(MarkwonInlineParserPlugin.create(MarkwonInlineParser.factoryBuilder()))
      .usePlugin(new AbstractMarkwonPlugin() {
        @Override
        public void configure(@NonNull Registry registry) {
          registry.require(MarkwonInlineParserPlugin.class, plugin -> {
            plugin.factoryBuilder()
              .excludeInlineProcessor(OpenBracketInlineProcessor.class);
          });
        }
      })
      .build();

    markwon.setMarkdown(textView, md);
  }
}
