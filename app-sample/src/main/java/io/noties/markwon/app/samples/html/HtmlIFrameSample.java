package io.noties.markwon.app.samples.html;

import io.noties.markwon.Markwon;
import io.noties.markwon.app.sample.Tags;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.app.samples.html.shared.IFrameHtmlPlugin;
import io.noties.markwon.html.HtmlPlugin;
import io.noties.markwon.image.ImagesPlugin;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;

@MarkwonSampleInfo(
  id = "202006182115521",
  title = "IFrame HTML tag",
  description = "Handling of `iframe` HTML tag",
  artifacts = {MarkwonArtifact.HTML, MarkwonArtifact.IMAGE},
  tags = {Tags.image, Tags.rendering, Tags.html}
)
public class HtmlIFrameSample extends MarkwonTextViewSample {
  @Override
  public void render() {
    final String md = "" +
      "# Hello iframe\n\n" +
      "<p class=\"p1\"><img title=\"JUMP FORCE\" src=\"https://img1.ak.crunchyroll.com/i/spire1/f0c009039dd9f8dff5907fff148adfca1587067000_full.jpg\" alt=\"JUMP FORCE\" width=\"640\" height=\"362\" /></p>\n" +
      "<p class=\"p2\">&nbsp;</p>\n" +
      "<p class=\"p1\">Switch owners will soon get to take part in the ultimate <em>Shonen Jump </em>rumble. Bandai Namco announced plans to bring <strong><em>Jump Force </em></strong>to <strong>Switch</strong> as <strong><em>Jump Force Deluxe Edition</em></strong>, with a release set for sometime this year. This version will include all of the original playable characters and those from Character Pass 1, and <strong>Character Pass 2 is also in the works </strong>for all versions, starting with <strong>Shoto Todoroki from </strong><span style=\"color: #ff9900;\"><a href=\"/my-hero-academia?utm_source=editorial_cr&amp;utm_medium=news&amp;utm_campaign=article_driven&amp;referrer=editorial_cr_news_article_driven\"><span style=\"color: #ff9900;\"><strong><em>My Hero Academia</em></strong></span></a></span>.</p>\n" +
      "<p class=\"p2\">&nbsp;</p>\n" +
      "<p class=\"p1\">Other than Todoroki, Bandai Namco hinted that the four other Character Pass 2 characters will hail from <span style=\"color: #ff9900;\"><a href=\"/hunter-x-hunter?utm_source=editorial_cr&amp;utm_medium=news&amp;utm_campaign=article_driven&amp;referrer=editorial_cr_news_article_driven\"><span style=\"color: #ff9900;\"><em>Hunter x Hunter</em></span></a></span>, <em>Yu Yu Hakusho</em>, <span style=\"color: #ff9900;\"><a href=\"/bleach?utm_source=editorial_cr&amp;utm_medium=news&amp;utm_campaign=article_driven&amp;referrer=editorial_cr_news_article_driven\"><span style=\"color: #ff9900;\"><em>Bleach</em></span></a></span>, and <span style=\"color: #ff9900;\"><a href=\"/jojos-bizarre-adventure?utm_source=editorial_cr&amp;utm_medium=news&amp;utm_campaign=article_driven&amp;referrer=editorial_cr_news_article_driven\"><span style=\"color: #ff9900;\"><em>JoJo's Bizarre Adventure</em></span></a></span>. Character Pass 2 will be priced at $17.99, and Todoroki launches this spring.<span class=\"Apple-converted-space\">&nbsp;</span></p>\n" +
      "<p class=\"p2\">&nbsp;</p>\n" +
      "<p class=\"p1\"><iframe style=\"display: block; margin-left: auto; margin-right: auto;\" src=\"https://www.youtube.com/embed/At1qTj-LWCc\" frameborder=\"0\" width=\"640\" height=\"360\"></iframe></p>\n" +
      "<p class=\"p2\">&nbsp;</p>\n" +
      "<p class=\"p1\">Character Pass 2 promo:</p>\n" +
      "<p class=\"p2\">&nbsp;</p>\n" +
      "<p class=\"p1\"><iframe style=\"display: block; margin-left: auto; margin-right: auto;\" src=\"https://www.youtube.com/embed/CukwN6kV4R4\" frameborder=\"0\" width=\"640\" height=\"360\"></iframe></p>\n" +
      "<p class=\"p2\">&nbsp;</p>\n" +
      "<p class=\"p1\"><a href=\"https://got.cr/PremiumTrial-NewsBanner4\"><img style=\"display: block; margin-left: auto; margin-right: auto;\" src=\"https://img1.ak.crunchyroll.com/i/spire4/78f5441d927cf160a93e037b567c2b1f1587067041_full.png\" alt=\"\" width=\"640\" height=\"43\" /></a></p>\n" +
      "<p class=\"p2\">&nbsp;</p>\n" +
      "<p class=\"p1\">-------</p>\n" +
      "<p class=\"p1\"><em>Joseph Luster is the Games and Web editor at </em><a href=\"http://www.otakuusamagazine.com/ME2/Default.asp\"><em>Otaku USA Magazine</em></a><em>. You can read his webcomic, </em><a href=\"http://subhumanzoids.com/comics/big-dumb-fighting-idiots/\">BIG DUMB FIGHTING IDIOTS</a><em> at </em><a href=\"http://subhumanzoids.com/\"><em>subhumanzoids</em></a><em>. Follow him on Twitter </em><a href=\"https://twitter.com/Moldilox\"><em>@Moldilox</em></a><em>.</em><span class=\"Apple-converted-space\">&nbsp;</span></p>";

    final Markwon markwon = Markwon.builder(context)
      .usePlugin(ImagesPlugin.create())
      .usePlugin(HtmlPlugin.create())
      .usePlugin(new IFrameHtmlPlugin())
      .build();

    markwon.setMarkdown(textView, md);
  }
}

