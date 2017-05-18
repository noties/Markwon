package ru.noties.markwon;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;

import java.util.Collections;

import ru.noties.markwon.renderer.SpannableRenderer;

@SuppressWarnings("WeakerAccess")
public abstract class Markwon {

    public static Parser createParser() {
        return new Parser.Builder()
                .extensions(Collections.singleton(StrikethroughExtension.create()))
                .build();
    }

    public static void setMarkdown(@NonNull TextView view, @NonNull String markdown) {
        setMarkdown(view, SpannableConfiguration.create(view.getContext()), markdown);
    }

    public static void setMarkdown(
            @NonNull TextView view,
            @NonNull SpannableConfiguration configuration,
            @Nullable String markdown
    ) {

        setText(view, markdown(configuration, markdown));
    }

    public static void setText(@NonNull TextView view, CharSequence text) {

        unscheduleDrawables(view);

        // update movement method (for links to be clickable)
        view.setMovementMethod(LinkMovementMethod.getInstance());
        view.setText(text);

        // schedule drawables (dynamic drawables that can change bounds/animate will be correctly updated)
        scheduleDrawables(view);
    }

    // with default configuration
    public static CharSequence markdown(@NonNull Context context, @Nullable String markdown) {
        final CharSequence out;
        if (TextUtils.isEmpty(markdown)) {
            out = null;
        } else {
            final SpannableConfiguration configuration = SpannableConfiguration.create(context);
            out = markdown(configuration, markdown);
        }
        return out;
    }

    public static CharSequence markdown(@NonNull SpannableConfiguration configuration, @Nullable String markdown) {
        final CharSequence out;
        if (TextUtils.isEmpty(markdown)) {
            out = null;
        } else {
            final Parser parser = createParser();
            final Node node = parser.parse(markdown);
            final SpannableRenderer renderer = new SpannableRenderer();
            out = renderer.render(configuration, node);
        }
        return out;
    }

    public static void scheduleDrawables(@NonNull TextView view) {
        DrawablesScheduler.schedule(view);
    }

    public static void unscheduleDrawables(@NonNull TextView view) {
        DrawablesScheduler.unschedule(view);
    }

    private Markwon() {
    }
}
