package io.noties.markwon.app.samples.image;

import android.view.ViewTreeObserver;

import io.noties.markwon.Markwon;
import io.noties.markwon.app.sample.Tags;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.image.DefaultDownScalingMediaDecoder;
import io.noties.markwon.image.ImagesPlugin;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;

@MarkwonSampleInfo(
  id = "20210118165230",
  title = "Huge image downscaling",
  description = "Downscale displayed images with `BitmapOptions` 2 step rendering " +
    "(measure, downscale), use `DefaultDownScalingMediaDecoder`",
  artifacts = MarkwonArtifact.IMAGE,
  tags = Tags.image
)
public class HugeImageSample extends MarkwonTextViewSample {
  @Override
  public void render() {

    // NB! this is based on the width of the widget. In case you have big vertical
    //  images (with big vertical dimension, use some reasonable value or fallback to real OpenGL
    //  maximum, see: https://stackoverflow.com/questions/15313807/android-maximum-allowed-width-height-of-bitmap

    final int width = textView.getWidth();
    if (width > 0) {
      renderWithMaxWidth(width);
      return;
    }

    textView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
      @Override
      public boolean onPreDraw() {
        final int w = textView.getWidth();
        if (w > 0) {
          renderWithMaxWidth(w);

          final ViewTreeObserver observer = textView.getViewTreeObserver();
          if (observer.isAlive()) {
            observer.removeOnPreDrawListener(this);
          }
        }
        return true;
      }
    });
  }

  private void renderWithMaxWidth(int maxWidth) {

    final String md = "" +
      "# Huge image\n\n" +
      "![this is alt](https://otakurevolution.com/storyimgs/falldog/GundamTimeline/Falldogs_GundamTimeline_v13_April2020.png)\n\n" +
      "hey!";

    final Markwon markwon = Markwon.builder(context)
      .usePlugin(ImagesPlugin.create(plugin -> {
        plugin
          .defaultMediaDecoder(DefaultDownScalingMediaDecoder.create(maxWidth, 0));
      }))
      .build();

    markwon.setMarkdown(textView, md);
  }

}
