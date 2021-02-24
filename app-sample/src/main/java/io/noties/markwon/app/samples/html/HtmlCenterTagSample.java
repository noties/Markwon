package io.noties.markwon.app.samples.html;

import android.text.Layout;
import android.text.style.AlignmentSpan;

import androidx.annotation.NonNull;

import java.util.Collection;
import java.util.Collections;

import io.noties.debug.Debug;
import io.noties.markwon.Markwon;
import io.noties.markwon.MarkwonVisitor;
import io.noties.markwon.SpannableBuilder;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.app.samples.html.shared.IFrameHtmlPlugin;
import io.noties.markwon.html.HtmlPlugin;
import io.noties.markwon.html.HtmlTag;
import io.noties.markwon.html.MarkwonHtmlRenderer;
import io.noties.markwon.html.TagHandler;
import io.noties.markwon.image.ImagesPlugin;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;
import io.noties.markwon.sample.annotations.Tag;

@MarkwonSampleInfo(
  id = "20200630120101",
  title = "Center HTML tag",
  description = "Handling of `center` HTML tag",
  artifacts = {MarkwonArtifact.HTML, MarkwonArtifact.IMAGE},
  tags = {Tag.rendering, Tag.html}
)
public class HtmlCenterTagSample extends MarkwonTextViewSample {
  @Override
  public void render() {
    final String html = "<html>\n" +
      "\n" +
      "<head></head>\n" +
      "\n" +
      "<body>\n" +
      "    <p></p>\n" +
      "    <h3>LiSA's Sword Art Online: Alicization OP Song \"ADAMAS\" Certified Platinum with 250,000 Downloads</h3>\n" +
      "    <p></p>\n" +
      "    <h5>The upper tune was already certified Gold one month after its digital release</h5>\n" +
      "    <p>According to The Recording Industry Association of Japan (RIAJ)'s monthly report for April 2020, one of the <span\n" +
      "            style=\"color: #ff9900;\"><strong><a href=\"http://www.lxixsxa.com/\" target=\"_blank\"><span\n" +
      "                        style=\"color: #ff9900;\">LiSA</span></a></strong></span>'s 14th single songs,\n" +
      "        <strong>\"ADAMAS\"</strong>&nbsp;(the first OP theme for the TV anime <a href=\"/sword-art-online\"\n" +
      "            target=\"_blank\"><span style=\"color: #ff9900;\"><strong><em>Sword Art Online:\n" +
      "                        Alicization</em></strong></span></a>) has been certified <strong>Platinum</strong> for\n" +
      "        surpassing 250,000 downloads.</p>\n" +
      "    <p>&nbsp;</p>\n" +
      "    <p>As a double A-side single with <strong>\"Akai Wana (who loves it?),\"</strong> <strong>\"ADAMAS\"</strong> was\n" +
      "        released from SACRA Music in Japan on December 12, 2018. Its CD single ranked second in Oricon's weekly single\n" +
      "        chart by selling 35,000 copies in its first week. Meanwhile, the song was released digitally two months prior to\n" +
      "        its CD release, October 8, then reached Gold (100,000 downloads) in the following month.</p>\n" +
      "    <p>&nbsp;</p>\n" +
      "    <p>&nbsp;</p>\n" +
      "    <center>\n" +
      "        <p><strong>\"ADAMAS\"</strong> MV YouTube EDIT ver.:</p>\n" +
      "        <p><iframe src=\"https://www.youtube.com/embed/UeEIl4JlE-g\" frameborder=\"0\" width=\"640\" height=\"360\"></iframe>\n" +
      "        </p>\n" +
      "        <p>&nbsp;</p>\n" +
      "        <p>Standard edition CD jacket:</p>\n" +
      "        <p><img src=\"https://img1.ak.crunchyroll.com/i/spire2/d7b1d6bc7563224388ef5ffc04a967581589950464_full.jpg\"\n" +
      "                alt=\"\" width=\"640\" height=\"635\"></p>\n" +
      "    </center>\n" +
      "    <p>&nbsp;&nbsp;</p>\n" +
      "    <hr>\n" +
      "    <p>&nbsp;</p>\n" +
      "    <p>Source: RIAJ press release</p>\n" +
      "    <p>&nbsp;</p>\n" +
      "    <p><em>©SACRA MUSIC</em></p>\n" +
      "    <p>&nbsp;</p>\n" +
      "    <p style=\"text-align: center;\"><a href=\"https://got.cr/PremiumTrial-NewsBanner4\"><em><img\n" +
      "                    src=\"https://img1.ak.crunchyroll.com/i/spire4/78f5441d927cf160a93e037b567c2b1f1559091520_full.png\"\n" +
      "                    alt=\"\" width=\"640\" height=\"43\"></em></a></p>\n" +
      "</body>\n" +
      "\n" +
      "</html>";

    final Markwon markwon = Markwon.builder(context)
      .usePlugin(HtmlPlugin.create(plugin ->
        plugin.addHandler(new CenterTagHandler())))
      .usePlugin(new IFrameHtmlPlugin())
      .usePlugin(ImagesPlugin.create())
      .build();

    markwon.setMarkdown(textView, html);
  }
}

class CenterTagHandler extends TagHandler {

  @Override
  public void handle(@NonNull MarkwonVisitor visitor, @NonNull MarkwonHtmlRenderer renderer, @NonNull HtmlTag tag) {
    Debug.e("center, isBlock: %s", tag.isBlock());
    if (tag.isBlock()) {
      visitChildren(visitor, renderer, tag.getAsBlock());
    }
    SpannableBuilder.setSpans(
      visitor.builder(),
      new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
      tag.start(),
      tag.end()
    );
  }

  @NonNull
  @Override
  public Collection<String> supportedTags() {
    return Collections.singleton("center");
  }
}

