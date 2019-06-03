package ru.noties.markwon.ext.latex;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Px;
import android.text.Spanned;
import android.widget.TextView;

import org.commonmark.parser.Parser;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import ru.noties.jlatexmath.JLatexMathDrawable;
import ru.noties.markwon.AbstractMarkwonPlugin;
import ru.noties.markwon.MarkwonConfiguration;
import ru.noties.markwon.MarkwonVisitor;
import ru.noties.markwon.image.AsyncDrawable;
import ru.noties.markwon.image.AsyncDrawableLoader;
import ru.noties.markwon.image.AsyncDrawableScheduler;
import ru.noties.markwon.image.AsyncDrawableSpan;
import ru.noties.markwon.image.ImageSize;

/**
 * @since 3.0.0
 */
public class JLatexMathPlugin extends AbstractMarkwonPlugin {

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

        private final Drawable background;

        @JLatexMathDrawable.Align
        private final int align;

        private final boolean fitCanvas;

        private final int padding;

        // @since 4.0.0-SNAPSHOT
        private final ExecutorService executorService;

        Config(@NonNull Builder builder) {
            this.textSize = builder.textSize;
            this.background = builder.background;
            this.align = builder.align;
            this.fitCanvas = builder.fitCanvas;
            this.padding = builder.padding;

            // @since 4.0.0-SNAPSHOT
            ExecutorService executorService = builder.executorService;
            if (executorService == null) {
                executorService = Executors.newCachedThreadPool();
            }
            this.executorService = executorService;
        }
    }

    private final JLatextAsyncDrawableLoader jLatextAsyncDrawableLoader;

    @SuppressWarnings("WeakerAccess")
    JLatexMathPlugin(@NonNull Config config) {
        this.jLatextAsyncDrawableLoader = new JLatextAsyncDrawableLoader(config);
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

                visitor.builder().append(latex);

                final MarkwonConfiguration configuration = visitor.configuration();

                final AsyncDrawableSpan span = new AsyncDrawableSpan(
                        configuration.theme(),
                        new AsyncDrawable(
                                latex,
                                jLatextAsyncDrawableLoader,
                                configuration.imageSizeResolver(),
                                new ImageSize(
                                        new ImageSize.Dimension(100, "%"),
                                        null)),
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

    public static class Builder {

        private final float textSize;

        private Drawable background;

        @JLatexMathDrawable.Align
        private int align = JLatexMathDrawable.ALIGN_CENTER;

        private boolean fitCanvas = true;

        private int padding;

        // @since 4.0.0-SNAPSHOT
        private ExecutorService executorService;

        Builder(float textSize) {
            this.textSize = textSize;
        }

        @NonNull
        public Builder background(@NonNull Drawable background) {
            this.background = background;
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
            this.padding = padding;
            return this;
        }

        /**
         * @since 4.0.0-SNAPSHOT
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

    // @since 4.0.0-SNAPSHOT
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

                        // create JLatexMathDrawable
                        final JLatexMathDrawable jLatexMathDrawable =
                                JLatexMathDrawable.builder(drawable.getDestination())
                                        .textSize(config.textSize)
                                        .background(config.background)
                                        .align(config.align)
                                        .fitCanvas(config.fitCanvas)
                                        .padding(config.padding)
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
}
