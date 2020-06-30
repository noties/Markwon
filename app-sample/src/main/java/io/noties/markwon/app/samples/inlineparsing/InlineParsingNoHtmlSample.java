package io.noties.markwon.app.samples.inlineparsing;

import androidx.annotation.NonNull;

import org.commonmark.node.Block;
import org.commonmark.node.HtmlBlock;
import org.commonmark.parser.Parser;

import java.util.Set;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.app.sample.Tags;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.core.CorePlugin;
import io.noties.markwon.inlineparser.HtmlInlineProcessor;
import io.noties.markwon.inlineparser.MarkwonInlineParserPlugin;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;

@MarkwonSampleInfo(
  id = "202006182171239",
  title = "Inline parsing exclude HTML",
  artifacts = MarkwonArtifact.INLINE_PARSER,
  tags = {Tags.parsing, Tags.inline, Tags.block}
)
public class InlineParsingNoHtmlSample extends MarkwonTextViewSample {
  @Override
  public void render() {
    final String md = "# Html <b>disabled</b>\n\n" +
      "<em>emphasis <strong>strong</strong>\n\n" +
      "<p>paragraph <img src='hey.jpg' /></p>\n\n" +
      "<test></test>\n\n" +
      "<test>";

    final Markwon markwon = Markwon.builder(context)
      .usePlugin(MarkwonInlineParserPlugin.create())
      .usePlugin(new AbstractMarkwonPlugin() {
        @Override
        public void configure(@NonNull Registry registry) {
          registry.require(MarkwonInlineParserPlugin.class, plugin -> {
            plugin.factoryBuilder()
              .excludeInlineProcessor(HtmlInlineProcessor.class);
          });
        }

        @Override
        public void configureParser(@NonNull Parser.Builder builder) {
          final Set<Class<? extends Block>> blocks = CorePlugin.enabledBlockTypes();
          blocks.remove(HtmlBlock.class);

          builder.enabledBlockTypes(blocks);
        }
      })
      .build();

    markwon.setMarkdown(textView, md);
  }
}
