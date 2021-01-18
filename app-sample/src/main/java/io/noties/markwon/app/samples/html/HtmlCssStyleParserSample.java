package io.noties.markwon.app.samples.html;

import android.graphics.Color;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import io.noties.debug.Debug;
import io.noties.markwon.Markwon;
import io.noties.markwon.MarkwonConfiguration;
import io.noties.markwon.RenderProps;
import io.noties.markwon.app.sample.Tags;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.html.CssInlineStyleParser;
import io.noties.markwon.html.CssProperty;
import io.noties.markwon.html.HtmlPlugin;
import io.noties.markwon.html.HtmlTag;
import io.noties.markwon.html.tag.SimpleTagHandler;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;

@MarkwonSampleInfo(
  id = "20210118155530",
  title = "CSS attributes in HTML",
  description = "Parse CSS attributes of HTML tags with `CssInlineStyleParser`",
  artifacts = MarkwonArtifact.HTML,
  tags = Tags.html
)
public class HtmlCssStyleParserSample extends MarkwonTextViewSample {
  @Override
  public void render() {

    final String md = "# CSS\n\n" +
      "<span style=\"background-color: #ff0000;\">this has red background</span> and then\n\n" +
      "this <span style=\"color: #00ff00;\">is green</span>";

    final Markwon markwon = Markwon.builder(context)
      .usePlugin(HtmlPlugin.create(plugin -> plugin.addHandler(new SpanTagHandler())))
      .build();

    markwon.setMarkdown(textView, md);
  }

  private static class SpanTagHandler extends SimpleTagHandler {

    @Nullable
    @Override
    public Object getSpans(@NonNull MarkwonConfiguration configuration, @NonNull RenderProps renderProps, @NonNull HtmlTag tag) {
      final String style = tag.attributes().get("style");
      if (TextUtils.isEmpty(style)) {
        return null;
      }

      int color = 0;
      int backgroundColor = 0;

      for (CssProperty property : CssInlineStyleParser.create().parse(style)) {
        switch (property.key()) {

          case "color":
            color = Color.parseColor(property.value());
            break;

          case "background-color":
            backgroundColor = Color.parseColor(property.value());
            break;

          default:
            Debug.i("unexpected CSS property: %s", property);
        }
      }

      final List<Object> spans = new ArrayList<>(3);

      if (color != 0) {
        spans.add(new ForegroundColorSpan(color));
      }
      if (backgroundColor != 0) {
        spans.add(new BackgroundColorSpan(backgroundColor));
      }

      return spans.toArray();
    }

    @NonNull
    @Override
    public Collection<String> supportedTags() {
      return Collections.singleton("span");
    }
  }
}
