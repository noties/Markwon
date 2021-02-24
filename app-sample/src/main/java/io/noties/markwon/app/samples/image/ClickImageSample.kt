package io.noties.markwon.app.samples.image

import android.view.View
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.LinkResolver
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonConfiguration
import io.noties.markwon.MarkwonSpansFactory
import io.noties.markwon.app.readme.GithubImageDestinationProcessor
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample
import io.noties.markwon.app.utils.loadReadMe
import io.noties.markwon.core.spans.LinkSpan
import io.noties.markwon.image.ImageProps
import io.noties.markwon.image.ImagesPlugin
import io.noties.markwon.sample.annotations.MarkwonArtifact
import io.noties.markwon.sample.annotations.MarkwonSampleInfo
import io.noties.markwon.sample.annotations.Tag
import org.commonmark.node.Image

@MarkwonSampleInfo(
  id = "20201221130230",
  title = "Click images",
  description = "Make _all_ images clickable (to open in a gallery, etc)",
  artifacts = [MarkwonArtifact.IMAGE],
  tags = [Tag.rendering, Tag.image]
)
class ClickImageSample : MarkwonTextViewSample() {
  override fun render() {

    val md = loadReadMe(context)

    // please note that if an image is already inside a link, original link would be overriden

    val markwon = Markwon.builder(context)
      .usePlugin(ImagesPlugin.create())
      .usePlugin(object : AbstractMarkwonPlugin() {
        override fun configureConfiguration(builder: MarkwonConfiguration.Builder) {
          builder.imageDestinationProcessor(GithubImageDestinationProcessor())
        }
      })
      .usePlugin(object : AbstractMarkwonPlugin() {
        override fun configureSpansFactory(builder: MarkwonSpansFactory.Builder) {
          builder.appendFactory(Image::class.java) { configuration, props ->

            // this is the destination of image, you can additionally process it
            val url = ImageProps.DESTINATION.require(props)

            LinkSpan(
              configuration.theme(),
              url,
              ImageLinkResolver(configuration.linkResolver())
            )
          }
        }
      })
      .build()

    markwon.setMarkdown(textView, md)
  }

  class ImageLinkResolver(val original: LinkResolver) : LinkResolver {
    override fun resolve(view: View, link: String) {
      // decide if you want to open gallery or anything else,
      //  here we just pass to original
      if (false) {
        // do your thing
      } else {
        // just use original
        original.resolve(view, link)
      }
    }
  }
}