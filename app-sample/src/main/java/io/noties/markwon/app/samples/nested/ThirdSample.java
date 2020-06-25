package io.noties.markwon.app.samples.nested;

import io.noties.markwon.Markwon;
import io.noties.markwon.app.ui.MarkwonTextViewSample;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;

@MarkwonSampleInfo(
  id = "202006177155827",
  title = "Third sample",
  description = "> yo! \n\n```\nfinal int i = 0;\n```",
  artifacts = {MarkwonArtifact.SIMPLE_EXT, MarkwonArtifact.EDITOR},
  tags = {"a", "c", "test"}
)
public class ThirdSample extends MarkwonTextViewSample {
  @Override
  public void render() {
    final String md = "# Hello!";
    final Markwon markwon = Markwon.create(context);
    markwon.setMarkdown(textView, md);
  }
}
