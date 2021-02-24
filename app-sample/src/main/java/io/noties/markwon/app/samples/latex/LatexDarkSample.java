package io.noties.markwon.app.samples.latex;

import io.noties.markwon.Markwon;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.ext.latex.JLatexMathPlugin;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;
import io.noties.markwon.sample.annotations.Tag;

@MarkwonSampleInfo(
  id = "20200701094225",
  title = "LaTeX dark",
  description = "LaTeX automatically uses `TextView` text color " +
    "if not configured explicitly",
  artifacts = MarkwonArtifact.EXT_LATEX,
  tags = Tag.rendering
)
public class LatexDarkSample extends MarkwonTextViewSample {
  @Override
  public void render() {
    scrollView.setBackgroundColor(0xFF000000);
    textView.setTextColor(0xFFffffff);

    final String md = "" +
      "# LaTeX\n" +
      "$$\n" +
      "\\int \\frac{1}{x} dx = \\ln \\left| x \\right| + C\n" +
      "$$\n" +
      "text color is taken from text";

    final Markwon markwon = Markwon.builder(context)
      .usePlugin(JLatexMathPlugin.create(textView.getTextSize()))
      .build();

    markwon.setMarkdown(textView, md);
  }
}
