package io.noties.markwon.app.samples;

import io.noties.markwon.Markwon;
import io.noties.markwon.app.sample.Tags;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;

@MarkwonSampleInfo(
  id = "202006181124005",
  title = "Links without scheme",
  description = "Links without scheme are considered to be `https`",
  artifacts = {MarkwonArtifact.CORE},
  tags = {Tags.links, Tags.defaults}
)
public class LinkWithoutSchemeSample extends MarkwonTextViewSample {
  @Override
  public void render() {
    final String md = "" +
      "# Links without scheme\n" +
      "[a link without scheme](github.com) is considered to be `https`.\n" +
      "Override `LinkResolverDef` to change this functionality" +
      "";

    final Markwon markwon = Markwon.create(context);

    markwon.setMarkdown(textView, md);
  }
}
