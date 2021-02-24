package io.noties.markwon.app.samples

import android.content.Context
import io.noties.debug.Debug
import io.noties.markwon.Markwon
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.sample.annotations.MarkwonArtifact
import io.noties.markwon.sample.annotations.MarkwonSampleInfo
import io.noties.markwon.sample.annotations.Tag
import java.util.Collections
import java.util.WeakHashMap

@MarkwonSampleInfo(
  id = "20200707102458",
  title = "Cache Markwon instance",
  description = "A static cache for `Markwon` instance " +
    "to be associated with a `Context`",
  artifacts = [MarkwonArtifact.CORE],
  tags = [Tag.cache]
)
class CacheMarkwonSample : MarkwonTextViewSample() {
  override fun render() {
    render("# First!")
    render("## Second!!")
    render("### Third!!!")
  }

  fun render(md: String) {
    val markwon = MarkwonCache.with(context)
    Debug.i("markwon: ${markwon.hashCode()}, md: '$md'")
    markwon.setMarkdown(textView, md)
  }
}

object MarkwonCache {
  private val cache = Collections.synchronizedMap(WeakHashMap<Context, Markwon>())

  fun with(context: Context): Markwon {
    // yeah, why work as expected? new value is returned each time, no caching occur
    //  kotlin: 1.3.72
    //  intellij plugin: 1.3.72-release-Studio4.0-5
//      return cache.getOrPut(context) {
//        // create your markwon instance here
//        return Markwon.builder(context)
//          .usePlugin(StrikethroughPlugin.create())
//          .build()
//      }

    return cache[context] ?: {
      Markwon.builder(context)
        .usePlugin(StrikethroughPlugin.create())
        .build()
        .also {
          cache[context] = it
        }
    }.invoke()
  }
}