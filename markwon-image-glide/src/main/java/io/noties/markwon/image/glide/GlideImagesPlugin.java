package io.noties.markwon.image.glide;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spanned;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
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
public class GlideImagesPlugin extends AbstractMarkwonPlugin {

    public interface GlideStore {

        @NonNull
        RequestBuilder<Drawable> load(@NonNull AsyncDrawable drawable);

        void cancel(@NonNull Target<?> target);
    }

    @NonNull
    public static GlideImagesPlugin create(@NonNull final Context context) {
        // @since 4.5.0 cache RequestManager
        //  sometimes `cancel` would be called after activity is destroyed,
        //  so `Glide.with(context)` will throw an exception
        return create(Glide.with(context));
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

    private static class GlideAsyncDrawableLoader extends AsyncDrawableLoader {

        private final GlideStore glideStore;
        private final Map<AsyncDrawable, Target<?>> cache = new HashMap<>(2);

        GlideAsyncDrawableLoader(@NonNull GlideStore glideStore) {
            this.glideStore = glideStore;
        }

        @Override
        public void load(@NonNull AsyncDrawable drawable) {
            final Target<Drawable> target = new AsyncDrawableTarget(drawable);
            cache.put(drawable, target);
            glideStore.load(drawable)
                    .into(target);
        }

        @Override
        public void cancel(@NonNull AsyncDrawable drawable) {
            final Target<?> target = cache.remove(drawable);
            if (target != null) {
                glideStore.cancel(target);
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
                if (cache.remove(drawable) != null) {
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
                if (cache.remove(drawable) != null) {
                    if (errorDrawable != null
                            && drawable.isAttached()) {
                        DrawableUtils.applyIntrinsicBoundsIfEmpty(errorDrawable);
                        drawable.setResult(errorDrawable);
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
