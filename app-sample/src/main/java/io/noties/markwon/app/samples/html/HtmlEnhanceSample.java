package io.noties.markwon.app.samples.html;

import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;

import java.util.Collection;
import java.util.Collections;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.MarkwonVisitor;
import io.noties.markwon.app.sample.Tags;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.html.HtmlPlugin;
import io.noties.markwon.html.HtmlTag;
import io.noties.markwon.html.MarkwonHtmlRenderer;
import io.noties.markwon.html.TagHandler;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;

@MarkwonSampleInfo(
  id = "20200630115103",
  title = "Enhance custom HTML tag",
  description = "Custom HTML tag implementation " +
    "that _enhances_ a part of text given start and end indices",
  artifacts = MarkwonArtifact.HTML,
  tags = {Tags.rendering, Tags.span, Tags.html}
)
public class HtmlEnhanceSample extends MarkwonTextViewSample {
  @Override
  public void render() {
    final String md = "" +
      "<enhance start=\"5\" end=\"12\">This is text that must be enhanced, at least a part of it</enhance>";


    final Markwon markwon = Markwon.builder(context)
      .usePlugin(HtmlPlugin.create())
      .usePlugin(new AbstractMarkwonPlugin() {
        @Override
        public void configure(@NonNull Registry registry) {
          registry.require(HtmlPlugin.class, htmlPlugin -> htmlPlugin
            .addHandler(new EnhanceTagHandler((int) (textView.getTextSize() * 2 + .05F))));
        }
      })
      .build();

    markwon.setMarkdown(textView, md);
  }
}

class EnhanceTagHandler extends TagHandler {

  private final int enhanceTextSize;

  EnhanceTagHandler(@Px int enhanceTextSize) {
    this.enhanceTextSize = enhanceTextSize;
  }

  @Override
  public void handle(
    @NonNull MarkwonVisitor visitor,
    @NonNull MarkwonHtmlRenderer renderer,
    @NonNull HtmlTag tag) {

    // we require start and end to be present
    final int start = parsePosition(tag.attributes().get("start"));
    final int end = parsePosition(tag.attributes().get("end"));

    if (start > -1 && end > -1) {
      visitor.builder().setSpan(
        new AbsoluteSizeSpan(enhanceTextSize),
        tag.start() + start,
        tag.start() + end
      );
    }
  }

  @NonNull
  @Override
  public Collection<String> supportedTags() {
    return Collections.singleton("enhance");
  }

  private static int parsePosition(@Nullable String value) {
    int position;
    if (!TextUtils.isEmpty(value)) {
      try {
        position = Integer.parseInt(value);
      } catch (NumberFormatException e) {
        e.printStackTrace();
        position = -1;
      }
    } else {
      position = -1;
    }
    return position;
  }
}
