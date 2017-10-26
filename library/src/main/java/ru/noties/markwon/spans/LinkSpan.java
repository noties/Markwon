package ru.noties.markwon.spans;

import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

public class LinkSpan extends ClickableSpan {

    public interface Resolver {
        void resolve(View view, @NonNull String link);
    }

    private final SpannableTheme mTheme;
    private final String mLink;
    private final Resolver mResolver;

    public LinkSpan(@NonNull SpannableTheme theme, @NonNull String link, @NonNull Resolver resolver) {
        mTheme = theme;
        mLink = link;
        mResolver = resolver;
    }

    @Override
    public void onClick(View widget) {
        mResolver.resolve(widget, mLink);
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        mTheme.applyLinkStyle(ds);
    }
}
