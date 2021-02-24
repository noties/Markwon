package io.noties.markwon.app.samples;

import io.noties.markwon.Markwon;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;
import io.noties.markwon.sample.annotations.Tag;

@MarkwonSampleInfo(
  id = "20200826122247",
  title = "Deeplinks",
  description = "Handling of deeplinks (app handles https scheme to deep link into content)",
  artifacts = MarkwonArtifact.CORE,
  tags = Tag.links
)
public class DeeplinksSample extends MarkwonTextViewSample {
  @Override
  public void render() {
    final String md = "" +
      "# Deeplinks\n\n" +
      "The [link](https://noties.io/Markwon/app/sample/20200826122247) to self";

    // nothing special is required
    final Markwon markwon = Markwon.builder(context)
      .build();

    markwon.setMarkdown(textView, md);
  }
}
