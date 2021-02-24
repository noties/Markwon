package io.noties.markwon.app.samples.movementmethod

import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonPlugin
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample
import io.noties.markwon.core.CorePlugin
import io.noties.markwon.sample.annotations.MarkwonArtifact
import io.noties.markwon.sample.annotations.MarkwonSampleInfo
import io.noties.markwon.sample.annotations.Tag

@MarkwonSampleInfo(
  id = "20200627081256",
  title = "Disable implicit movement method",
  description = "Configure `Markwon` to **not** apply implicit movement method, " +
    "which consumes touch events when used in a `RecyclerView` even when " +
    "markdown does not contain links",
  artifacts = [MarkwonArtifact.CORE],
  tags = [Tag.plugin, Tag.movementMethod, Tag.links, Tag.recyclerView]
)
class DisableImplicitMovementMethodSample : MarkwonTextViewSample() {
  override fun render() {
    val md = """
      # Disable implicit movement method
      Sometimes it is required to stop `Markwon` from applying _implicit_
      movement method (for example when used inside in a `RecyclerView`
      in order to make the whole itemView clickable). `Markwon` inspects
      `TextView` and applies implicit movement method if `getMovementMethod()` 
      returns `null`. No [links](https://github.com) will be clickable in this
      markdown
    """.trimIndent()

    val markwon = Markwon.builder(context)
      .usePlugin(object : AbstractMarkwonPlugin() {
        override fun configure(registry: MarkwonPlugin.Registry) {
          registry.require(CorePlugin::class.java)
            // this flag will make sure that CorePlugin won't apply any movement method
            .hasExplicitMovementMethod(true)
        }
      })
      .build()

    markwon.setMarkdown(textView, md)
  }
}