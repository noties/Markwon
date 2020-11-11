package io.noties.markwon.app.samples.parser

import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.app.sample.Tags
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample
import io.noties.markwon.core.CorePlugin
import io.noties.markwon.sample.annotations.MarkwonArtifact
import io.noties.markwon.sample.annotations.MarkwonSampleInfo
import org.commonmark.node.Heading
import org.commonmark.parser.Parser
import org.commonmark.parser.block.BlockParserFactory
import org.commonmark.parser.block.BlockStart
import org.commonmark.parser.block.MatchedBlockParser
import org.commonmark.parser.block.ParserState

@MarkwonSampleInfo(
  id = "20201111221207",
  title = "Custom heading parser",
  description = "Custom heading block parser. Actual parser is not implemented",
  artifacts = [MarkwonArtifact.CORE],
  tags = [Tags.parsing, Tags.heading]
)
class CustomHeadingParserSample : MarkwonTextViewSample() {
  override fun render() {
    val md = "#Head"
    val markwon = Markwon.builder(context)
      .usePlugin(object : AbstractMarkwonPlugin() {
        override fun configureParser(builder: Parser.Builder) {
          val enabled = CorePlugin.enabledBlockTypes()
            .filter { it != Heading::class.java }
            .toSet()
          builder.enabledBlockTypes(enabled)
          builder.customBlockParserFactory(MyHeadingBlockParserFactory)
        }
      })
      .build()
    markwon.setMarkdown(textView, md)
  }

  object MyHeadingBlockParserFactory : BlockParserFactory {
    override fun tryStart(state: ParserState, matchedBlockParser: MatchedBlockParser): BlockStart {
      // TODO("Not yet implemented")
      return BlockStart.none()
    }
  }
}