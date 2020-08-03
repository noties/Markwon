package io.noties.markwon.app.samples.html;

import android.text.Layout;
import android.text.style.AlignmentSpan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collection;
import java.util.Collections;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.MarkwonConfiguration;
import io.noties.markwon.RenderProps;
import io.noties.markwon.app.sample.Tags;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.html.HtmlPlugin;
import io.noties.markwon.html.HtmlTag;
import io.noties.markwon.html.tag.SimpleTagHandler;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;

@MarkwonSampleInfo(
  id = "20200630114630",
  title = "Align HTML tag",
  description = "Implement custom HTML tag handling",
  artifacts = MarkwonArtifact.HTML,
  tags = {Tags.rendering, Tags.span, Tags.html}
)
public class HtmlAlignSample extends MarkwonTextViewSample {
  @Override
  public void render() {
    final String md = "" +
      "<align center>We are centered</align>\n" +
      "\n" +
      "<align end>We are at the end</align>\n" +
      "\n" +
      "<align>We should be at the start</align>\n" +
      "\n";


    final Markwon markwon = Markwon.builder(context)
      .usePlugin(HtmlPlugin.create())
      .usePlugin(new AbstractMarkwonPlugin() {
        @Override
        public void configure(@NonNull Registry registry) {
          registry.require(HtmlPlugin.class, htmlPlugin -> htmlPlugin
            .addHandler(new AlignTagHandler()));
        }
      })
      .build();

    markwon.setMarkdown(textView, md);
  }
}

class AlignTagHandler extends SimpleTagHandler {

  @Nullable
  @Override
  public Object getSpans(
    @NonNull MarkwonConfiguration configuration,
    @NonNull RenderProps renderProps,
    @NonNull HtmlTag tag) {

    final Layout.Alignment alignment;

    // html attribute without value, <align center></align>
    if (tag.attributes().containsKey("center")) {
      alignment = Layout.Alignment.ALIGN_CENTER;
    } else if (tag.attributes().containsKey("end")) {
      alignment = Layout.Alignment.ALIGN_OPPOSITE;
    } else {
      // empty value or any other will make regular alignment
      alignment = Layout.Alignment.ALIGN_NORMAL;
    }

    return new AlignmentSpan.Standard(alignment);
  }

  @NonNull
  @Override
  public Collection<String> supportedTags() {
    return Collections.singleton("align");
  }
}
