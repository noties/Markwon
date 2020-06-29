package io.noties.markwon.app.samples.movementmethod

import io.noties.markwon.Markwon
import io.noties.markwon.app.sample.Tags
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample
import io.noties.markwon.movement.MovementMethodPlugin
import io.noties.markwon.sample.annotations.MarkwonArtifact
import io.noties.markwon.sample.annotations.MarkwonSampleInfo

@MarkwonSampleInfo(
  id = "202006181121803",
  title = "Disable implicit movement method via plugin",
  description = "Disable implicit movement method via `MovementMethodPlugin`",
  artifacts = [MarkwonArtifact.CORE],
  tags = [Tags.links, Tags.movementMethod, Tags.recyclerView]
)
class DisableImplicitMovementMethodPluginSample : MarkwonTextViewSample() {
  override fun render() {
    val md = """
      # Disable implicit movement method via plugin
      We can disable implicit movement method via `MovementMethodPlugin` &mdash;
      [link-that-is-not-clickable](https://noties.io)
    """.trimIndent()

    val markwon = Markwon.builder(context)
      .usePlugin(MovementMethodPlugin.none())
      .build()

    markwon.setMarkdown(textView, md)
  }
}