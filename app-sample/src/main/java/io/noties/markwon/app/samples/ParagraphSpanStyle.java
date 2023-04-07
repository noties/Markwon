package io.noties.markwon.app.samples;

import android.graphics.Color;
import android.text.style.ForegroundColorSpan;

import androidx.annotation.NonNull;

import com.vladsch.flexmark.ast.Paragraph;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.MarkwonSpansFactory;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;
import io.noties.markwon.sample.annotations.Tag;

@MarkwonSampleInfo(
  id = "20200629122647",
  title = "Paragraph style",
  description = "Apply a style (via span) to a paragraph",
  artifacts = {MarkwonArtifact.CORE},
  tags = {Tag.paragraph, Tag.style, Tag.span}
)
public class ParagraphSpanStyle extends MarkwonTextViewSample {
  @Override
  public void render() {
    final String md = "# Hello!\n\nA paragraph?\n\nIt should be!";

    final Markwon markwon = Markwon.builder(context)
      .usePlugin(new AbstractMarkwonPlugin() {
        @Override
        public void configureSpansFactory(@NonNull MarkwonSpansFactory.Builder builder) {
          // apply a span to a Paragraph
          builder.setFactory(Paragraph.class, (configuration, props) ->
            new ForegroundColorSpan(Color.GREEN));
        }
      })
      .build();

    markwon.setMarkdown(textView, md);
  }
}
