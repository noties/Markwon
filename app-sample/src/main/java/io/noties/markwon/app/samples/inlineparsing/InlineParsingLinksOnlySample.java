package io.noties.markwon.app.samples.inlineparsing;

import androidx.annotation.NonNull;

import org.commonmark.parser.InlineParserFactory;
import org.commonmark.parser.Parser;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.app.sample.Tags;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.inlineparser.CloseBracketInlineProcessor;
import io.noties.markwon.inlineparser.MarkwonInlineParser;
import io.noties.markwon.inlineparser.OpenBracketInlineProcessor;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;

@MarkwonSampleInfo(
  id = "20200630170412",
  title = "Links only inline parsing",
  artifacts = MarkwonArtifact.INLINE_PARSER,
  tags = {Tags.parsing, Tags.inline}
)
public class InlineParsingLinksOnlySample extends MarkwonTextViewSample {
  @Override
  public void render() {
    // note that image is considered a link now
    final String md = "**bold_bold-italic_** <u>html-u</u>, [link](#) ![alt](#image) `code`";

    // create an inline-parser-factory that will _ONLY_ parse links
    //  this would mean:
    //  * no emphasises (strong and regular aka bold and italics),
    //  * no images,
    //  * no code,
    //  * no HTML entities (&amp;)
    //  * no HTML tags
    // markdown blocks are still parsed
    final InlineParserFactory inlineParserFactory = MarkwonInlineParser.factoryBuilderNoDefaults()
      .referencesEnabled(true)
      .addInlineProcessor(new OpenBracketInlineProcessor())
      .addInlineProcessor(new CloseBracketInlineProcessor())
      .build();

    final Markwon markwon = Markwon.builder(context)
      .usePlugin(new AbstractMarkwonPlugin() {
        @Override
        public void configureParser(@NonNull Parser.Builder builder) {
          builder.inlineParserFactory(inlineParserFactory);
        }
      })
      .build();

    markwon.setMarkdown(textView, md);
  }
}
