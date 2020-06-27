package io.noties.markwon.app.samples

import android.widget.Toast
import io.noties.markwon.Markwon
import io.noties.markwon.app.sample.Tags
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample
import io.noties.markwon.sample.annotations.MarkwonArtifact
import io.noties.markwon.sample.annotations.MarkwonSampleInfo

@MarkwonSampleInfo(
  id = "202006179072642",
  title = "Markdown in Toast",
  description = "Display _static_ markdown content in a `android.widget.Toast`",
  artifacts = [MarkwonArtifact.CORE],
  tags = [Tags.toast]
)
class ToastSample : MarkwonTextViewSample() {
  override fun render() {
    // NB! only _static_ content is going to be displayed,
    //  so, no images, tables or latex in a Toast
    val md = """
      # Heading is fine
      > Even quote if **fine**
      ```
      finally code works;
      ```
      _italic_ to put an end to it
    """.trimIndent()

    val markwon = Markwon.create(context)

    // render raw input to styled markdown
    val markdown = markwon.toMarkdown(md)

    // Toast accepts CharSequence and allows styling via spans
    Toast.makeText(context, markdown, Toast.LENGTH_LONG).show()
  }
}