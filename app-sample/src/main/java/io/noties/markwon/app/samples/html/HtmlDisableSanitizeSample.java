package io.noties.markwon.app.samples.html;

import androidx.annotation.NonNull;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;
import io.noties.markwon.sample.annotations.Tag;

@MarkwonSampleInfo(
  id = "20200630171424",
  title = "Disable HTML",
  description = "Disable HTML via replacing special `<` and `>` symbols",
  artifacts = MarkwonArtifact.CORE,
  tags = {Tag.html, Tag.rendering, Tag.parsing, Tag.plugin}
)
public class HtmlDisableSanitizeSample extends MarkwonTextViewSample {
  @Override
  public void render() {
    final String md = "# Html <b>disabled</b>\n\n" +
      "<em>emphasis <strong>strong</strong>\n\n" +
      "<p>paragraph <img src='hey.jpg' /></p>\n\n" +
      "<test></test>\n\n" +
      "<test>";

    final Markwon markwon = Markwon.builder(context)
      .usePlugin(new AbstractMarkwonPlugin() {
        @NonNull
        @Override
        public String processMarkdown(@NonNull String markdown) {
          return markdown
            .replaceAll("<", "&lt;")
            .replaceAll(">", "&gt;");
        }
      })
      .build();

    markwon.setMarkdown(textView, md);
  }
}
