package io.noties.markwon.app.samples.html;

import io.noties.markwon.Markwon;
import io.noties.markwon.app.sample.Tags;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.html.HtmlPlugin;
import io.noties.markwon.image.ImagesPlugin;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;

@MarkwonSampleInfo(
  id = "202006182115300",
  title = "Html images",
  description = "Usage of HTML images",
  artifacts = {MarkwonArtifact.HTML, MarkwonArtifact.IMAGE},
  tags = {Tags.image, Tags.rendering, Tags.html}
)
public class HtmlImageSample extends MarkwonTextViewSample {
  @Override
  public void render() {
    // treat unclosed/void `img` tag as HTML inline
    final String md = "" +
      "## Try CommonMark\n" +
      "\n" +
      "Markwon IMG:\n" +
      "\n" +
      "![](https://upload.wikimedia.org/wikipedia/it/thumb/c/c5/GTA_2.JPG/220px-GTA_2.JPG)\n" +
      "\n" +
      "New lines...\n" +
      "\n" +
      "HTML IMG:\n" +
      "\n" +
      "<img src=\"https://upload.wikimedia.org/wikipedia/it/thumb/c/c5/GTA_2.JPG/220px-GTA_2.JPG\"></img>\n" +
      "\n" +
      "New lines\n\n";

    final Markwon markwon = Markwon.builder(context)
      .usePlugin(ImagesPlugin.create())
      .usePlugin(HtmlPlugin.create())
      .build();

    markwon.setMarkdown(textView, md);
  }
}
