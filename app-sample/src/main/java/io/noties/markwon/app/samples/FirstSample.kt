package io.noties.markwon.app.samples

import io.noties.markwon.Markwon
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample
import io.noties.markwon.sample.annotations.MarkwonArtifact
import io.noties.markwon.sample.annotations.MarkwonSampleInfo

@MarkwonSampleInfo(
  id = "202006164150023",
  title = "First Sample",
  description = "This **is** _the first_ sample",
  artifacts = [MarkwonArtifact.CORE],
  tags = ["test"]
)
class FirstSample : MarkwonTextViewSample() {

  override fun render() {

    val md = """
            # Hello there!
            > How are you?
            
            **bold** and _italic_ and **bold _italic bold_ just bold**
        """.trimIndent()

    val markwon: Markwon = Markwon.create(context)

    markwon.setMarkdown(textView, md)
  }
}