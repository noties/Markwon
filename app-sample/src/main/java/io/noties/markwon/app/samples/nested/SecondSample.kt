package io.noties.markwon.app.samples.nested

import io.noties.markwon.Markwon
import io.noties.markwon.app.ui.MarkwonTextViewSample
import io.noties.markwon.sample.annotations.MarkwonArtifact
import io.noties.markwon.sample.annotations.MarkwonSampleInfo

@MarkwonSampleInfo(
  id = "202006177155656",
  title = "Second sample",
  description = "# Hey hey hey",
  artifacts = [MarkwonArtifact.CORE, MarkwonArtifact.RECYCLER],
  tags = ["b", "c", "a", "test"]
)
class SecondSample : MarkwonTextViewSample() {
  override fun render() {
    val md = """
      # Hello second
      
      ```java
      final int i = 0;
      ```
    """.trimIndent()

    val markwon = Markwon.create(context)
    markwon.setMarkdown(textView, md)
  }

  val _mock: String
  get() = """
    a
    b
    d
    s
    as
    sd
    ds
    sd
    s
    sd
    sd
    ds
    sd
    sd
    sd
    sd
    sd
    sd
    ds
    sd
    sd
    
    s
    sd
    sd
    sd
    sd
    sd
    
    ds
    sd
    sd
    sd
  """.trimIndent()
}