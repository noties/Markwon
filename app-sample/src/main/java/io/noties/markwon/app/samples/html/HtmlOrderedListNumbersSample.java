package io.noties.markwon.app.samples.html;

import io.noties.markwon.Markwon;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.html.HtmlPlugin;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;
import io.noties.markwon.sample.annotations.Tag;

@MarkwonSampleInfo(
  id = "20210201140502",
  title = "HTML Ordered list numbers",
  artifacts = MarkwonArtifact.HTML,
  tags = {Tag.rendering, Tag.html}
)
public class HtmlOrderedListNumbersSample extends MarkwonTextViewSample {
  @Override
  public void render() {
    final String md = "# HTML Ordered lists\n\n" +
      "<ol start=\"7\">" +
      "    <li>July</li>\n" +
      "    <li>August</li>\n" +
      "    <li>September</li>\n" +
      "    <li>October</li>\n" +
      "    <li>November</li>\n" +
      "    <li>December</li>\n" +
      "</ol>\n" +
      "";

    final Markwon markwon = Markwon.builder(context)
            .usePlugin(HtmlPlugin.create())
            .build();
    markwon.setMarkdown(textView, md);
  }
}
