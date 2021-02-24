package io.noties.markwon.app.samples.latex;

import io.noties.markwon.Markwon;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.app.samples.latex.shared.LatexHolder;
import io.noties.markwon.ext.latex.JLatexMathPlugin;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;
import io.noties.markwon.sample.annotations.Tag;

@MarkwonSampleInfo(
  id = "20200630200257",
  title = "LaTex block",
  description = "Render LaTeX block",
  artifacts = MarkwonArtifact.EXT_LATEX,
  tags = {Tag.rendering}
)
public class LatexBlockSample extends MarkwonTextViewSample {
  @Override
  public void render() {
    final String md = "" +
      "# LaTeX\n" +
      "$$\n" +
      "" + LatexHolder.LATEX_ARRAY + "\n" +
      "$$";

    final Markwon markwon = Markwon.builder(context)
      .usePlugin(JLatexMathPlugin.create(textView.getTextSize()))
      .build();

    markwon.setMarkdown(textView, md);
  }
}
