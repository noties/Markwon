package io.noties.markwon.app.samples.latex;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import io.noties.markwon.Markwon;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.app.samples.latex.shared.LatexHolder;
import io.noties.markwon.ext.latex.JLatexMathPlugin;
import io.noties.markwon.ext.latex.JLatexMathTheme;
import io.noties.markwon.inlineparser.MarkwonInlineParserPlugin;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;
import io.noties.markwon.sample.annotations.Tag;

@MarkwonSampleInfo(
  id = "20200701121528",
  title = "LaTeX theme",
  description = "Sample of theme customization for LaTeX",
  artifacts = {MarkwonArtifact.EXT_LATEX, MarkwonArtifact.INLINE_PARSER},
  tags = Tag.rendering
)
public class LatexThemeSample extends MarkwonTextViewSample {
  @Override
  public void render() {
    final String md = "" +
      "# LaTeX theme\n" +
      "Hello there $$" + LatexHolder.LATEX_BANGLE + "$$, how was it?" +
      "Now, what about a _different_ approach and block:\n\n" +
      "$$\n" +
      "" + LatexHolder.LATEX_LONG_DIVISION + "\n" +
      "$$\n\n" +
      "Seems **fine**";

    final int blockPadding = (int) (16 * context.getResources().getDisplayMetrics().density + 0.5F);

    final Markwon markwon = Markwon.builder(context)
      .usePlugin(MarkwonInlineParserPlugin.create())
      .usePlugin(JLatexMathPlugin.create(textView.getTextSize(), builder -> {
        builder.inlinesEnabled(true);
        builder.theme()
          .inlineBackgroundProvider(() -> new ColorDrawable(0x200000ff))
          .inlineTextColor(Color.GREEN)
          .blockBackgroundProvider(() -> new ColorDrawable(0x2000ff00))
          .blockPadding(JLatexMathTheme.Padding.all(blockPadding))
          .blockTextColor(Color.RED)
        ;
      }))
      .build();

    markwon.setMarkdown(textView, md);
  }
}
