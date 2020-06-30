package io.noties.markwon.app.samples.image;

import io.noties.markwon.Markwon;
import io.noties.markwon.app.sample.Tags;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.image.ImagesPlugin;
import io.noties.markwon.image.svg.SvgPictureMediaDecoder;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;

@MarkwonSampleInfo(
  id = "202006182161952",
  title = "SVG image",
  artifacts = MarkwonArtifact.IMAGE,
  tags = {Tags.image, Tags.svg}
)
public class SvgImageSample extends MarkwonTextViewSample {
  @Override
  public void render() {
    final String md = "" +
      "![svg-image](https://github.com/dcurtis/markdown-mark/raw/master/svg/markdown-mark-solid.svg)";

    final Markwon markwon = Markwon.builder(context)
      // SVG and GIF are automatically handled if required
      //  libraries are in path (specified in dependencies block)
//      .usePlugin(ImagesPlugin.create())
      // let's make it implicit
      .usePlugin(ImagesPlugin.create(plugin ->
        // there 2 svg media decoders:
        // - regular `SvgMediaDecoder`
        // - special one when SVG doesn't have width and height specified - `SvgPictureMediaDecoder`
        plugin.addMediaDecoder(SvgPictureMediaDecoder.create())))
      .build();

    markwon.setMarkdown(textView, md);
  }
}
