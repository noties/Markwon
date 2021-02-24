package io.noties.markwon.app.samples;

import java.util.concurrent.Executors;

import io.noties.markwon.Markwon;
import io.noties.markwon.PrecomputedTextSetterCompat;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;
import io.noties.markwon.sample.annotations.Tag;

@MarkwonSampleInfo(
  id = "20200702091654",
  title = "PrecomputedTextSetterCompat",
  description = "`TextSetter` to use `PrecomputedTextSetterCompat`",
  artifacts = MarkwonArtifact.CORE,
  tags = Tag.precomputedText
)
public class PrecomputedSample extends MarkwonTextViewSample {
  @Override
  public void render() {
    final String md = "" +
      "# Heading\n" +
      "**bold** some precomputed spans via `PrecomputedTextSetterCompat`";

    final Markwon markwon = Markwon.builder(context)
      .textSetter(PrecomputedTextSetterCompat.create(Executors.newCachedThreadPool()))
      .build();

    markwon.setMarkdown(textView, md);
  }
}
