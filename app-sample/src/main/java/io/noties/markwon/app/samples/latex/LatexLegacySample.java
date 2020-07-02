package io.noties.markwon.app.samples.latex;

import io.noties.markwon.Markwon;
import io.noties.markwon.app.sample.Tags;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.app.samples.latex.shared.LatexHolder;
import io.noties.markwon.ext.latex.JLatexMathPlugin;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;

@MarkwonSampleInfo(
  id = "202007183090335",
  title = "LaTeX blocks in legacy mode",
  description = "Sample using _legacy_ LaTeX block parsing (pre `4.3.0` Markwon version)",
  artifacts = MarkwonArtifact.EXT_LATEX,
  tags = Tags.rendering
)
public class LatexLegacySample extends MarkwonTextViewSample {
  @Override
  public void render() {
    final String md = "" +
      "# LaTeX legacy\n" +
      "There are no inlines in previous versions, only blocks:\n" +
      "$$\n" +
      "" + LatexHolder.LATEX_BOXES + "\n" +
      "$$\n" +
      "yeah";

    final Markwon markwon = Markwon.builder(context)
      .usePlugin(JLatexMathPlugin.create(textView.getTextSize(), builder -> {
        builder.blocksLegacy(true);
      }))
      .build();

    markwon.setMarkdown(textView, md);
  }
}
