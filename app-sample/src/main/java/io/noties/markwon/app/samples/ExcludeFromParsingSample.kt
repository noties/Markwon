package io.noties.markwon.app.samples

import android.text.SpannableStringBuilder
import io.noties.debug.Debug
import io.noties.markwon.Markwon
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample
import io.noties.markwon.sample.annotations.MarkwonArtifact
import io.noties.markwon.sample.annotations.MarkwonSampleInfo
import io.noties.markwon.sample.annotations.Tag
import java.util.regex.Pattern

@MarkwonSampleInfo(
  id = "20201111221945",
  title = "Exclude part of input from parsing",
  description = "Exclude part of input from parsing by splitting input with delimiters",
  artifacts = [MarkwonArtifact.CORE],
  tags = [Tag.parsing]
)
class ExcludeFromParsingSample : MarkwonTextViewSample() {
  override fun render() {

    // cannot have continuous markdown between parts (so a node started in one part and ended in other)
    //  with this approach
    // also exclude will start a new block and won't seamlessly continue any existing markdown one (so
    //  if started inside a blockquote, then blockquote would be closed)

    val md = """
      # Hello
      
      we are **going** to exclude some parts of this input _from_ parsing

      $EXCLUDE_START
      what is **good** is that we
      > do not need to care about blocks or inlines
      * and
      * everything
      * else
      $EXCLUDE_END

      **then** markdown _again_
      
      and empty exclude at end: $EXCLUDE_START$EXCLUDE_END
    """.trimIndent()

    val markwon = Markwon.create(context)
    val matcher = Pattern.compile(RE, Pattern.MULTILINE).matcher(md)

    val builder by lazy(LazyThreadSafetyMode.NONE) {
      SpannableStringBuilder()
    }

    var end: Int = 0

    while (matcher.find()) {
      val start = matcher.start()
      Debug.i(end, start, md.substring(end, start))
      builder.append(markwon.toMarkdown(md.substring(end, start)))
      builder.append(matcher.group(1))
      end = matcher.end()
    }

    if (end != md.length) {
      builder.append(markwon.toMarkdown(md.substring(end)))
    }

    markwon.setParsedMarkdown(textView, builder)
  }

  private companion object {
    const val EXCLUDE_START = "##IGNORE##"
    const val EXCLUDE_END = "--IGNORE--"

    const val RE = "${EXCLUDE_START}([\\s\\S]*?)${EXCLUDE_END}"
  }
}