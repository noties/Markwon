package io.noties.markwon.app.samples.html;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.noties.markwon.Markwon;
import io.noties.markwon.app.sample.Tags;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.html.HtmlEmptyTagReplacement;
import io.noties.markwon.html.HtmlPlugin;
import io.noties.markwon.html.HtmlTag;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;

@MarkwonSampleInfo(
  id = "20200630115725",
  title = "HTML empty tag replacement",
  description = "Render custom content when HTML tag contents is empty, " +
    "in case of self-closed HTML tags or tags without content (closed " +
    "right after opened)",
  artifacts = MarkwonArtifact.HTML,
  tags = {Tags.rendering, Tags.html}
)
public class HtmlEmptyTagReplacementSample extends MarkwonTextViewSample {
  @Override
  public void render() {
    final String md = "" +
      "<empty></empty> the `<empty></empty>` is replaced?";

    final Markwon markwon = Markwon.builder(context)
      .usePlugin(HtmlPlugin.create(plugin -> {
        plugin.emptyTagReplacement(new HtmlEmptyTagReplacement() {
          @Nullable
          @Override
          public String replace(@NonNull HtmlTag tag) {
            if ("empty".equals(tag.name())) {
              return "REPLACED_EMPTY_WITH_IT";
            }
            return super.replace(tag);
          }
        });
      }))
      .build();

    markwon.setMarkdown(textView, md);
  }
}
