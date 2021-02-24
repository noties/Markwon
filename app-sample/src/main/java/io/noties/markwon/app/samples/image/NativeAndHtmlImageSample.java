package io.noties.markwon.app.samples.image;

import androidx.annotation.NonNull;

import org.commonmark.node.Image;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.MarkwonSpansFactory;
import io.noties.markwon.RenderProps;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.html.HtmlPlugin;
import io.noties.markwon.image.AsyncDrawable;
import io.noties.markwon.image.AsyncDrawableSpan;
import io.noties.markwon.image.ImageProps;
import io.noties.markwon.image.ImageSize;
import io.noties.markwon.image.ImagesPlugin;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;
import io.noties.markwon.sample.annotations.Tag;

@MarkwonSampleInfo(
  id = "20200803115847",
  title = "Native and HTML image",
  description = "Define images in both native markdown and HTML. Native markdown images take 100% of available width",
  artifacts = {MarkwonArtifact.IMAGE, MarkwonArtifact.HTML},
  tags = {Tag.rendering, Tag.image, Tag.html}
)
public class NativeAndHtmlImageSample extends MarkwonTextViewSample {
  @Override
  public void render() {
    final String md = "" +
      "# Native image\n" +
      "![alt](https://picsum.photos/id/237/1024/800)\n\n" +
      "# HTML image\n" +
      "<img src=\"https://picsum.photos/id/237/1024/800\" width=\"100%\" height=\"auto\"></img>" +
      "";

    final Markwon markwon = Markwon.builder(context)
      .usePlugin(ImagesPlugin.create())
      .usePlugin(HtmlPlugin.create())
      .usePlugin(new AbstractMarkwonPlugin() {
        @Override
        public void configureSpansFactory(@NonNull MarkwonSpansFactory.Builder builder) {
          builder.setFactory(Image.class, (configuration, props) -> new AsyncDrawableSpan(
            configuration.theme(),
            new AsyncDrawable(
              ImageProps.DESTINATION.require(props),
              configuration.asyncDrawableLoader(),
              configuration.imageSizeResolver(),
              imageSize(props)
            ),
            AsyncDrawableSpan.ALIGN_BOTTOM,
            ImageProps.REPLACEMENT_TEXT_IS_LINK.get(props, false)
          ));
        }
      })
      .build();

    markwon.setMarkdown(textView, md);
  }

  // Use defined image size or make its width 100%
  @NonNull
  private static ImageSize imageSize(@NonNull RenderProps props) {
    final ImageSize imageSize = ImageProps.IMAGE_SIZE.get(props);
    if (imageSize != null) {
      return imageSize;
    }
    return new ImageSize(
      new ImageSize.Dimension(100F, "%"),
      null
    );
  }
}
