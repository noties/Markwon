package io.noties.markwon.app.samples

import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample
import io.noties.markwon.core.CorePlugin
import io.noties.markwon.sample.annotations.MarkwonArtifact
import io.noties.markwon.sample.annotations.MarkwonSampleInfo
import io.noties.markwon.sample.annotations.Tag
import org.commonmark.node.BlockQuote
import org.commonmark.parser.Parser

@MarkwonSampleInfo(
  id = "20200627075012",
  title = "Enabled markdown blocks",
  description = "Modify/inspect enabled by `CorePlugin` block types. " +
    "Disable quotes or other blocks from being parsed",
  artifacts = [MarkwonArtifact.CORE],
  tags = [Tag.parsing, Tag.block, Tag.plugin]
)
class EnabledBlockTypesSample : MarkwonTextViewSample() {
  override fun render() {
    val md = """
      # Heading
      ## Second level
      > Quote is not handled
    """.trimIndent()

    val markwon = Markwon.builder(context)
      .usePlugin(object : AbstractMarkwonPlugin() {
        override fun configureParser(builder: Parser.Builder) {
          // obtain all enabled block types
          val enabledBlockTypes = CorePlugin.enabledBlockTypes()
          // it is safe to modify returned collection
          // remove quotes
          enabledBlockTypes.remove(BlockQuote::class.java)

          builder.enabledBlockTypes(enabledBlockTypes)
        }
      })
      .build()

    markwon.setMarkdown(textView, md)
  }
}