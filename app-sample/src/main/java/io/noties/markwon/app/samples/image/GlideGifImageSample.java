package io.noties.markwon.app.samples.image;

import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import io.noties.markwon.Markwon;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.image.AsyncDrawable;
import io.noties.markwon.image.glide.GlideImagesPlugin;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;
import io.noties.markwon.sample.annotations.Tag;

@MarkwonSampleInfo(
  id = "20200820071942",
  title = "Glide GIF",
  artifacts = MarkwonArtifact.IMAGE_GLIDE,
  tags = Tag.image
)
public class GlideGifImageSample extends MarkwonTextViewSample {
  @Override
  public void render() {
    final String md = "# Glide GIF\n" +
      "![gif-image](https://upload.wikimedia.org/wikipedia/commons/2/2c/Rotating_earth_%28large%29.gif) " +
      "and some other resource: ![image](https://github.com/dcurtis/markdown-mark/raw/master/png/208x128-solid.png)\n\n" +
      "Hey: ![alt](https://picsum.photos/id/237/1024/800)";

    final Markwon markwon = Markwon.builder(context)
      .usePlugin(GlideImagesPlugin.create(new GifGlideStore(Glide.with(context))))
      .build();

    markwon.setMarkdown(textView, md);
  }

  private static class GifGlideStore implements GlideImagesPlugin.GlideStore {
    private final RequestManager requestManager;

    GifGlideStore(RequestManager requestManager) {
      this.requestManager = requestManager;
    }

    @NonNull
    @Override
    public RequestBuilder<Drawable> load(@NonNull AsyncDrawable drawable) {
      // NB! Strange behaviour: First time a resource is requested - it fails, the next time it loads fine
      final String destination = drawable.getDestination();
      return requestManager
        // it is enough to call this (in order to load GIF and non-GIF)
        .asDrawable()
        .addListener(new RequestListener<Drawable>() {
          @Override
          public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
            return false;
          }

          @Override
          public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
            // we must start GIF animation manually
            if (resource instanceof Animatable) {
              ((Animatable) resource).start();
            }
            return false;
          }
        })
        .load(destination);
    }

    @Override
    public RequestBuilder<GifDrawable> loadGif(@NonNull AsyncDrawable drawable) {
      return null;
    }

    @Override
    public void cancel(@NonNull Target<?> target) {
      requestManager.clear(target);
    }
  }
}
