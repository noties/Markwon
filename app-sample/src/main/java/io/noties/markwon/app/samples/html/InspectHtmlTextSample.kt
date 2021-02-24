package io.noties.markwon.app.samples.html

import android.text.style.URLSpan
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonVisitor
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.html.HtmlTag
import io.noties.markwon.html.MarkwonHtmlRenderer
import io.noties.markwon.html.TagHandler
import io.noties.markwon.sample.annotations.MarkwonArtifact
import io.noties.markwon.sample.annotations.MarkwonSampleInfo
import io.noties.markwon.sample.annotations.Tag

@MarkwonSampleInfo(
  id = "20210201140501",
  title = "Inspect text",
  description = "Inspect text content of a `HTML` node",
  artifacts = [MarkwonArtifact.HTML],
  tags = [Tag.html]
)
class InspectHtmlTextSample : MarkwonTextViewSample() {
  override fun render() {
    val md = """
      <p>lorem ipsum</p>
      <div class="custom-youtube-player">https://www.youtube.com/watch?v=abcdefgh</div>
    """.trimIndent()

    val markwon = Markwon.builder(context)
      .usePlugin(HtmlPlugin.create {
        it.addHandler(DivHandler())
      })
      .build()

    markwon.setMarkdown(textView, md)
  }

  class DivHandler : TagHandler() {
    override fun handle(visitor: MarkwonVisitor, renderer: MarkwonHtmlRenderer, tag: HtmlTag) {
      val attr = tag.attributes()["class"] ?: return
      if (attr.contains(CUSTOM_CLASS)) {
        val text = visitor.builder().substring(tag.start(), tag.end())
        visitor.builder().setSpan(
          URLSpan(text),
          tag.start(),
          tag.end()
        )
      }
    }

    override fun supportedTags(): Collection<String> = setOf("div")

    companion object {
      const val CUSTOM_CLASS = "custom-youtube-player"
    }
  }
}