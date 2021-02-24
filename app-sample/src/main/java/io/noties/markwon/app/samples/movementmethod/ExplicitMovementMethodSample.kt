package io.noties.markwon.app.samples.movementmethod

import android.text.method.ScrollingMovementMethod
import io.noties.markwon.Markwon
import io.noties.markwon.app.BuildConfig
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample
import io.noties.markwon.sample.annotations.MarkwonArtifact
import io.noties.markwon.sample.annotations.MarkwonSampleInfo
import io.noties.markwon.sample.annotations.Tag

@MarkwonSampleInfo(
  id = "20200627080007",
  title = "Explicit movement method",
  description = "When a movement method already applied to a `TextView`" +
    "`Markwon` won't try to apply own (implicit) one",
  artifacts = [MarkwonArtifact.CORE],
  tags = [Tag.movementMethod, Tag.links]
)
class ExplicitMovementMethodSample : MarkwonTextViewSample() {
  override fun render() {
    val md = """
      # Explicit movement method
      If `TextView` already has a movement method specified, then `Markwon`
      won't be applying a default one. You can specify movement 
      method via call to `setMovementMethod`. If your movement method can
      handle [links](${BuildConfig.GIT_REPOSITORY}) then link would be
      _clickable_
    """.trimIndent()

    val markwon = Markwon.create(context)

    // own movement method that does not handle clicks would still be used
    //  (no default aka implicit method would be applied by Markwon)
    textView.movementMethod = ScrollingMovementMethod.getInstance()

    markwon.setMarkdown(textView, md)
  }
}