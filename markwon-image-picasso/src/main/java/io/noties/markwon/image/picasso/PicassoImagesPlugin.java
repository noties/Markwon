package io.noties.markwon.image.picasso;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Spanned;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

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
 * @since 4.0.0-SNAPSHOT
 */
public class PicassoImagesPlugin extends AbstractMarkwonPlugin {

    public interface PicassoStore {

        @NonNull
        RequestCreator load(@NonNull AsyncDrawable drawable);

        void cancel(@NonNull AsyncDrawable drawable);
    }

    @NonNull
    public static PicassoImagesPlugin create(@NonNull Context context) {
        return create(new Picasso.Builder(context).build());
    }

    @NonNull
    public static PicassoImagesPlugin create(@NonNull final Picasso picasso) {
        return create(new PicassoStore() {
            @NonNull
            @Override
            public RequestCreator load(@NonNull AsyncDrawable drawable) {
                return picasso.load(drawable.getDestination())
                        .tag(drawable);
            }

            @Override
            public void cancel(@NonNull AsyncDrawable drawable) {
                picasso.cancelTag(drawable);
            }
        });
    }

    @NonNull
    public static PicassoImagesPlugin create(@NonNull PicassoStore picassoStore) {
        return new PicassoImagesPlugin(picassoStore);
    }

    private final PicassoAsyncDrawableLoader picassoAsyncDrawableLoader;

    @SuppressWarnings("WeakerAccess")
    PicassoImagesPlugin(@NonNull PicassoStore picassoStore) {
        this.picassoAsyncDrawableLoader = new PicassoAsyncDrawableLoader(picassoStore);
    }

    @Override
    public void configureConfiguration(@NonNull MarkwonConfiguration.Builder builder) {
        builder.asyncDrawableLoader(picassoAsyncDrawableLoader);
    }

    @Override
    public void configureSpansFactory(@NonNull MarkwonSpansFactory.Builder builder) {
        builder.setFactory(Image.class, new ImageSpanFactory());
    }

    @Override
    public void beforeSetText(@NonNull TextView textView, @NonNull Spanned markdown) {
        AsyncDrawableScheduler.unschedule(textView);
    }

    @Override
    public void afterSetText(@NonNull TextView textView) {
        AsyncDrawableScheduler.schedule(textView);
    }

    private static class PicassoAsyncDrawableLoader extends AsyncDrawableLoader {

        private final PicassoStore picassoStore;
        private final Map<AsyncDrawable, AsyncDrawableTarget> cache = new HashMap<>(2);

        PicassoAsyncDrawableLoader(@NonNull PicassoStore picassoStore) {
            this.picassoStore = picassoStore;
        }

        @Override
        public void load(@NonNull AsyncDrawable drawable) {

            // we must store hard-reference to target (otherwise it will be garbage-collected
            // ad picasso internally stores a target in a weak-reference)
            final AsyncDrawableTarget target = new AsyncDrawableTarget(drawable);
            cache.put(drawable, target);

            picassoStore.load(drawable)
                    .into(target);
        }

        @Override
        public void cancel(@NonNull AsyncDrawable drawable) {

            cache.remove(drawable);

            picassoStore.cancel(drawable);
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
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                if (cache.remove(drawable) != null) {
                    if (drawable.isAttached() && bitmap != null) {
                        final BitmapDrawable bitmapDrawable = new BitmapDrawable(Resources.getSystem(), bitmap);
                        DrawableUtils.applyIntrinsicBounds(bitmapDrawable);
                        drawable.setResult(bitmapDrawable);
                    }
                }
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                if (cache.remove(drawable) != null) {
                    if (errorDrawable != null
                            && drawable.isAttached()) {
                        DrawableUtils.applyIntrinsicBoundsIfEmpty(errorDrawable);
                        drawable.setResult(errorDrawable);
                    }
                }
                e.printStackTrace();
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                if (placeHolderDrawable != null
                        && canDeliver()) {
                    DrawableUtils.applyIntrinsicBoundsIfEmpty(placeHolderDrawable);
                    drawable.setResult(placeHolderDrawable);
                }
            }

            private boolean canDeliver() {
                return drawable.isAttached() && cache.containsKey(drawable);
            }
        }
    }
}
