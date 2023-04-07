package io.noties.markwon.app.samples.basics

import android.text.Spanned
import com.vladsch.flexmark.util.ast.Node
import io.noties.markwon.Markwon
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample
import io.noties.markwon.core.CorePlugin
import io.noties.markwon.sample.annotations.MarkwonArtifact
import io.noties.markwon.sample.annotations.MarkwonSampleInfo
import io.noties.markwon.sample.annotations.Tag

@MarkwonSampleInfo(
  id = "20200626153426",
  title = "Simple with walk-through",
  description = "Walk-through for simple use case",
  artifacts = [MarkwonArtifact.CORE],
  tags = [Tag.basics]
)
class SimpleWalkthrough : MarkwonTextViewSample() {
  override fun render() {
    val md: String = """
      # Hello!
      
      > a quote
      
      ```
      code block
      ```
    """.trimIndent()

    // create markwon instance via builder method
    val markwon: Markwon = Markwon.builder(context)
      // add required plugins
      // NB, there is no need to add CorePlugin as it is added automatically
      .usePlugin(CorePlugin.create())
      .build()

    // parse markdown into commonmark representation
    val node: Node = markwon.parse(md)

    // render commonmark node
    val markdown: Spanned = markwon.render(node)

    // apply it to a TextView
    markwon.setParsedMarkdown(textView, markdown)
  }
}