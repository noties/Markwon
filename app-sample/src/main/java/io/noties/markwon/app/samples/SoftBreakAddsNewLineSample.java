package io.noties.markwon.app.samples;

import io.noties.markwon.Markwon;
import io.noties.markwon.SoftBreakAddsNewLinePlugin;
import io.noties.markwon.app.sample.Tags;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;

@MarkwonSampleInfo(
  id = "20200629125040",
  title = "Soft break new line",
  description = "Add a new line for a markdown soft-break node",
  artifacts = MarkwonArtifact.CORE,
  tags = {Tags.newLine, Tags.softBreak}
)
public class SoftBreakAddsNewLineSample extends MarkwonTextViewSample {
  @Override
  public void render() {
    final String md = "" +
      "Hello there ->(line)\n(break)<- going on and on";

    final Markwon markwon = Markwon.builder(context)
      .usePlugin(SoftBreakAddsNewLinePlugin.create())
      .build();

    markwon.setMarkdown(textView, md);
  }
}
