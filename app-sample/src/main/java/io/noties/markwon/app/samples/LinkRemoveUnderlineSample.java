package io.noties.markwon.app.samples;

import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.text.style.UpdateAppearance;

import androidx.annotation.NonNull;

import org.commonmark.node.Link;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.MarkwonSpansFactory;
import io.noties.markwon.app.sample.Tags;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;

@MarkwonSampleInfo(
  id = "20200702101224",
  title = "Remove link underline",
  artifacts = MarkwonArtifact.CORE,
  tags = {Tags.links, Tags.rendering, Tags.span}
)
public class LinkRemoveUnderlineSample extends MarkwonTextViewSample {
  @Override
  public void render() {
    final String md = "" +
      "There are a lot of [links](#) [here](#)";

    final Markwon markwon = Markwon.builder(context)
      .usePlugin(new AbstractMarkwonPlugin() {
        @Override
        public void configureSpansFactory(@NonNull MarkwonSpansFactory.Builder builder) {
          builder.appendFactory(Link.class, (configuration, props) -> new RemoveUnderlineSpan());
        }
      })
      .build();

    markwon.setMarkdown(textView, md);
  }
}

class RemoveUnderlineSpan extends CharacterStyle implements UpdateAppearance {
  @Override
  public void updateDrawState(TextPaint tp) {
    tp.setUnderlineText(false);
  }
}
