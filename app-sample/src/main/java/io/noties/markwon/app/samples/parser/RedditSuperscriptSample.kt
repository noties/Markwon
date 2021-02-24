package io.noties.markwon.app.samples.parser

import android.text.TextPaint
import android.text.style.MetricAffectingSpan
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonConfiguration
import io.noties.markwon.RenderProps
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.html.HtmlTag
import io.noties.markwon.html.tag.SimpleTagHandler
import io.noties.markwon.sample.annotations.MarkwonArtifact
import io.noties.markwon.sample.annotations.MarkwonSampleInfo
import io.noties.markwon.sample.annotations.Tag.html
import io.noties.markwon.sample.annotations.Tag.reddit
import java.util.regex.Pattern

@MarkwonSampleInfo(
  id = "20210224091506",
  title = "Reddit superscript",
  artifacts = [MarkwonArtifact.HTML],
  tags = [html, reddit]
)
class RedditSuperscriptSample : MarkwonTextViewSample() {
  override fun render() {
    val md = """
      The greatest thing you'll ever learn is just to ^reddit and be ^(reddited here) in ^return and ^(hey hey hye) something something^? daaagw
    """.trimIndent()

    val markwon = Markwon.builder(context)
      .usePlugin(HtmlPlugin.create {
        it.addHandler(SmallerSuperScriptTagHandler())
      })
      .usePlugin(ProcessRedditSuperscript)
      .build()

    markwon.setMarkdown(textView, md)
  }

  object ProcessRedditSuperscript : AbstractMarkwonPlugin() {

    val re = Pattern.compile("(?:\\^\\((.+?)\\))|(?:\\^(\\S+))")

    override fun processMarkdown(markdown: String): String {
      val builder = StringBuilder()
      val matcher = re.matcher(markdown)

      var start = 0

      while (matcher.find()) {
        // if one of them, otherwise matcher should not report a match
        val match = matcher.group(1) ?: matcher.group(2)!!
        builder
          .append(markdown.substring(start, matcher.start()))
          .append("<sup>")
          .append(match)
          .append("</sup>")
        start = matcher.end()
      }

      // append the rest of markwon
      if (start < markdown.length) {
        builder.append(markdown.substring(start))
      }

      // if no match is found (thus builder is empty), then return original
      return if (builder.isEmpty()) markdown else builder.toString()
    }
  }

  class SmallerSuperScriptSpan(val ratio: Float) : MetricAffectingSpan() {
    override fun updateDrawState(tp: TextPaint?): Unit = tp?.let(::update) ?: Unit
    override fun updateMeasureState(textPaint: TextPaint) = update(textPaint)

    fun update(paint: TextPaint) = paint.run {
      textSize *= ratio
      baselineShift += (ascent() / 2F).toInt()
    }
  }

  class SmallerSuperScriptTagHandler : SimpleTagHandler() {
    override fun supportedTags(): Collection<String> = setOf("sup")

    override fun getSpans(configuration: MarkwonConfiguration, renderProps: RenderProps, tag: HtmlTag): Any {
      // 0.5 makes half the original textSize
      // default value (for library SuperscriptSpan is `HtmlPlugin.SCRIPT_DEF_TEXT_SIZE_RATIO`
      return SmallerSuperScriptSpan(0.5F)
    }
  }
}