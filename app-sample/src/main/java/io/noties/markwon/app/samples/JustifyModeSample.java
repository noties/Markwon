package io.noties.markwon.app.samples;

import android.annotation.SuppressLint;
import android.os.Build;
import android.text.Layout;
import android.text.Spanned;
import android.widget.TextView;

import io.noties.markwon.Markwon;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.image.AsyncDrawableScheduler;
import io.noties.markwon.image.ImagesPlugin;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;
import io.noties.markwon.sample.annotations.Tag;

@MarkwonSampleInfo(
  id = "20200826084338",
  title = "Justify text",
  description = "Justify text with `justificationMode` argument on Oreo (>= 26)",
  artifacts = MarkwonArtifact.CORE,
  tags = Tag.rendering
)
public class JustifyModeSample extends MarkwonTextViewSample {
  @SuppressLint("WrongConstant")
  @Override
  public void render() {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      /*
      nice, API 29 though
      ```
      Error: Must be one of: LineBreaker.JUSTIFICATION_MODE_NONE, LineBreaker.JUSTIFICATION_MODE_INTER_WORD [WrongConstant]
      ```
       */
      textView.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
    }

    final String md = "" +
      "# Justify\n\n" +
      "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis rutrum orci at aliquet dapibus. Quisque laoreet fermentum bibendum. Suspendisse euismod nisl vel sapien viverra faucibus. Nulla vel neque volutpat, egestas dui ac, consequat elit. Donec et interdum massa. Quisque porta ornare posuere. Nam at ante a felis facilisis tempus eu et erat. Curabitur auctor mauris eget purus iaculis vulputate.\n\n" +
      "> Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis rutrum orci at aliquet dapibus. Quisque laoreet fermentum bibendum. Suspendisse euismod nisl vel sapien viverra faucibus. Nulla vel neque volutpat, egestas dui ac, consequat elit. Donec et interdum massa. Quisque porta ornare posuere. Nam at ante a felis facilisis tempus eu et erat. Curabitur auctor mauris eget purus iaculis vulputate.\n\n" +
      "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis rutrum orci at aliquet dapibus. Quisque laoreet fermentum bibendum. Suspendisse euismod nisl vel sapien viverra faucibus. Nulla vel neque volutpat, egestas dui ac, consequat elit. Donec et interdum massa. **Quisque porta ornare posuere.** Nam at ante a felis facilisis tempus eu et erat. Curabitur auctor mauris eget purus iaculis vulputate.\n\n" +
      "";

    if (false) {
      // specify bufferType to make justificationMode argument be ignored
      // Actually just calling method with BufferType argument would make
      //  justification gone
      textView.setText(md, TextView.BufferType.SPANNABLE);
      return;
    }

    final Markwon markwon = Markwon.builder(context)
      .usePlugin(ImagesPlugin.create())
      .build();

    if (true) {
      final Spanned spanned = markwon.toMarkdown(md);

      // NB! the call to `setText` without arguments
      textView.setText(spanned);

      // if a plugin relies on `afterSetText` then we must manually call it,
      //  for example images are scheduled this way:
      AsyncDrawableScheduler.schedule(textView);
      return;
    }

    // cannot use that
    markwon.setMarkdown(textView, md);
  }
}
