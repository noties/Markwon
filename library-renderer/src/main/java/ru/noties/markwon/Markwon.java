package ru.noties.markwon;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.TextView;

import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;

import java.util.Collections;

import ru.noties.markwon.renderer.SpannableRenderer;

@SuppressWarnings("WeakerAccess")
public abstract class Markwon {

    public static void scheduleDrawables(@NonNull TextView view) {
        DrawablesScheduler.schedule(view);
    }

    public static void unscheduleDrawables(@NonNull TextView view) {
        DrawablesScheduler.unschedule(view);
    }

    // with default configuration
    public static CharSequence markdown(@NonNull Context context, @Nullable String text) {
        final CharSequence out;
        if (TextUtils.isEmpty(text)) {
            out = null;
        } else {
            final SpannableConfiguration configuration = SpannableConfiguration.create(context);
            out = markdown(configuration, text);
        }
        return out;
    }

    public static CharSequence markdown(@NonNull SpannableConfiguration configuration, @Nullable String text) {
        final CharSequence out;
        if (TextUtils.isEmpty(text)) {
            out = null;
        } else {
            final Parser parser = new Parser.Builder()
                    .extensions(Collections.singleton(StrikethroughExtension.create()))
                    .build();
            final Node node = parser.parse(text);
            final SpannableRenderer renderer = new SpannableRenderer();
            out = renderer.render(configuration, node);
        }
        return out;
    }

    private Markwon() {
    }
}
