package io.noties.markwon.app;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Spanned;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.inject.Inject;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.MarkwonConfiguration;
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin;
import io.noties.markwon.ext.tables.TablePlugin;
import io.noties.markwon.ext.tasklist.TaskListPlugin;
import io.noties.markwon.app.gif.GifAwarePlugin;
import io.noties.markwon.html.HtmlPlugin;
import io.noties.markwon.image.ImagesPlugin;
import io.noties.markwon.image.data.DataUriSchemeHandler;
import io.noties.markwon.image.file.FileSchemeHandler;
import io.noties.markwon.image.gif.GifMediaDecoder;
import io.noties.markwon.image.network.OkHttpNetworkSchemeHandler;
import io.noties.markwon.image.svg.SvgMediaDecoder;
import io.noties.markwon.syntax.Prism4jTheme;
import io.noties.markwon.syntax.Prism4jThemeDarkula;
import io.noties.markwon.syntax.Prism4jThemeDefault;
import io.noties.markwon.syntax.SyntaxHighlightPlugin;
import io.noties.markwon.urlprocessor.UrlProcessor;
import io.noties.markwon.urlprocessor.UrlProcessorRelativeToAbsolute;
import io.noties.debug.Debug;
import ru.noties.prism4j.Prism4j;

@ActivityScope
public class MarkdownRenderer {

    interface MarkdownReadyListener {
        void onMarkdownReady(@NonNull Markwon markwon, Spanned markdown);
    }

    @Inject
    ExecutorService service;

    @Inject
    Handler handler;

    @Inject
    Prism4j prism4j;

    @Inject
    Prism4jThemeDefault prism4jThemeDefault;

    @Inject
    Prism4jThemeDarkula prism4JThemeDarkula;

    private Future<?> task;

    @Inject
    MarkdownRenderer() {
    }

    public void render(
            @NonNull final Context context,
            final boolean isLightTheme,
            @Nullable final Uri uri,
            @NonNull final String markdown,
            @NonNull final MarkdownReadyListener listener) {

        // todo: create prism4j theme factory (accepting light/dark argument)

        cancel();

        task = service.submit(new Runnable() {

            @Override
            public void run() {
                try {
                    execute();
                } catch (Throwable t) {
                    Debug.e(t);
                }
            }

            private void execute() {
                final UrlProcessor urlProcessor;
                if (uri == null) {
                    urlProcessor = new UrlProcessorInitialReadme();
                } else {
                    urlProcessor = new UrlProcessorRelativeToAbsolute(uri.toString());
                }

                final Prism4jTheme prism4jTheme = isLightTheme
                        ? prism4jThemeDefault
                        : prism4JThemeDarkula;

                final Markwon markwon = Markwon.builder(context)
                        .usePlugin(ImagesPlugin.create(new ImagesPlugin.ImagesConfigure() {
                            @Override
                            public void configureImages(@NonNull ImagesPlugin plugin) {
                                plugin
                                        .addSchemeHandler(DataUriSchemeHandler.create())
                                        .addSchemeHandler(OkHttpNetworkSchemeHandler.create())
                                        .addSchemeHandler(FileSchemeHandler.createWithAssets(context.getAssets()))
                                        .addMediaDecoder(GifMediaDecoder.create(false))
                                        .addMediaDecoder(SvgMediaDecoder.create());
                            }
                        }))
                        .usePlugin(SyntaxHighlightPlugin.create(prism4j, prism4jTheme))
                        .usePlugin(GifAwarePlugin.create(context))
                        .usePlugin(TablePlugin.create(context))
                        .usePlugin(TaskListPlugin.create(context))
                        .usePlugin(StrikethroughPlugin.create())
                        .usePlugin(HtmlPlugin.create())
                        .usePlugin(new AbstractMarkwonPlugin() {
                            @Override
                            public void configureConfiguration(@NonNull MarkwonConfiguration.Builder builder) {
                                builder.urlProcessor(urlProcessor);
                            }
                        })
                        .build();

                final long start = SystemClock.uptimeMillis();

                final Spanned text = markwon.toMarkdown(markdown);

                final long end = SystemClock.uptimeMillis();

                Debug.i("markdown rendered: %d ms", end - start);

                if (!isCancelled()) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (!isCancelled()) {
                                listener.onMarkdownReady(markwon, text);
                                task = null;
                            }
                        }
                    });
                }
            }
        });
    }

    public void cancel() {
        if (task != null) {
            task.cancel(true);
            task = null;
        }
    }

    private boolean isCancelled() {
        return task == null || task.isCancelled();
    }
}
