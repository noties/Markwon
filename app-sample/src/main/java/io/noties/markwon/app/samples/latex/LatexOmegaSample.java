package io.noties.markwon.app.samples.latex;

import io.noties.markwon.Markwon;
import io.noties.markwon.app.sample.Tags;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.ext.latex.JLatexMathPlugin;
import io.noties.markwon.inlineparser.MarkwonInlineParserPlugin;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;

@MarkwonSampleInfo(
  id = "202007183090618",
  title = "LaTeX omega symbol",
  description = "Bug rendering omega symbol in LaTeX",
  artifacts = {MarkwonArtifact.EXT_LATEX, MarkwonArtifact.INLINE_PARSER},
  tags = {Tags.rendering, Tags.knownBug}
)
public class LatexOmegaSample extends MarkwonTextViewSample {
  @Override
  public void render() {
    final String md = "" +
      "# Block\n\n" +
      "$$\n" +
      "\\Omega\n" +
      "$$\n\n" +
      "# Inline\n\n" +
      "$$\\Omega$$";

    final Markwon markwon = Markwon.builder(context)
      .usePlugin(MarkwonInlineParserPlugin.create())
      .usePlugin(JLatexMathPlugin.create(textView.getTextSize(), builder -> {
        builder.inlinesEnabled(true);
      }))
      .build();

    markwon.setMarkdown(textView, md);
  }
}
