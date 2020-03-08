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
import io.noties.markwon.image.DrawableUtils;
import io.noties.markwon.image.ImageSizeResolver;
import io.noties.markwon.inlineparser.MarkwonInlineParserPlugin;
import ru.noties.jlatexmath.JLatexMathDrawable;

/**
 * @since 3.0.0
 */
public class JLatexMathPlugin extends AbstractMarkwonPlugin {

    /**
     * @since 4.3.0-SNAPSHOT
     */
    public enum RenderMode {
        /**
         * <em>LEGACY</em> mode mimics pre {@code 4.3.0-SNAPSHOT} behavior by rendering LaTeX blocks only.
         * In this mode LaTeX is started by `$$` (that must be exactly at the start of a line) and
         * ended at whatever line that is ended with `$$` characters exactly.
         */
        LEGACY,

        /**
         * Starting with {@code 4.3.0-SNAPSHOT} it is possible to have LaTeX inlines (which flows inside
         * a text paragraph without breaking it). Inline LaTeX starts and ends with `$$` symbols. For example:
         * {@code
         * **bold $$\\begin{array}\\end{array}$$ bold-end**, and whatever more
         * }
         * LaTeX block starts on a new line started by 0-3 spaces and 2 (or more) {@code $} signs
         * followed by a new-line (with any amount of space characters in-between). And ends on a new-line
         * starting with 0-3 spaces followed by number of {@code $} signs that was used to <em>start the block</em>.
         */
        BLOCKS_AND_INLINES
    }

    /**
     * @since 4.3.0-SNAPSHOT
     */
    public interface ErrorHandler {

        /**
         * @param latex that caused the error
         * @param error occurred
         * @return (optional) error drawable that will be used instead (if drawable will have bounds
         * it will be used, if not intrinsic bounds will be set)
         */
        @Nullable
        Drawable handleError(@NonNull String latex, @NonNull Throwable error);
    }

    public interface BuilderConfigure {
        void configureBuilder(@NonNull Builder builder);
    }

    @NonNull
    public static JLatexMathPlugin create(float textSize) {
        return new JLatexMathPlugin(builder(textSize).build());
    }

    /**
     * @since 4.3.0-SNAPSHOT
     */
    @NonNull
    public static JLatexMathPlugin create(@Px float inlineTextSize, @Px float blockTextSize) {
        return new JLatexMathPlugin(builder(inlineTextSize, blockTextSize).build());
    }

    @NonNull
    public static JLatexMathPlugin create(@NonNull Config config) {
        return new JLatexMathPlugin(config);
    }

    @NonNull
    public static JLatexMathPlugin create(@Px float textSize, @NonNull BuilderConfigure builderConfigure) {
        final Builder builder = builder(textSize);
        builderConfigure.configureBuilder(builder);
        return new JLatexMathPlugin(builder.build());
    }

    /**
     * @since 4.3.0-SNAPSHOT
     */
    @NonNull
    public static JLatexMathPlugin create(
            @Px float inlineTextSize,
            @Px float blockTextSize,
            @NonNull BuilderConfigure builderConfigure) {
        final Builder builder = builder(inlineTextSize, blockTextSize);
        builderConfigure.configureBuilder(builder);
        return new JLatexMathPlugin(builder.build());
    }

    @NonNull
    public static JLatexMathPlugin.Builder builder(@Px float textSize) {
        return new Builder(JLatexMathTheme.builder(textSize));
    }

    /**
     * @since 4.3.0-SNAPSHOT
     */
    @NonNull
    public static JLatexMathPlugin.Builder builder(@Px float inlineTextSize, @Px float blockTextSize) {
        return new Builder(JLatexMathTheme.builder(inlineTextSize, blockTextSize));
    }

    @VisibleForTesting
    static class Config {

        // @since 4.3.0-SNAPSHOT
        final JLatexMathTheme theme;

        // @since 4.3.0-SNAPSHOT
        final RenderMode renderMode;

        // @since 4.3.0-SNAPSHOT
        final ErrorHandler errorHandler;

        final ExecutorService executorService;

        Config(@NonNull Builder builder) {
            this.theme = builder.theme.build();
            this.renderMode = builder.renderMode;
            this.errorHandler = builder.errorHandler;
            // @since 4.0.0
            ExecutorService executorService = builder.executorService;
            if (executorService == null) {
                executorService = Executors.newCachedThreadPool();
            }
            this.executorService = executorService;
        }
    }

    @VisibleForTesting
    final Config config;

    private final JLatextAsyncDrawableLoader jLatextAsyncDrawableLoader;
    private final JLatexBlockImageSizeResolver jLatexBlockImageSizeResolver;
    private final ImageSizeResolver inlineImageSizeResolver;

    @SuppressWarnings("WeakerAccess")
    JLatexMathPlugin(@NonNull Config config) {
        this.config = config;
        this.jLatextAsyncDrawableLoader = new JLatextAsyncDrawableLoader(config);
        this.jLatexBlockImageSizeResolver = new JLatexBlockImageSizeResolver(config.theme.blockFitCanvas());
        this.inlineImageSizeResolver = new InlineImageSizeResolver();
    }

    @Override
    public void configure(@NonNull Registry registry) {
        if (RenderMode.BLOCKS_AND_INLINES == config.renderMode) {
            registry.require(MarkwonInlineParserPlugin.class)
                    .factoryBuilder()
                    .addInlineProcessor(new JLatexMathInlineProcessor());
        }
    }

    @Override
    public void configureParser(@NonNull Parser.Builder builder) {

        // depending on renderMode we should register our parsing here
        // * for LEGACY -> just add custom block parser
        // * for INLINE.. -> require InlinePlugin, add inline processor + add block parser

        switch (config.renderMode) {

            case LEGACY: {
                builder.customBlockParserFactory(new JLatexMathBlockParserLegacy.Factory());
            }
            break;

            case BLOCKS_AND_INLINES: {
                builder.customBlockParserFactory(new JLatexMathBlockParser.Factory());
                // inline processor is added through `registry`
            }
            break;

            default:
                throw new RuntimeException("Unexpected `renderMode`: " + config.renderMode);
        }
    }

    @Override
    public void configureVisitor(@NonNull MarkwonVisitor.Builder builder) {
        builder.on(JLatexMathBlock.class, new MarkwonVisitor.NodeVisitor<JLatexMathBlock>() {
            @Override
            public void visit(@NonNull MarkwonVisitor visitor, @NonNull JLatexMathBlock jLatexMathBlock) {

                visitor.ensureNewLine();

                final String latex = jLatexMathBlock.latex();

                final int length = visitor.length();

                // @since 4.0.2 we cannot append _raw_ latex as a placeholder-text,
                // because Android will draw formula for each line of text, thus
                // leading to formula duplicated (drawn on each line of text)
                visitor.builder().append(prepareLatexTextPlaceholder(latex));

                final MarkwonConfiguration configuration = visitor.configuration();

                final AsyncDrawableSpan span = new AsyncDrawableSpan(
                        configuration.theme(),
                        new JLatextAsyncDrawable(
                                latex,
                                jLatextAsyncDrawableLoader,
                                jLatexBlockImageSizeResolver,
                                null,
                                true),
                        AsyncDrawableSpan.ALIGN_CENTER,
                        false);

                visitor.setSpans(length, span);

                if (visitor.hasNext(jLatexMathBlock)) {
                    visitor.ensureNewLine();
                    visitor.forceNewLine();
                }
            }
        });


        if (RenderMode.BLOCKS_AND_INLINES == config.renderMode) {

            builder.on(JLatexMathNode.class, new MarkwonVisitor.NodeVisitor<JLatexMathNode>() {
                @Override
                public void visit(@NonNull MarkwonVisitor visitor, @NonNull JLatexMathNode jLatexMathNode) {
                    final String latex = jLatexMathNode.latex();

                    final int length = visitor.length();

                    // @since 4.0.2 we cannot append _raw_ latex as a placeholder-text,
                    // because Android will draw formula for each line of text, thus
                    // leading to formula duplicated (drawn on each line of text)
                    visitor.builder().append(prepareLatexTextPlaceholder(latex));

                    final MarkwonConfiguration configuration = visitor.configuration();

                    final AsyncDrawableSpan span = new JLatexInlineAsyncDrawableSpan(
                            configuration.theme(),
                            new JLatextAsyncDrawable(
                                    latex,
                                    jLatextAsyncDrawableLoader,
                                    inlineImageSizeResolver,
                                    null,
                                    false),
                            AsyncDrawableSpan.ALIGN_CENTER,
                            false);

                    visitor.setSpans(length, span);
                }
            });
        }
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

        // @since 4.3.0-SNAPSHOT
        private final JLatexMathTheme.Builder theme;

        // @since 4.3.0-SNAPSHOT
        private RenderMode renderMode = RenderMode.BLOCKS_AND_INLINES;

        // @since 4.3.0-SNAPSHOT
        private ErrorHandler errorHandler;

        // @since 4.0.0
        private ExecutorService executorService;

        Builder(@NonNull JLatexMathTheme.Builder builder) {
            this.theme = builder;
        }

        @NonNull
        public JLatexMathTheme.Builder theme() {
            return theme;
        }

        /**
         * @since 4.3.0-SNAPSHOT
         */
        @SuppressWarnings("UnusedReturnValue")
        @NonNull
        public Builder renderMode(@NonNull RenderMode renderMode) {
            this.renderMode = renderMode;
            return this;
        }

        @SuppressWarnings("UnusedReturnValue")
        @NonNull
        public Builder errorHandler(@Nullable ErrorHandler errorHandler) {
            this.errorHandler = errorHandler;
            return this;
        }

        /**
         * @since 4.0.0
         */
        @SuppressWarnings({"UnusedReturnValue", "WeakerAccess"})
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
    static class JLatextAsyncDrawableLoader extends AsyncDrawableLoader {

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
                            // @since 4.3.0-SNAPSHOT add error handling
                            final ErrorHandler errorHandler = config.errorHandler;
                            if (errorHandler == null) {
                                // as before
                                Log.e(
                                        "JLatexMathPlugin",
                                        "Error displaying latex: `" + drawable.getDestination() + "`",
                                        t);
                            } else {
                                // just call `getDestination` without casts and checks
                                final Drawable errorDrawable = errorHandler.handleError(
                                        drawable.getDestination(),
                                        t
                                );
                                if (errorDrawable != null) {
                                    DrawableUtils.applyIntrinsicBoundsIfEmpty(errorDrawable);
                                    setResult(drawable, errorDrawable);
                                }
                            }
                        }
                    }

                    private void execute() {

                        final JLatexMathDrawable jLatexMathDrawable;

                        final JLatextAsyncDrawable jLatextAsyncDrawable = (JLatextAsyncDrawable) drawable;

                        if (jLatextAsyncDrawable.isBlock()) {
                            jLatexMathDrawable = createBlockDrawable(jLatextAsyncDrawable.getDestination());
                        } else {
                            jLatexMathDrawable = createInlineDrawable(jLatextAsyncDrawable.getDestination());
                        }

                        setResult(drawable, jLatexMathDrawable);
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

        // @since 4.3.0-SNAPSHOT
        @NonNull
        private JLatexMathDrawable createBlockDrawable(@NonNull String latex) {

            final JLatexMathTheme theme = config.theme;

            final JLatexMathTheme.BackgroundProvider backgroundProvider = theme.blockBackgroundProvider();
            final JLatexMathTheme.Padding padding = theme.blockPadding();
            final int color = theme.blockTextColor();

            final JLatexMathDrawable.Builder builder = JLatexMathDrawable.builder(latex)
                    .textSize(theme.blockTextSize())
                    .align(theme.blockHorizontalAlignment())
                    .fitCanvas(theme.blockFitCanvas());

            if (backgroundProvider != null) {
                builder.background(backgroundProvider.provide());
            }

            if (padding != null) {
                builder.padding(padding.left, padding.top, padding.right, padding.bottom);
            }

            if (color != 0) {
                builder.color(color);
            }

            return builder.build();
        }

        // @since 4.3.0-SNAPSHOT
        @NonNull
        private JLatexMathDrawable createInlineDrawable(@NonNull String latex) {

            final JLatexMathTheme theme = config.theme;

            final JLatexMathTheme.BackgroundProvider backgroundProvider = theme.inlineBackgroundProvider();
            final JLatexMathTheme.Padding padding = theme.inlinePadding();
            final int color = theme.inlineTextColor();

            final JLatexMathDrawable.Builder builder = JLatexMathDrawable.builder(latex)
                    .textSize(theme.inlineTextSize())
                    .fitCanvas(false);

            if (backgroundProvider != null) {
                builder.background(backgroundProvider.provide());
            }

            if (padding != null) {
                builder.padding(padding.left, padding.top, padding.right, padding.bottom);
            }

            if (color != 0) {
                builder.color(color);
            }

            return builder.build();
        }

        // @since 4.3.0-SNAPSHOT
        private void setResult(@NonNull final AsyncDrawable drawable, @NonNull final Drawable result) {
            // we must post to handler, but also have a way to identify the drawable
            // for which we are posting (in case of cancellation)
            handler.postAtTime(new Runnable() {
                @Override
                public void run() {
                    // remove entry from cache (it will be present if task is not cancelled)
                    if (cache.remove(drawable) != null
                            && drawable.isAttached()) {
                        drawable.setResult(result);
                    }

                }
            }, drawable, SystemClock.uptimeMillis());
        }
    }

    private static class InlineImageSizeResolver extends ImageSizeResolver {

        @NonNull
        @Override
        public Rect resolveImageSize(@NonNull AsyncDrawable drawable) {
            return drawable.getResult().getBounds();
        }
    }
}
