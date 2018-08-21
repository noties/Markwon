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
import ru.noties.markwon.spans.AsyncDrawable;
import ru.noties.markwon.spans.SpannableTheme;
import ru.noties.markwon.syntax.Prism4jSyntaxHighlight;
import ru.noties.markwon.syntax.Prism4jTheme;
import ru.noties.markwon.syntax.Prism4jThemeDarkula;
import ru.noties.markwon.syntax.Prism4jThemeDefault;
import ru.noties.prism4j.Prism4j;

@ActivityScope
public class MarkdownRenderer {

    interface MarkdownReadyListener {
        void onMarkdownReady(CharSequence markdown);
    }

    @Inject
    AsyncDrawable.Loader loader;

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

                final int background = isLightTheme
                        ? prism4jTheme.background()
                        : 0x0Fffffff;

                final GifPlaceholder gifPlaceholder = new GifPlaceholder(
                        context.getResources().getDrawable(R.drawable.ic_play_circle_filled_18dp_white),
                        0x20000000
                );

                final int red = 0xFFff0000;

                final SpannableConfiguration configuration = SpannableConfiguration.builder(context)
                        .asyncDrawableLoader(loader)
                        .urlProcessor(urlProcessor)
                        .syntaxHighlight(Prism4jSyntaxHighlight.create(prism4j, prism4jTheme))
                        .theme(SpannableTheme.builderWithDefaults(context)
                                .codeBackgroundColor(background)
                                .codeTextColor(prism4jTheme.textColor())
                                .listItemColor(red)
                                .build())
                        .factory(new GifAwareSpannableFactory(gifPlaceholder))
                        .trimWhiteSpaceEnd(false)
                        .build();

                final long start = SystemClock.uptimeMillis();

                final CharSequence text = Markwon.markdown(configuration, markdown);

                final long end = SystemClock.uptimeMillis();

                Debug.i("markdown rendered: %d ms", end - start);

                if (!isCancelled()) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (!isCancelled()) {
                                listener.onMarkdownReady(text);
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
