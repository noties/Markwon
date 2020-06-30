package io.noties.markwon.app.samples.image;

import io.noties.markwon.Markwon;
import io.noties.markwon.app.sample.Tags;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.image.glide.GlideImagesPlugin;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;

@MarkwonSampleInfo(
  id = "202006182170112",
  title = "Glide image",
  artifacts = MarkwonArtifact.IMAGE_GLIDE,
  tags = Tags.image
)
public class GlideImageSample extends MarkwonTextViewSample {
  @Override
  public void render() {
    final String md = "[![undefined](https://img.youtube.com/vi/gs1I8_m4AOM/0.jpg)](https://www.youtube.com/watch?v=gs1I8_m4AOM)";

    final Markwon markwon = Markwon.builder(context)
      .usePlugin(GlideImagesPlugin.create(context))
      .build();

    markwon.setMarkdown(textView, md);
  }
}
