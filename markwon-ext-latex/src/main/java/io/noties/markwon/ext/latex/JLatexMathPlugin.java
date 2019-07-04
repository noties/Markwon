package io.noties.markwon.ext.latex;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.Spanned;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.annotation.VisibleForTesting;

import org.commonmark.parser.Parser;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.MarkwonConfiguration;
import io.noties.markwon.MarkwonVisitor;
import io.noties.markwon.image.AsyncDrawable;
import io.noties.markwon.image.AsyncDrawableLoader;
import io.noties.markwon.image.AsyncDrawableScheduler;
import io.noties.markwon.image.AsyncDrawableSpan;
import io.noties.markwon.image.ImageSizeResolver;
import ru.noties.jlatexmath.JLatexMathDrawable;

/**
 * @since 3.0.0
 */
public class JLatexMathPlugin extends AbstractMarkwonPlugin {

    /**
     * @since 4.0.0
     */
    public interface BackgroundProvider {
        @NonNull
        Drawable provide();
    }

    public interface BuilderConfigure {
        void configureBuilder(@NonNull Builder builder);
    }

    @NonNull
    public static JLatexMathPlugin create(float textSize) {
        return new JLatexMathPlugin(builder(textSize).build());
    }

    @NonNull
    public static JLatexMathPlugin create(@NonNull Config config) {
        return new JLatexMathPlugin(config);
    }

    @NonNull
    public static JLatexMathPlugin create(float textSize, @NonNull BuilderConfigure builderConfigure) {
        final Builder builder = new Builder(textSize);
        builderConfigure.configureBuilder(builder);
        return new JLatexMathPlugin(builder.build());
    }

    @NonNull
    public static JLatexMathPlugin.Builder builder(float textSize) {
        return new Builder(textSize);
    }

    public static class Config {

        private final float textSize;

        // @since 4.0.0
        private final BackgroundProvider backgroundProvider;

        @JLatexMathDrawable.Align
        private final int align;

        private final boolean fitCanvas;

        // @since 4.0.0
        private final int paddingHorizontal;

        // @since 4.0.0
        private final int paddingVertical;

        // @since 4.0.0
        private final ExecutorService executorService;

        Config(@NonNull Builder builder) {
            this.textSize = builder.textSize;
            this.backgroundProvider = builder.backgroundProvider;
            this.align = builder.align;
            this.fitCanvas = builder.fitCanvas;
            this.paddingHorizontal = builder.paddingHorizontal;
            this.paddingVertical = builder.paddingVertical;

            // @since 4.0.0
            ExecutorService executorService = builder.executorService;
            if (executorService == null) {
                executorService = Executors.newCachedThreadPool();
            }
            this.executorService = executorService;
        }
    }

    private final JLatextAsyncDrawableLoader jLatextAsyncDrawableLoader;
    private final JLatexImageSizeResolver jLatexImageSizeResolver;

    @SuppressWarnings("WeakerAccess")
    JLatexMathPlugin(@NonNull Config config) {
        this.jLatextAsyncDrawableLoader = new JLatextAsyncDrawableLoader(config);
        this.jLatexImageSizeResolver = new JLatexImageSizeResolver(config.fitCanvas);
    }

    @Override
    public void configureParser(@NonNull Parser.Builder builder) {
        builder.customBlockParserFactory(new JLatexMathBlockParser.Factory());
    }

    @Override
    public void configureVisitor(@NonNull MarkwonVisitor.Builder builder) {
        builder.on(JLatexMathBlock.class, new MarkwonVisitor.NodeVisitor<JLatexMathBlock>() {
            @Override
            public void visit(@NonNull MarkwonVisitor visitor, @NonNull JLatexMathBlock jLatexMathBlock) {

                final String latex = jLatexMathBlock.latex();

                final int length = visitor.length();

                // @since 4.0.2 we cannot append _raw_ latex as a placeholder-text,
                // because Android will draw formula for each line of text, thus
                // leading to formula duplicated (drawn on each line of text)
                visitor.builder().append(prepareLatexTextPlaceholder(latex));

                final MarkwonConfiguration configuration = visitor.configuration();

                final AsyncDrawableSpan span = new AsyncDrawableSpan(
                        configuration.theme(),
                        new AsyncDrawable(
                                latex,
                                jLatextAsyncDrawableLoader,
                                jLatexImageSizeResolver,
                                null),
                        AsyncDrawableSpan.ALIGN_BOTTOM,
                        false);

                visitor.setSpans(length, span);
            }
        });
    }

    @Override
    public void beforeSetText(@NonNull TextView textView, @NonNull Spanned markdown) {
        AsyncDrawableScheduler.unschedule(textView);
    }

    @Override
    public void afterSetText(@NonNull TextView textView) {
        AsyncDrawableScheduler.schedule(textView);
    }

    // @since 4.0.2
    @VisibleForTesting
    @NonNull
    static String prepareLatexTextPlaceholder(@NonNull String latex) {
        return latex.replace('\n', ' ').trim();
    }

    public static class Builder {

        private final float textSize;

        // @since 4.0.0
        private BackgroundProvider backgroundProvider;

        @JLatexMathDrawable.Align
        private int align = JLatexMathDrawable.ALIGN_CENTER;

        private boolean fitCanvas = true;

        // @since 4.0.0
        private int paddingHorizontal;

        // @since 4.0.0
        private int paddingVertical;

        // @since 4.0.0
        private ExecutorService executorService;

        Builder(float textSize) {
            this.textSize = textSize;
        }

        @NonNull
        public Builder backgroundProvider(@NonNull BackgroundProvider backgroundProvider) {
            this.backgroundProvider = backgroundProvider;
            return this;
        }

        @NonNull
        public Builder align(@JLatexMathDrawable.Align int align) {
            this.align = align;
            return this;
        }

        @NonNull
        public Builder fitCanvas(boolean fitCanvas) {
            this.fitCanvas = fitCanvas;
            return this;
        }

        @NonNull
        public Builder padding(@Px int padding) {
            this.paddingHorizontal = padding;
            this.paddingVertical = padding;
            return this;
        }

        /**
         * @since 4.0.0
         */
        @NonNull
        public Builder builder(@Px int paddingHorizontal, @Px int paddingVertical) {
            this.paddingHorizontal = paddingHorizontal;
            this.paddingVertical = paddingVertical;
            return this;
        }

        /**
         * @since 4.0.0
         */
        @NonNull
        public Builder executorService(@NonNull ExecutorService executorService) {
            this.executorService = executorService;
            return this;
        }

        @NonNull
        public Config build() {
            return new Config(this);
        }
    }

    // @since 4.0.0
    private static class JLatextAsyncDrawableLoader extends AsyncDrawableLoader {

        private final Config config;
        private final Handler handler = new Handler(Looper.getMainLooper());
        private final Map<AsyncDrawable, Future<?>> cache = new HashMap<>(3);

        JLatextAsyncDrawableLoader(@NonNull Config config) {
            this.config = config;
        }

        @Override
        public void load(@NonNull final AsyncDrawable drawable) {

            // this method must be called from main-thread only (thus synchronization can be skipped)

            // check for currently running tasks associated with provided drawable
            final Future<?> future = cache.get(drawable);

            // if it's present -> proceed with new execution
            // as asyncDrawable is immutable, it won't have destination changed (so there is no need
            // to cancel any started tasks)
            if (future == null) {

                cache.put(drawable, config.executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        // @since 4.0.1 wrap in try-catch block and add error logging
                        try {
                            execute();
                        } catch (Throwable t) {
                            Log.e(
                                    "JLatexMathPlugin",
                                    "Error displaying latex: `" + drawable.getDestination() + "`",
                                    t);
                        }
                    }

                    private void execute() {

                        // @since 4.0.1 (background provider can be null)
                        final BackgroundProvider backgroundProvider = config.backgroundProvider;

                        // create JLatexMathDrawable
                        //noinspection ConstantConditions
                        final JLatexMathDrawable jLatexMathDrawable =
                                JLatexMathDrawable.builder(drawable.getDestination())
                                        .textSize(config.textSize)
                                        .background(backgroundProvider != null ? backgroundProvider.provide() : null)
                                        .align(config.align)
                                        .fitCanvas(config.fitCanvas)
                                        .padding(
                                                config.paddingHorizontal,
                                                config.paddingVertical,
                                                config.paddingHorizontal,
                                                config.paddingVertical)
                                        .build();

                        // we must post to handler, but also have a way to identify the drawable
                        // for which we are posting (in case of cancellation)
                        handler.postAtTime(new Runnable() {
                            @Override
                            public void run() {
                                // remove entry from cache (it will be present if task is not cancelled)
                                if (cache.remove(drawable) != null
                                        && drawable.isAttached()) {
                                    drawable.setResult(jLatexMathDrawable);
                                }

                            }
                        }, drawable, SystemClock.uptimeMillis());
                    }
                }));
            }
        }

        @Override
        public void cancel(@NonNull AsyncDrawable drawable) {

            // this method also must be called from main thread only

            final Future<?> future = cache.remove(drawable);
            if (future != null) {
                future.cancel(true);
            }

            // remove all callbacks (via runnable) and messages posted for this drawable
            handler.removeCallbacksAndMessages(drawable);
        }

        @Nullable
        @Override
        public Drawable placeholder(@NonNull AsyncDrawable drawable) {
            return null;
        }
    }

    // we must make drawable fit canvas (if specified), but do not keep the ratio whilst scaling up
    // @since 4.0.0
    private static class JLatexImageSizeResolver extends ImageSizeResolver {

        private final boolean fitCanvas;

        JLatexImageSizeResolver(boolean fitCanvas) {
            this.fitCanvas = fitCanvas;
        }

        @NonNull
        @Override
        public Rect resolveImageSize(@NonNull AsyncDrawable drawable) {

            final Rect imageBounds = drawable.getResult().getBounds();
            final int canvasWidth = drawable.getLastKnownCanvasWidth();

            if (fitCanvas) {

                // we modify bounds only if `fitCanvas` is true
                final int w = imageBounds.width();

                if (w < canvasWidth) {
                    // increase width and center formula (keep height as-is)
                    return new Rect(0, 0, canvasWidth, imageBounds.height());
                }

                // @since 4.0.2 we additionally scale down the resulting formula (keeping the ratio)
                // the thing is - JLatexMathDrawable will do it anyway, but it will modify its own
                // bounds (which AsyncDrawable won't catch), thus leading to an empty space after the formula
                if (w > canvasWidth) {
                    // here we must scale it down (keeping the ratio)
                    final float ratio = (float) w / imageBounds.height();
                    final int h = (int) (canvasWidth / ratio + .5F);
                    return new Rect(0, 0, canvasWidth, h);
                }
            }

            return imageBounds;
        }
    }
}
