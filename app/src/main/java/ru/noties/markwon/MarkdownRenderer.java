package ru.noties.markwon;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;

import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.inject.Inject;

import ru.noties.markwon.renderer.SpannableRenderer;

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
                    urlProcessor = null;
                } else {
                    urlProcessor = new UrlProcessorRelativeToAbsolute(uri.toString());
                }

                final SpannableConfiguration configuration = SpannableConfiguration.builder(context)
                        .asyncDrawableLoader(loader)
                        .urlProcessor(urlProcessor)
                        .build();

                final Parser parser = Parser.builder()
                        .extensions(Collections.singleton(StrikethroughExtension.create()))
                        .build();

                final Node node = parser.parse(markdown);
                final SpannableRenderer renderer = new SpannableRenderer();
                final CharSequence text = renderer.render(configuration, node);

//                final CharSequence text = Markwon.markdown(configuration, markdown);
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
