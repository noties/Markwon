package io.noties.markwon.app.samples.image;

import io.noties.markwon.Markwon;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.image.ImagesPlugin;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;
import io.noties.markwon.sample.annotations.Tag;

@MarkwonSampleInfo(
  id = "20200630144659",
  title = "Markdown image",
  artifacts = MarkwonArtifact.IMAGE,
  tags = Tag.image
)
public class ImageSample extends MarkwonTextViewSample {
  @Override
  public void render() {
    final String md = "" +
      "![image](https://github.com/dcurtis/markdown-mark/raw/master/png/208x128-solid.png)";

    final Markwon markwon = Markwon.builder(context)
      .usePlugin(ImagesPlugin.create())
      .build();

    markwon.setMarkdown(textView, md);
  }
}
