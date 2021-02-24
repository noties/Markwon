package io.noties.markwon.app.samples.basics

import io.noties.markwon.Markwon
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample
import io.noties.markwon.sample.annotations.MarkwonArtifact
import io.noties.markwon.sample.annotations.MarkwonSampleInfo
import io.noties.markwon.sample.annotations.Tag

@MarkwonSampleInfo(
  id = "20200626152255",
  title = "Simple",
  description = "The most primitive and simple way to apply markdown to a `TextView`",
  artifacts = [MarkwonArtifact.CORE],
  tags = [Tag.basics]
)
class Simple : MarkwonTextViewSample() {
  override fun render() {
    // markdown input
    val md = """
      # Heading
      
      > A quote
      
      **bold _italic_ bold**
    """.trimIndent()

    // markwon instance
    val markwon = Markwon.create(context)

    // apply raw markdown (internally parsed and rendered)
    markwon.setMarkdown(textView, md)
  }
}