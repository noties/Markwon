package io.noties.markwon.app.samples;

import android.graphics.Color;
import android.text.style.ForegroundColorSpan;

import io.noties.markwon.Markwon;
import io.noties.markwon.app.sample.Tags;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.core.spans.EmphasisSpan;
import io.noties.markwon.core.spans.StrongEmphasisSpan;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;
import io.noties.markwon.simple.ext.SimpleExtPlugin;

@MarkwonSampleInfo(
  id = "20200630194335",
  title = "Delimiter processor simple-ext",
  description = "Custom delimiter processor implemented with a `SimpleExtPlugin`",
  artifacts = MarkwonArtifact.SIMPLE_EXT,
  tags = Tags.parsing
)
public class SimpleExtensionSample extends MarkwonTextViewSample {
  @Override
  public void render() {
    final String md = "" +
      "# SimpleExt\n" +
      "\n" +
      "+let's start with `+`, ??then we can use this, and finally @@this$$??+";

    // NB! we cannot have multiple delimiter processor with the same character
    //  (even if lengths are different)

    final Markwon markwon = Markwon.builder(context)
      .usePlugin(SimpleExtPlugin.create(plugin -> {
        plugin
          .addExtension(1, '+', (configuration, props) -> new EmphasisSpan())
          .addExtension(2, '?', (configuration, props) -> new StrongEmphasisSpan())
          .addExtension(
            2,
            '@',
            '$',
            (configuration, props) -> new ForegroundColorSpan(Color.RED)
          );
      }))
      .build();

    markwon.setMarkdown(textView, md);
  }
}
