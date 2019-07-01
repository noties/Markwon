package io.noties.markwon.app.gif;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.noties.markwon.image.AsyncDrawable;
import io.noties.markwon.image.AsyncDrawableLoader;
import io.noties.markwon.image.ImageSize;
import io.noties.markwon.image.ImageSizeResolver;
import pl.droidsonroids.gif.GifDrawable;

public class GifAwareAsyncDrawable extends AsyncDrawable {

    public interface OnGifResultListener {
        void onGifResult(@NonNull GifAwareAsyncDrawable drawable);
    }

    private final Drawable gifPlaceholder;
    private OnGifResultListener onGifResultListener;
    private boolean isGif;

    public GifAwareAsyncDrawable(
            @NonNull Drawable gifPlaceholder,
            @NonNull String destination,
            @NonNull AsyncDrawableLoader loader,
            @Nullable ImageSizeResolver imageSizeResolver,
            @Nullable ImageSize imageSize) {
        super(destination, loader, imageSizeResolver, imageSize);
        this.gifPlaceholder = gifPlaceholder;
    }

    public void onGifResultListener(@Nullable OnGifResultListener onGifResultListener) {
        this.onGifResultListener = onGifResultListener;
    }

    @Override
    public void setResult(@NonNull Drawable result) {
        super.setResult(result);
        isGif = result instanceof GifDrawable;
        if (isGif && onGifResultListener != null) {
            onGifResultListener.onGifResult(this);
        }
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        super.draw(canvas);

        if (isGif) {
            final GifDrawable drawable = (GifDrawable) getResult();
            if (!drawable.isPlaying()) {
                gifPlaceholder.setBounds(drawable.getBounds());
                gifPlaceholder.draw(canvas);
            }
        }
    }
}
