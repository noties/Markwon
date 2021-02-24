package io.noties.markwon.app.samples.movementmethod

import io.noties.markwon.Markwon
import io.noties.markwon.app.BuildConfig
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample
import io.noties.markwon.sample.annotations.MarkwonArtifact
import io.noties.markwon.sample.annotations.MarkwonSampleInfo
import io.noties.markwon.sample.annotations.Tag

@MarkwonSampleInfo(
  id = "20200627075524",
  title = "Implicit movement method",
  description = "By default movement method is applied for links to be clickable",
  artifacts = [MarkwonArtifact.CORE],
  tags = [Tag.movementMethod, Tag.links, Tag.defaults]
)
class ImplicitMovementMethodSample : MarkwonTextViewSample() {
  override fun render() {
    val md = """
      # Implicit movement method
      By default `Markwon` applies `LinkMovementMethod` if it is missing,
      so in order for [links](${BuildConfig.GIT_REPOSITORY}) to be clickable
      nothing special should be done
    """.trimIndent()

    // by default Markwon will apply a `LinkMovementMethod` if
    //  it is missing. So, in order for links to be clickable
    //  nothing should be done

    val markwon = Markwon.create(context)

    markwon.setMarkdown(textView, md)
  }
}