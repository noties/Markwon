package ru.noties.markwon;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.FontRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.inject.Inject;

import ru.noties.debug.Debug;
import ru.noties.markwon.spans.AsyncDrawable;
import ru.noties.markwon.spans.SpannableTheme;
import ru.noties.markwon.spans.heading.HeadingConfig;
import ru.noties.markwon.spans.heading.HeadingTypeConfig;

@ActivityScope
public class MarkdownRenderer {

    private final Context context;

    @Inject
    AsyncDrawable.Loader loader;

    @Inject
    ExecutorService service;

    @Inject
    Handler handler;

    private Future<?> task;

    @Inject
    MarkdownRenderer(Context context) {
        this.context = context;
    }

    public void render(
        @NonNull final Context context,
        @Nullable final Uri uri,
        @NonNull final String markdown,
        @NonNull final MarkdownReadyListener listener) {
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
                    .theme(getSpannableTheme())
                    .asyncDrawableLoader(loader)
                    .urlProcessor(urlProcessor)
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

    private HeadingConfig getHeadingConfig() {
        final HeadingTypeConfig h1 = new HeadingTypeConfig(-1, Color.RED, getTypeface(R.font.opensans_semibold));
        final HeadingTypeConfig h2 = new HeadingTypeConfig(-1, Color.BLUE, getTypeface(R.font.opensans_regular));

        return new HeadingConfig(h1, h2);
    }

    private SpannableTheme getSpannableTheme() {
        return SpannableTheme.builderWithDefaults(context)
            .headingConfig(getHeadingConfig(), context.getResources().getDisplayMetrics().density)
            .build();
    }

    private Typeface getTypeface(@FontRes int font){
        return ResourcesCompat.getFont(context, font);
    }

    interface MarkdownReadyListener {
        void onMarkdownReady(CharSequence markdown);
    }
}
