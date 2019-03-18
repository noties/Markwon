package ru.noties.markwon.core.spans;

import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.view.View;

import ru.noties.markwon.core.MarkwonTheme;

public class LinkSpan extends URLSpan {

    public interface Resolver {
        void resolve(View view, @NonNull String link);
    }

    private final MarkwonTheme theme;
    private final String link;
    private final Resolver resolver;

    public LinkSpan(@NonNull MarkwonTheme theme, @NonNull String link, @NonNull Resolver resolver) {
        super(link);
        this.theme = theme;
        this.link = link;
        this.resolver = resolver;
    }

    @Override
    public void onClick(View widget) {
        resolver.resolve(widget, link);
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        theme.applyLinkStyle(ds);
    }
}
