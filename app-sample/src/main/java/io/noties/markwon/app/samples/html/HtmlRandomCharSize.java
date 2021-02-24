package io.noties.markwon.app.samples.html;

import android.text.style.AbsoluteSizeSpan;

import androidx.annotation.NonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Random;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.MarkwonVisitor;
import io.noties.markwon.SpannableBuilder;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.html.HtmlPlugin;
import io.noties.markwon.html.HtmlTag;
import io.noties.markwon.html.MarkwonHtmlRenderer;
import io.noties.markwon.html.TagHandler;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;
import io.noties.markwon.sample.annotations.Tag;

@MarkwonSampleInfo(
  id = "20200630114923",
  title = "Random char size HTML tag",
  description = "Implementation of a custom HTML tag handler " +
    "that assigns each character a random size",
  artifacts = MarkwonArtifact.HTML,
  tags = {Tag.rendering, Tag.span, Tag.html}
)
public class HtmlRandomCharSize extends MarkwonTextViewSample {
  @Override
  public void render() {
    final String md = "" +
      "<random-char-size>\n" +
      "This message should have a jumpy feeling because of different sizes of characters\n" +
      "</random-char-size>\n\n";

    final Markwon markwon = Markwon.builder(context)
      .usePlugin(HtmlPlugin.create())
      .usePlugin(new AbstractMarkwonPlugin() {
        @Override
        public void configure(@NonNull Registry registry) {
          registry.require(HtmlPlugin.class, htmlPlugin -> htmlPlugin
            .addHandler(new RandomCharSize(new Random(42L), textView.getTextSize())));
        }
      })
      .build();

    markwon.setMarkdown(textView, md);
  }
}

class RandomCharSize extends TagHandler {

  private final Random random;
  private final float base;

  RandomCharSize(@NonNull Random random, float base) {
    this.random = random;
    this.base = base;
  }

  @Override
  public void handle(
    @NonNull MarkwonVisitor visitor,
    @NonNull MarkwonHtmlRenderer renderer,
    @NonNull HtmlTag tag) {

    final SpannableBuilder builder = visitor.builder();

    // text content is already added, we should only apply spans

    for (int i = tag.start(), end = tag.end(); i < end; i++) {
      final int size = (int) (base * (random.nextFloat() + 0.5F) + 0.5F);
      builder.setSpan(new AbsoluteSizeSpan(size, false), i, i + 1);
    }
  }

  @NonNull
  @Override
  public Collection<String> supportedTags() {
    return Collections.singleton("random-char-size");
  }
}
