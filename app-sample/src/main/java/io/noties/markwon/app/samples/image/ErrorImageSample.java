package io.noties.markwon.app.samples.image;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import io.noties.markwon.Markwon;
import io.noties.markwon.app.R;
import io.noties.markwon.app.sample.Tags;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.image.ImagesPlugin;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;

@MarkwonSampleInfo(
  id = "202006182165828",
  title = "Image error handler",
  artifacts = MarkwonArtifact.IMAGE,
  tags = Tags.image
)
public class ErrorImageSample extends MarkwonTextViewSample {
  @Override
  public void render() {
    final String md = "" +
      "![error](https://github.com/dcurtis/markdown-mark/raw/master/png/______1664x1024-solid.png)";

    final Markwon markwon = Markwon.builder(context)
      // error handler additionally allows to log/inspect errors during image loading
      .usePlugin(ImagesPlugin.create(plugin ->
        plugin.errorHandler(new ImagesPlugin.ErrorHandler() {
          @Nullable
          @Override
          public Drawable handleError(@NonNull String url, @NonNull Throwable throwable) {
            return ContextCompat.getDrawable(context, R.drawable.ic_home_black_36dp);
          }
        })))
      .build();

    markwon.setMarkdown(textView, md);
  }
}
