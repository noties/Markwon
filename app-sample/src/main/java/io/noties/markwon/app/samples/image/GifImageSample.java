package io.noties.markwon.app.samples.image;

import io.noties.markwon.Markwon;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.image.ImagesPlugin;
import io.noties.markwon.image.gif.GifMediaDecoder;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;
import io.noties.markwon.sample.annotations.Tag;

@MarkwonSampleInfo(
  id = "20200630162214",
  title = "GIF image",
  artifacts = MarkwonArtifact.IMAGE,
  tags = {Tag.image, Tag.gif}
)
public class GifImageSample extends MarkwonTextViewSample {
  @Override
  public void render() {
    final String md = "" +
      "![gif-image](https://upload.wikimedia.org/wikipedia/commons/2/2c/Rotating_earth_%28large%29.gif)";

    final Markwon markwon = Markwon.builder(context)
      // GIF is handled by default if library is used in the app
//      .usePlugin(ImagesPlugin.create())
      .usePlugin(ImagesPlugin.create(plugin -> {
        plugin.addMediaDecoder(GifMediaDecoder.create());
      }))
      .build();

    markwon.setMarkdown(textView, md);
  }
}
