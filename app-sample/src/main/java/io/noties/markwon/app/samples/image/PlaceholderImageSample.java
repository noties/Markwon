package io.noties.markwon.app.samples.image;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import io.noties.markwon.Markwon;
import io.noties.markwon.app.R;
import io.noties.markwon.app.sample.Tags;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.image.AsyncDrawable;
import io.noties.markwon.image.ImagesPlugin;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;

@MarkwonSampleInfo(
  id = "20200630165504",
  title = "Image with placeholder",
  artifacts = MarkwonArtifact.IMAGE,
  tags = Tags.image
)
public class PlaceholderImageSample extends MarkwonTextViewSample {
  @Override
  public void render() {
    final String md = "" +
      "![image](https://github.com/dcurtis/markdown-mark/raw/master/png/1664x1024-solid.png)";

    final Markwon markwon = Markwon.builder(context)
      .usePlugin(ImagesPlugin.create(plugin -> {
        plugin.placeholderProvider(new ImagesPlugin.PlaceholderProvider() {
          @Nullable
          @Override
          public Drawable providePlaceholder(@NonNull AsyncDrawable drawable) {
            // by default drawable intrinsic size will be used
            //  otherwise bounds can be applied explicitly
            return ContextCompat.getDrawable(context, R.drawable.ic_android_black_24dp);
          }
        });
      }))
      .build();

    markwon.setMarkdown(textView, md);
  }
}
