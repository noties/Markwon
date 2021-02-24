package io.noties.markwon.app.samples.image

import coil.ImageLoader
import coil.request.Disposable
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import io.noties.markwon.Markwon
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample
import io.noties.markwon.image.AsyncDrawable
import io.noties.markwon.image.coil.CoilImagesPlugin
import io.noties.markwon.sample.annotations.MarkwonArtifact
import io.noties.markwon.sample.annotations.MarkwonSampleInfo
import io.noties.markwon.sample.annotations.Tag

@MarkwonSampleInfo(
  id = "20200826101209",
  title = "Coil image",
  artifacts = [MarkwonArtifact.IMAGE_COIL],
  tags = [Tag.image]
)
class CoilImageSample : MarkwonTextViewSample() {
  override fun render() {
    val md = """
      # H1
      ## H2
      ### H3
      #### H4
      ##### H5
      
      > a quote
      
      + one
      - two
      * three
      
      1. one
      1. two
      1. three
      
      ---
      
      # Images
      
      ![img](https://picsum.photos/id/237/1024/800)
    """.trimIndent()

    // pick one
    val markwon = Markwon.builder(context)
//      .usePlugin(coilPlugin1)
//      .usePlugin(coilPlugin2)
      .usePlugin(coilPlugin3)
      .build()

    markwon.setMarkdown(textView, md)
  }

  val coilPlugin1: CoilImagesPlugin
    get() = CoilImagesPlugin.create(context)

  val coilPlugin2: CoilImagesPlugin
    get() = CoilImagesPlugin.create(context, imageLoader)

  val coilPlugin3: CoilImagesPlugin
    get() {
      val loader = imageLoader
      return CoilImagesPlugin.create(
        object : CoilImagesPlugin.CoilStore {
          override fun load(drawable: AsyncDrawable): ImageRequest {
            return ImageRequest.Builder(context)
              .defaults(loader.defaults)
              .data(drawable.destination)
              .crossfade(true)
              .transformations(CircleCropTransformation())
              .build()
          }

          override fun cancel(disposable: Disposable) {
            disposable.dispose()
          }
        },
        loader
      )
    }

  val imageLoader: ImageLoader
    get() = ImageLoader.Builder(context)
      .apply {
        availableMemoryPercentage(0.5)
        bitmapPoolPercentage(0.5)
        crossfade(true)
      }
      .build()
}