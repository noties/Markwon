package io.noties.markwon.app.samples.latex;

import io.noties.markwon.Markwon;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.app.samples.latex.shared.LatexHolder;
import io.noties.markwon.ext.latex.JLatexMathPlugin;
import io.noties.markwon.inlineparser.MarkwonInlineParserPlugin;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;
import io.noties.markwon.sample.annotations.Tag;

@MarkwonSampleInfo(
  id = "20200701093504",
  title = "LaTeX inline/block different text size",
  artifacts = {MarkwonArtifact.EXT_LATEX, MarkwonArtifact.INLINE_PARSER},
  tags = {Tag.rendering}
)
public class LatexDifferentTextSizesSample extends MarkwonTextViewSample {
  @Override
  public void render() {
    final String md = "" +
      "# LaTeX different text sizes\n" +
      "inline: " + LatexHolder.LATEX_BANGLE + ", okay and block:\n" +
      "$$\n" +
      "" + LatexHolder.LATEX_BANGLE + "\n" +
      "$$\n" +
      "that's it";

    final Markwon markwon = Markwon.builder(context)
      .usePlugin(MarkwonInlineParserPlugin.create())
      .usePlugin(JLatexMathPlugin.create(
        textView.getTextSize() * 0.75F,
        textView.getTextSize() * 1.50F,
        builder -> {
          builder.inlinesEnabled(true);
        }
      ))
      .build();

    markwon.setMarkdown(textView, md);
  }
}
