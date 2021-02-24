package io.noties.markwon.app.samples.movementmethod

import io.noties.markwon.Markwon
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample
import io.noties.markwon.movement.MovementMethodPlugin
import io.noties.markwon.sample.annotations.MarkwonArtifact
import io.noties.markwon.sample.annotations.MarkwonSampleInfo
import io.noties.markwon.sample.annotations.Tag

@MarkwonSampleInfo(
  id = "20200627081631",
  title = "MovementMethodPlugin",
  description = "Plugin to control movement method",
  artifacts = [MarkwonArtifact.CORE],
  tags = [Tag.movementMethod, Tag.links, Tag.plugin]
)
class MovementMethodPluginSample : MarkwonTextViewSample() {
  override fun render() {
    val md = """
      # MovementMethodPlugin
      `MovementMethodPlugin` can be used to apply movement method 
      explicitly. Including specific case to disable implicit movement 
      method which is applied when `TextView.getMovementMethod()` 
      returns `null`. A [link](https://github.com)
    """.trimIndent()

    val markwon = Markwon.builder(context)
      .usePlugin(MovementMethodPlugin.link())
      .build()

    markwon.setMarkdown(textView, md)
  }
}