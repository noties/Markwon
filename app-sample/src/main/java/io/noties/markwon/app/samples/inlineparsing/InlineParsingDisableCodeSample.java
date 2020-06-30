package io.noties.markwon.app.samples.inlineparsing;

import androidx.annotation.NonNull;

import org.commonmark.node.Block;
import org.commonmark.node.BlockQuote;
import org.commonmark.node.Heading;
import org.commonmark.node.HtmlBlock;
import org.commonmark.node.ListBlock;
import org.commonmark.node.ThematicBreak;
import org.commonmark.parser.InlineParserFactory;
import org.commonmark.parser.Parser;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.app.sample.Tags;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.inlineparser.BackticksInlineProcessor;
import io.noties.markwon.inlineparser.MarkwonInlineParser;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;

@MarkwonSampleInfo(
  id = "202006182170607",
  title = "Disable code inline parsing",
  artifacts = MarkwonArtifact.INLINE_PARSER,
  tags = {Tags.inline, Tags.parsing}
)
public class InlineParsingDisableCodeSample extends MarkwonTextViewSample {
  @Override
  public void render() {
    // parses all as usual, but ignores code (inline and block)

    final String md = "# Head!\n\n" +
      "* one\n" +
      "+ two\n\n" +
      "and **bold** to `you`!\n\n" +
      "> a quote _em_\n\n" +
      "```java\n" +
      "final int i = 0;\n" +
      "```\n\n" +
      "**Good day!**";

    final InlineParserFactory inlineParserFactory = MarkwonInlineParser.factoryBuilder()
      .excludeInlineProcessor(BackticksInlineProcessor.class)
      .build();

    // unfortunately there is no _exclude_ method for parser-builder
    final Set<Class<? extends Block>> enabledBlocks = new HashSet<Class<? extends Block>>() {{
      // IndentedCodeBlock.class and FencedCodeBlock.class are missing
      // this is full list (including above) that can be passed to `enabledBlockTypes` method
      addAll(Arrays.asList(
        BlockQuote.class,
        Heading.class,
        HtmlBlock.class,
        ThematicBreak.class,
        ListBlock.class));
    }};

    final Markwon markwon = Markwon.builder(context)
      .usePlugin(new AbstractMarkwonPlugin() {
        @Override
        public void configureParser(@NonNull Parser.Builder builder) {
          builder
            .inlineParserFactory(inlineParserFactory)
            .enabledBlockTypes(enabledBlocks);
        }
      })
      .build();

    markwon.setMarkdown(textView, md);
  }
}
