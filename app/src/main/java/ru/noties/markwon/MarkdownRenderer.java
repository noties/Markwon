package ru.noties.markwon;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.inject.Inject;

@ActivityScope
public class MarkdownRenderer {

    interface MarkdownReadyListener {
        void onMarkdownReady(CharSequence markdown);
    }

    @Inject
    AsyncDrawableLoader loader;

    @Inject
    ExecutorService service;

    @Inject
    Handler handler;

    private Future<?> task;

    @Inject
    MarkdownRenderer() {
    }

    public void render(@NonNull final Context context, @NonNull final String markdown, @NonNull final MarkdownReadyListener listener) {
        cancel();
        task = service.submit(new Runnable() {
            @Override
            public void run() {
                final SpannableConfiguration configuration = SpannableConfiguration.builder(context)
                        .asyncDrawableLoader(loader)
                        .build();
                final CharSequence text = Markwon.markdown(configuration, markdown);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onMarkdownReady(text);
                    }
                });
                task = null;
            }
        });
    }

    public void cancel() {
        if (task != null) {
            task.cancel(true);
            task = null;
        }
    }
}
