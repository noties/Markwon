package io.noties.markwon.app.samples;

import io.noties.markwon.Markwon;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;
import io.noties.markwon.sample.annotations.Tag;

@MarkwonSampleInfo(
  id = "20200629124706",
  title = "Soft break adds space",
  description = "By default a soft break (`\n`) will " +
    "add a space character instead of new line",
  artifacts = MarkwonArtifact.CORE,
  tags = {Tag.newLine, Tag.softBreak, Tag.defaults}
)
public class SoftBreakAddsSpace extends MarkwonTextViewSample {
  @Override
  public void render() {
    final String md = "" +
      "Hello there ->(line)\n(break)<- going on and on";

    // by default a soft break will add a space (instead of line break)
    final Markwon markwon = Markwon.create(context);

    markwon.setMarkdown(textView, md);
  }
}
