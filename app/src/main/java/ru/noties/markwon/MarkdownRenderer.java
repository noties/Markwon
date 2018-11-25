package ru.noties.markwon;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.inject.Inject;

import ru.noties.debug.Debug;
import ru.noties.markwon.core.CorePlugin;
import ru.noties.markwon.image.ImagesPlugin;
import ru.noties.markwon.image.gif.GifPlugin;
import ru.noties.markwon.image.svg.SvgPlugin;
import ru.noties.markwon.syntax.Prism4jTheme;
import ru.noties.markwon.syntax.Prism4jThemeDarkula;
import ru.noties.markwon.syntax.Prism4jThemeDefault;
import ru.noties.markwon.syntax.SyntaxHighlightPlugin;
import ru.noties.prism4j.Prism4j;

@ActivityScope
public class MarkdownRenderer {

    interface MarkdownReadyListener {
        void onMarkdownReady(@NonNull Markwon2 markwon2, CharSequence markdown);
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

                final UrlProcessor urlProcessor;
                if (uri == null) {
                    urlProcessor = new UrlProcessorInitialReadme();
                } else {
                    urlProcessor = new UrlProcessorRelativeToAbsolute(uri.toString());
                }

                final Prism4jTheme prism4jTheme = isLightTheme
                        ? prism4jThemeDefault
                        : prism4JThemeDarkula;

//                final int background = isLightTheme
//                        ? prism4jTheme.background()
//                        : 0x0Fffffff;

                final Markwon2 markwon2 = Markwon2.builder(context)
                        .use(CorePlugin.create())
                        .use(ImagesPlugin.createWithAssets(context))
                        .use(SvgPlugin.create(context.getResources()))
                        .use(GifPlugin.create(false))
                        .use(SyntaxHighlightPlugin.create(prism4j, prism4jTheme))
                        .use(GifAwarePlugin.create(context))
                        .use(new AbstractMarkwonPlugin() {
                            @Override
                            public void configureConfiguration(@NonNull MarkwonConfiguration.Builder builder) {
                                builder.urlProcessor(urlProcessor);
                            }
                        })
                        .build();

                final long start = SystemClock.uptimeMillis();

                final CharSequence text = markwon2.toMarkdown(markdown);

                final long end = SystemClock.uptimeMillis();

                Debug.i("markdown rendered: %d ms", end - start);

                if (!isCancelled()) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (!isCancelled()) {
                                listener.onMarkdownReady(markwon2, text);
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
