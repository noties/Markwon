package io.noties.markwon.app.samples;

import io.noties.markwon.Markwon;
import io.noties.markwon.app.sample.Tags;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;

@MarkwonSampleInfo(
  id = "20201203221806",
  title = "Ordered list numbers",
  artifacts = MarkwonArtifact.CORE,
  tags = Tags.rendering
)
public class OrderedListNumbersSample extends MarkwonTextViewSample {
  @Override
  public void render() {
    final String md = "# Ordered lists\n\n" +
      "1. hello there\n" +
      "1. hello there and much much more, this text just goes and goes, and should it stop, we' know it\n" +
      "1. okay, np\n" +
      "1. hello there\n" +
      "1. hello there and much much more, this text just goes and goes, and should it stop, we' know it\n" +
      "1. okay, np\n" +
      "1. hello there\n" +
      "1. hello there and much much more, this text just goes and goes, and should it stop, we' know it\n" +
      "1. okay, np\n" +
      "1. hello there\n" +
      "1. hello there and much much more, this text just goes and goes, and should it stop, we' know it\n" +
      "1. okay, np\n" +
      "1. hello there\n" +
      "1. hello there and much much more, this text just goes and goes, and should it stop, we' know it\n" +
      "1. okay, np\n" +
      "1. hello there\n" +
      "1. hello there and much much more, this text just goes and goes, and should it stop, we' know it\n" +
      "1. okay, np\n" +
      "1. hello there\n" +
      "1. hello there and much much more, this text just goes and goes, and should it stop, we' know it\n" +
      "1. okay, np\n" +
      "1. hello there\n" +
      "1. hello there and much much more, this text just goes and goes, and should it stop, we' know it\n" +
      "1. okay, np\n" +
      "";

    final Markwon markwon = Markwon.create(context);
    markwon.setMarkdown(textView, md);
  }
}
