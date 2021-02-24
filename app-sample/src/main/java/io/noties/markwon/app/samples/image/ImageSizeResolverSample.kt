package io.noties.markwon.app.samples.image

import android.content.res.Resources
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonConfiguration
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.image.ImageSize
import io.noties.markwon.image.ImageSizeResolverDef
import io.noties.markwon.image.ImagesPlugin
import io.noties.markwon.sample.annotations.MarkwonArtifact
import io.noties.markwon.sample.annotations.MarkwonSampleInfo
import io.noties.markwon.sample.annotations.Tag

@MarkwonSampleInfo(
  id = "20210201165512",
  title = "ImageSizeResolver",
  description = "Custom `ImageSizeResolver` that treats dimension values " +
    "as density-based (like `dp`, `dip` in resources)",
  artifacts = [MarkwonArtifact.CORE],
  tags = [Tag.image]
)
class ImageSizeResolverSample : MarkwonTextViewSample() {
  override fun render() {
    val image = "https://github.com/dcurtis/markdown-mark/raw/master/png/208x128-solid.png"
    val md = """
      **150px x 150px**: <img src="$image" width="150px" height="150px" alt="150px x 150px" />
      
      **150 x 150**: <img src="$image" width="150" height="150" alt="150 x 150" />
      
      **no dimension**: <img src="$image" alt="no dimension" />
      
      **just width 150**: <img src="$image" width="150" alt="150" />
    """.trimIndent()

    val markwon = Markwon.builder(context)
      .usePlugin(object : AbstractMarkwonPlugin() {
        override fun configureConfiguration(builder: MarkwonConfiguration.Builder) {
          builder.imageSizeResolver(DensityImageSizeResolver())
        }
      })
      .usePlugin(ImagesPlugin.create())
      .usePlugin(HtmlPlugin.create())
      .build()

    markwon.setMarkdown(textView, md)
  }

  class DensityImageSizeResolver : ImageSizeResolverDef() {

    val density: Float by lazy(LazyThreadSafetyMode.NONE) {
      Resources.getSystem().displayMetrics.density
    }

    override fun resolveAbsolute(dimension: ImageSize.Dimension, original: Int, textSize: Float): Int {
      if (dimension.unit == null) {
        // assume density pixels
        return (dimension.value * density + 0.5F).toInt()
      }
      return super.resolveAbsolute(dimension, original, textSize)
    }
  }
}