package io.noties.markwon.app.samples.image;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.target.Target;

import io.noties.markwon.Markwon;
import io.noties.markwon.app.R;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.image.AsyncDrawable;
import io.noties.markwon.image.glide.GlideImagesPlugin;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;
import io.noties.markwon.sample.annotations.Tag;

@MarkwonSampleInfo(
  id = "20200630170241",
  title = "Glide image with placeholder",
  artifacts = MarkwonArtifact.IMAGE_GLIDE,
  tags = Tag.image
)
public class GlidePlaceholderImageSample extends MarkwonTextViewSample {
  @Override
  public void render() {
    final String md = "[![undefined](https://img.youtube.com/vi/gs1I8_m4AOM/0.jpg)](https://www.youtube.com/watch?v=gs1I8_m4AOM)";

    final Context context = this.context;

    final Markwon markwon = Markwon.builder(context)
      .usePlugin(GlideImagesPlugin.create(new GlideImagesPlugin.GlideStore() {
        @NonNull
        @Override
        public RequestBuilder<Drawable> load(@NonNull AsyncDrawable drawable) {
//          final Drawable placeholder = ContextCompat.getDrawable(context, R.drawable.ic_home_black_36dp);
//          placeholder.setBounds(0, 0, 100, 100);
          return Glide.with(context)
            .load(drawable.getDestination())
//            .placeholder(ContextCompat.getDrawable(context, R.drawable.ic_home_black_36dp));
//            .placeholder(placeholder);
            .placeholder(R.drawable.ic_home_black_36dp);
        }

        @Override
        public RequestBuilder<GifDrawable> loadGif(@NonNull AsyncDrawable drawable) {
          return null;
        }

        @Override
        public void cancel(@NonNull Target<?> target) {
          Glide.with(context)
            .clear(target);
        }
      }))
      .build();

    markwon.setMarkdown(textView, md);
  }
}
