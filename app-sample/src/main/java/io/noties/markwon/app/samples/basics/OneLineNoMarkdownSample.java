package io.noties.markwon.app.samples.basics;

import io.noties.markwon.Markwon;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;
import io.noties.markwon.sample.annotations.Tag;

@MarkwonSampleInfo(
  id = "20210118154116",
  title = "One line text",
  description = "Single line text without markdown markup",
  artifacts = MarkwonArtifact.CORE,
  tags = Tag.rendering
)
public class OneLineNoMarkdownSample extends MarkwonTextViewSample {
  @Override
  public void render() {

    textView.setBackgroundColor(0x40ff0000);

    final String md = " Demo text ";

    final Markwon markwon = Markwon.builder(context)
      .build();

    markwon.setMarkdown(textView, md);
  }
}
