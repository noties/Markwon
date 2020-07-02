package io.noties.markwon.app.samples.table;

import io.noties.markwon.Markwon;
import io.noties.markwon.app.sample.Tags;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.ext.tables.TablePlugin;
import io.noties.markwon.image.ImagesPlugin;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;

@MarkwonSampleInfo(
  id = "202007184135932",
  title = "Images inside table",
  description = "Usage of images inside markdown tables",
  artifacts = {MarkwonArtifact.EXT_TABLES, MarkwonArtifact.IMAGE},
  tags = Tags.image
)
public class TableWithImagesSample extends MarkwonTextViewSample {
  @Override
  public void render() {
    final String md = "" +
      "| HEADER | HEADER |\n" +
      "|:----:|:----:|\n" +
      "| ![Build](https://github.com/noties/Markwon/workflows/Build/badge.svg) | Build |\n" +
      "| Stable | ![stable](https://img.shields.io/maven-central/v/io.noties.markwon/core.svg?label=stable) |\n" +
      "| BIG | ![image](https://images.pexels.com/photos/41171/brussels-sprouts-sprouts-cabbage-grocery-41171.jpeg) |\n" +
      "\n";

    final Markwon markwon = Markwon.builder(context)
      .usePlugin(ImagesPlugin.create())
      .usePlugin(TablePlugin.create(context))
      .build();

    markwon.setMarkdown(textView, md);
  }
}
