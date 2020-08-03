package io.noties.markwon.app.samples;

import android.graphics.Color;

import androidx.annotation.NonNull;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.app.sample.Tags;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.core.MarkwonTheme;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;

@MarkwonSampleInfo(
  id = "20200629123617",
  title = "Customize theme",
  description = "Customize `MarkwonTheme` styling",
  artifacts = MarkwonArtifact.CORE,
  tags = {Tags.style, Tags.theme, Tags.plugin}
)
public class CustomizeThemeSample extends MarkwonTextViewSample {
  @Override
  public void render() {

    final String md = "`A code` that is rendered differently\n\n```\nHello!\n```";

    final Markwon markwon = Markwon.builder(context)
      .usePlugin(new AbstractMarkwonPlugin() {
        @Override
        public void configureTheme(@NonNull MarkwonTheme.Builder builder) {
          builder
            .codeBackgroundColor(Color.BLACK)
            .codeTextColor(Color.RED);
        }
      })
      .build();

    markwon.setMarkdown(textView, md);
  }
}
