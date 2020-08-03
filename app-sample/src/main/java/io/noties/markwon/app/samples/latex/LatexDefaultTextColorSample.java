package io.noties.markwon.app.samples.latex;

import android.graphics.Color;

import io.noties.markwon.Markwon;
import io.noties.markwon.app.sample.Tags;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.app.samples.latex.shared.LatexHolder;
import io.noties.markwon.ext.latex.JLatexMathPlugin;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;

@MarkwonSampleInfo(
  id = "20200701120848",
  title = "LaTeX default text color",
  description = "LaTeX will use text color of `TextView` by default",
  artifacts = MarkwonArtifact.EXT_LATEX,
  tags = Tags.rendering
)
public class LatexDefaultTextColorSample extends MarkwonTextViewSample {
  @Override
  public void render() {
    // @since 4.3.0 text color is automatically taken from textView
    //  (if it's not specified explicitly via configuration)
    textView.setTextColor(Color.RED);

    final String md = "" +
      "# LaTeX default text color\n" +
      "$$\n" +
      "" + LatexHolder.LATEX_LONG_DIVISION + "\n" +
      "$$\n" +
      "";

    final Markwon markwon = Markwon.builder(context)
      .usePlugin(JLatexMathPlugin.create(textView.getTextSize()))
      .build();

    markwon.setMarkdown(textView, md);
  }
}
