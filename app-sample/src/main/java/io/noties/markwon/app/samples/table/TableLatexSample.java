package io.noties.markwon.app.samples.table;

import io.noties.markwon.Markwon;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.ext.latex.JLatexMathPlugin;
import io.noties.markwon.ext.tables.TablePlugin;
import io.noties.markwon.image.ImagesPlugin;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;
import io.noties.markwon.sample.annotations.Tag;

@MarkwonSampleInfo(
  id = "20200702140041",
  title = "LaTeX inside table",
  description = "Usage of LaTeX formulas inside markdown tables",
  artifacts = {MarkwonArtifact.EXT_LATEX, MarkwonArtifact.EXT_TABLES, MarkwonArtifact.IMAGE},
  tags = {Tag.image}
)
public class TableLatexSample extends MarkwonTextViewSample {
  @Override
  public void render() {
    String latex = "\\begin{array}{cc}";
    latex += "\\fbox{\\text{A framed box with \\textdbend}}&\\shadowbox{\\text{A shadowed box}}\\cr";
    latex += "\\doublebox{\\text{A double framed box}}&\\ovalbox{\\text{An oval framed box}}\\cr";
    latex += "\\end{array}";

    final String md = "" +
      "| HEADER | HEADER |\n" +
      "|:----:|:----:|\n" +
      "| ![Build](https://github.com/noties/Markwon/workflows/Build/badge.svg) | Build |\n" +
      "| Stable | ![stable](https://img.shields.io/maven-central/v/io.noties.markwon/core.svg?label=stable) |\n" +
      "| BIG | $$" + latex + "$$ |\n" +
      "\n";

    final Markwon markwon = Markwon.builder(context)
      .usePlugin(ImagesPlugin.create())
      .usePlugin(JLatexMathPlugin.create(textView.getTextSize(), builder -> builder.inlinesEnabled(true)))
      .usePlugin(TablePlugin.create(context))
      .build();

    markwon.setMarkdown(textView, md);
  }
}
