package io.noties.markwon.image.coil;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spanned;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.commonmark.node.Image;

import java.util.HashMap;
import java.util.Map;

import coil.Coil;
import coil.ImageLoader;
import coil.api.ImageLoaders;
import coil.request.LoadRequest;
import coil.request.LoadRequestBuilder;
import coil.request.RequestDisposable;
import coil.target.Target;
import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.MarkwonConfiguration;
import io.noties.markwon.MarkwonSpansFactory;
import io.noties.markwon.image.AsyncDrawable;
import io.noties.markwon.image.AsyncDrawableLoader;
import io.noties.markwon.image.AsyncDrawableScheduler;
import io.noties.markwon.image.DrawableUtils;
import io.noties.markwon.image.ImageSpanFactory;

/**
 * @since 4.0.0
 */
public class CoilImagesPlugin extends AbstractMarkwonPlugin {

    public interface CoilStore {

        @NonNull
        RequestDisposable load(@NonNull AsyncDrawable drawable, @NonNull Target target);

        void cancel(@NonNull RequestDisposable disposable);
    }

    @NonNull
    public static CoilImagesPlugin create(@NonNull final Context context) {
        return create(new CoilStore() {
            @NonNull
            @Override
            public RequestDisposable load(@NonNull AsyncDrawable drawable, @NonNull Target target) {
                ImageLoader imageLoader = Coil.loader();
                LoadRequest request = ImageLoaders.newLoadBuilder(imageLoader, context)
                        .data(drawable.getDestination())
                        .target(target)
                        .build();
                return imageLoader.load(request);
            }

            @Override
            public void cancel(@NonNull RequestDisposable disposable) {
                disposable.dispose();
            }
        });
    }

    @NonNull
    public static CoilImagesPlugin create(@NonNull final LoadRequestBuilder loadRequestBuilder) {
        return create(new CoilStore() {
            @NonNull
            @Override
            public RequestDisposable load(@NonNull AsyncDrawable drawable, @NonNull Target target) {
                ImageLoader imageLoader = Coil.loader();
                LoadRequest request = loadRequestBuilder
                        .data(drawable.getDestination())
                        .target(target)
                        .build();
                return imageLoader.load(request);
            }

            @Override
            public void cancel(@NonNull RequestDisposable disposable) {
                disposable.dispose();
            }
        });
    }

    @NonNull
    public static CoilImagesPlugin create(@NonNull CoilStore coilStore) {
        return new CoilImagesPlugin(coilStore);
    }

    private final CoilAsyncDrawableLoader coilAsyncDrawableLoader;

    @SuppressWarnings("WeakerAccess")
    CoilImagesPlugin(@NonNull CoilStore coilStore) {
        this.coilAsyncDrawableLoader = new CoilAsyncDrawableLoader(coilStore);
    }

    @Override
    public void configureSpansFactory(@NonNull MarkwonSpansFactory.Builder builder) {
        builder.setFactory(Image.class, new ImageSpanFactory());
    }

    @Override
    public void configureConfiguration(@NonNull MarkwonConfiguration.Builder builder) {
        builder.asyncDrawableLoader(coilAsyncDrawableLoader);
    }

    @Override
    public void beforeSetText(@NonNull TextView textView, @NonNull Spanned markdown) {
        AsyncDrawableScheduler.unschedule(textView);
    }

    @Override
    public void afterSetText(@NonNull TextView textView) {
        AsyncDrawableScheduler.schedule(textView);
    }

    private static class CoilAsyncDrawableLoader extends AsyncDrawableLoader {

        private final CoilStore coilStore;
        private final Map<AsyncDrawable, RequestDisposable> cache = new HashMap<>(2);

        CoilAsyncDrawableLoader(@NonNull CoilStore coilStore) {
            this.coilStore = coilStore;
        }

        @Override
        public void load(@NonNull AsyncDrawable drawable) {
            final Target target = new AsyncDrawableTarget(drawable);
            RequestDisposable disposable = coilStore.load(drawable, target);
            cache.put(drawable, disposable);
        }

        @Override
        public void cancel(@NonNull AsyncDrawable drawable) {
            final RequestDisposable disposable = cache.remove(drawable);
            if (disposable != null) {
                coilStore.cancel(disposable);
            }
        }

        @Nullable
        @Override
        public Drawable placeholder(@NonNull AsyncDrawable drawable) {
            return null;
        }

        private class AsyncDrawableTarget implements Target {

            private final AsyncDrawable drawable;

            AsyncDrawableTarget(@NonNull AsyncDrawable drawable) {
                this.drawable = drawable;
            }

            @Override
            public void onSuccess(@NonNull Drawable loadedDrawable) {
                if (cache.remove(drawable) != null) {
                    if (drawable.isAttached()) {
                        DrawableUtils.applyIntrinsicBoundsIfEmpty(loadedDrawable);
                        drawable.setResult(loadedDrawable);
                    }
                }
            }

            @Override
            public void onStart(@Nullable Drawable placeholder) {
                if (placeholder != null && drawable.isAttached()) {
                    DrawableUtils.applyIntrinsicBoundsIfEmpty(placeholder);
                    drawable.setResult(placeholder);
                }
            }

            @Override
            public void onError(@Nullable Drawable errorDrawable) {
                if (cache.remove(drawable) != null) {
                    if (errorDrawable != null && drawable.isAttached()) {
                        DrawableUtils.applyIntrinsicBoundsIfEmpty(errorDrawable);
                        drawable.setResult(errorDrawable);
                    }
                }
            }
        }
    }
}
