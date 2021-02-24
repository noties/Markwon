package io.noties.markwon.app.samples.latex;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import io.noties.debug.Debug;
import io.noties.markwon.Markwon;
import io.noties.markwon.app.R;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.ext.latex.JLatexMathPlugin;
import io.noties.markwon.inlineparser.MarkwonInlineParserPlugin;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;
import io.noties.markwon.sample.annotations.Tag;

@MarkwonSampleInfo(
  id = "20200701122624",
  title = "LaTeX error handling",
  description = "Log error when parsing LaTeX and display error drawable",
  artifacts = MarkwonArtifact.EXT_LATEX,
  tags = Tag.rendering
)
public class LatexErrorSample extends MarkwonTextViewSample {
  @Override
  public void render() {
    final String md = "" +
      "# LaTeX with error\n" +
      "$$\n" +
      "\\sum_{i=0}^\\infty x \\cdot 0 \\rightarrow \\iMightNotExist{0}\n" +
      "$$\n\n" +
      "must **not** be rendered";

    final Markwon markwon = Markwon.builder(context)
      .usePlugin(MarkwonInlineParserPlugin.create())
      .usePlugin(JLatexMathPlugin.create(textView.getTextSize(), builder -> {
        builder.inlinesEnabled(true);
        //noinspection Convert2Lambda
        builder.errorHandler(new JLatexMathPlugin.ErrorHandler() {
          @Nullable
          @Override
          public Drawable handleError(@Nullable String latex, @NonNull Throwable error) {
            Debug.e(error, latex);
            return ContextCompat.getDrawable(context, R.drawable.ic_android_black_24dp);
          }
        });
      }))
      .build();

    markwon.setMarkdown(textView, md);
  }
}
