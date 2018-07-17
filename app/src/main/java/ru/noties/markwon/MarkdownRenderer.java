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
    Prism4jTheme prism4jTheme;

    private Future<?> task;

    @Inject
    MarkdownRenderer() {
    }

    public void render(
            @NonNull final Context context,
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

                final SpannableConfiguration configuration = SpannableConfiguration.builder(context)
                        .asyncDrawableLoader(loader)
                        .urlProcessor(urlProcessor)
                        .syntaxHighlight(Prism4jSyntaxHighlight.create(prism4j, prism4jTheme))
                        .theme(SpannableTheme.builderWithDefaults(context)
                                .codeBackgroundColor(prism4jTheme.background())
                                .codeTextColor(prism4jTheme.textColor())
                                .build())
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
