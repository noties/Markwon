package io.noties.markwon.image.glide;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Spanned;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import org.commonmark.node.Image;

import java.util.HashMap;
import java.util.Map;

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

/**
 * @since 4.0.0
 */
public class GlideImagesPlugin extends AbstractMarkwonPlugin {

    public interface GlideStore {

        @NonNull
        RequestBuilder<Drawable> load(@NonNull AsyncDrawable drawable);

        RequestBuilder<GifDrawable> loadGif(@NonNull AsyncDrawable drawable);

        void cancel(@NonNull Target<?> target);
    }

    @NonNull
    public static GlideImagesPlugin create(@NonNull final Context context) {
        return create(new GlideStore() {
            @NonNull
            @Override
            public RequestBuilder<Drawable> load(@NonNull AsyncDrawable drawable) {
                return Glide.with(getValidContext(context))
                        .load(drawable.getDestination());
            }

            @Override
            public RequestBuilder<GifDrawable> loadGif(@NonNull AsyncDrawable drawable) {
                return Glide.with(getValidContext(context))
                        .asGif()
                        .load(drawable.getDestination());
            }

            @Override
            public void cancel(@NonNull Target<?> target) {
                if (target != null){
// Fix the sentry issue  Canvas: trying to use a recycled bitmap android.graphics.Bitmap@a44a774
//                    Glide.with(getValidContext(context))
//                            .clear(target);
                }
            }
        });
    }

    @NonNull
    public static GlideImagesPlugin create(@NonNull final RequestManager requestManager) {
        return create(new GlideStore() {
            @NonNull
            @Override
            public RequestBuilder<Drawable> load(@NonNull AsyncDrawable drawable) {
                return requestManager.load(drawable.getDestination());
            }

            @Override
            public RequestBuilder<GifDrawable> loadGif(@NonNull AsyncDrawable drawable) {
                return requestManager.asGif().load(drawable.getDestination());
            }

            @Override
            public void cancel(@NonNull Target<?> target) {
                requestManager.clear(target);
            }
        });
    }

    @NonNull
    public static GlideImagesPlugin create(@NonNull GlideStore glideStore) {
        return new GlideImagesPlugin(glideStore);
    }

    private final GlideAsyncDrawableLoader glideAsyncDrawableLoader;

    @SuppressWarnings("WeakerAccess")
    GlideImagesPlugin(@NonNull GlideStore glideStore) {
        this.glideAsyncDrawableLoader = new GlideAsyncDrawableLoader(glideStore);
    }

    @Override
    public void configureSpansFactory(@NonNull MarkwonSpansFactory.Builder builder) {
        builder.setFactory(Image.class, new ImageSpanFactory());
    }

    @Override
    public void configureConfiguration(@NonNull MarkwonConfiguration.Builder builder) {
        builder.asyncDrawableLoader(glideAsyncDrawableLoader);
    }

    @Override
    public void beforeSetText(@NonNull TextView textView, @NonNull Spanned markdown) {
        AsyncDrawableScheduler.unschedule(textView);
    }

    @Override
    public void afterSetText(@NonNull TextView textView) {
        AsyncDrawableScheduler.schedule(textView);
    }

    public static Context getValidContext(final Context context) {
        if (context instanceof Activity) {
            final Activity activity = (Activity) context;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                if (activity.isDestroyed() || activity.isFinishing()) {
                    return context.getApplicationContext();
                }
            }
        }
        return context;
    }

    private static class GlideAsyncDrawableLoader extends AsyncDrawableLoader {

        private final GlideStore glideStore;
        private final Map<String, Target<?>> cache = new HashMap<>(2);

        GlideAsyncDrawableLoader(@NonNull GlideStore glideStore) {
            this.glideStore = glideStore;
        }

        @Override
        public void load(@NonNull AsyncDrawable drawable) {
            String dest = drawable.getDestination();
            if (dest.contains(".gif")){
                final Target<GifDrawable> targetGif = new AsyncGifDrawableTarget(drawable);
                if (!cache.containsKey(dest)){
                    cache.put(dest, targetGif);
                    glideStore.loadGif(drawable)
                            .into(targetGif);
                }
            } else {
                final Target<Drawable> target = new AsyncDrawableTarget(drawable);
                if (!cache.containsKey(dest)){
                    cache.put(dest, target);
                    glideStore.load(drawable)
                            .into(target);
                }
            }

        }

        @Override
        public void cancel(@NonNull AsyncDrawable drawable) {
            String dest = drawable.getDestination();
            final Target<?> target = cache.get(dest);
            if (target != null) {
                glideStore.cancel(target);
                cache.remove(dest);
            }
        }

        @Nullable
        @Override
        public Drawable placeholder(@NonNull AsyncDrawable drawable) {
            return null;
        }

        private class AsyncDrawableTarget extends CustomTarget<Drawable> {

            private final AsyncDrawable drawable;

            AsyncDrawableTarget(@NonNull AsyncDrawable drawable) {
                this.drawable = drawable;
            }

            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                String dest = drawable.getDestination();
                if (cache.get(dest) != null) {
                    if (drawable.isAttached()) {
                        DrawableUtils.applyIntrinsicBoundsIfEmpty(resource);
                        drawable.setResult(resource);
                    }
                }
            }

            @Override
            public void onLoadStarted(@Nullable Drawable placeholder) {
                if (placeholder != null
                        && drawable.isAttached()) {
                    DrawableUtils.applyIntrinsicBoundsIfEmpty(placeholder);
                    drawable.setResult(placeholder);
                }
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                String dest = drawable.getDestination();
                if (cache.get(dest) != null) {
                    if (errorDrawable != null
                            && drawable.isAttached()) {
                        DrawableUtils.applyIntrinsicBoundsIfEmpty(errorDrawable);
                        drawable.setResult(errorDrawable);
                    }
                    cache.remove(dest);
                }
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
                // we won't be checking if target is still present as cancellation
                // must remove target anyway
                if (drawable.isAttached()) {
                    drawable.clearResult();
                }
            }
        }


        private class AsyncGifDrawableTarget extends CustomTarget<GifDrawable> {

            private final AsyncDrawable drawable;

            AsyncGifDrawableTarget(@NonNull AsyncDrawable drawable) {
                this.drawable = drawable;
            }

            @Override
            public void onLoadStarted(@Nullable Drawable placeholder) {
                if (placeholder != null
                        && drawable.isAttached()) {
                    DrawableUtils.applyIntrinsicBoundsIfEmpty(placeholder);
                    drawable.setResult(placeholder);
                }
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                String dest = drawable.getDestination();
                if (cache.get(dest) != null) {
                    if (errorDrawable != null
                            && drawable.isAttached()) {
                        DrawableUtils.applyIntrinsicBoundsIfEmpty(errorDrawable);
                        drawable.setResult(errorDrawable);
                    }
                    cache.remove(dest);
                }
            }

            @Override
            public void onResourceReady(@NonNull GifDrawable resource, @Nullable Transition<? super GifDrawable> transition) {
                String dest = drawable.getDestination();
                if (cache.get(dest) != null) {
                    if (drawable.isAttached()) {
                        DrawableUtils.applyIntrinsicBoundsIfEmpty(resource);
                        drawable.setResult(resource);
                    }
                    if (resource != null) {
                        resource.start();
                    }
                }
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
                // we won't be checking if target is still present as cancellation
                // must remove target anyway
                if (drawable.isAttached()) {
                    drawable.clearResult();
                }
            }
        }
    }
}
