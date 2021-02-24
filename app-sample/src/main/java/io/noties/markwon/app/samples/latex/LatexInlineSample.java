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
  id = "20200701085820",
  title = "LaTeX inline",
  description = "Display LaTeX inline",
  artifacts = {MarkwonArtifact.EXT_LATEX, MarkwonArtifact.INLINE_PARSER},
  tags = Tag.rendering
)
public class LatexInlineSample extends MarkwonTextViewSample {
  @Override
  public void render() {
    final String md = "" +
      "# LaTeX inline\n" +
      "hey = $$" + LatexHolder.LATEX_BANGLE + "$$,\n" +
      "that's it!";

    // inlines must be explicitly enabled and require `MarkwonInlineParserPlugin`
    final Markwon markwon = Markwon.builder(context)
      .usePlugin(MarkwonInlineParserPlugin.create())
      .usePlugin(JLatexMathPlugin.create(textView.getTextSize(), builder -> {
        builder.inlinesEnabled(true);
      }))
      .build();

    markwon.setMarkdown(textView, md);
  }
}
