package io.noties.markwon.core.spans;

import android.text.TextPaint;
import android.text.style.URLSpan;
import android.view.View;

import androidx.annotation.NonNull;

import io.noties.markwon.LinkResolver;
import io.noties.markwon.core.MarkwonTheme;

public class LinkSpan extends URLSpan {

    private final MarkwonTheme theme;
    private final String link;
    private final LinkResolver resolver;

    public LinkSpan(
            @NonNull MarkwonTheme theme,
            @NonNull String link,
            @NonNull LinkResolver resolver) {
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
    public void updateDrawState(@NonNull TextPaint ds) {
        theme.applyLinkStyle(ds);
    }

    /**
     * @since 4.2.0
     */
    @NonNull
    public String getLink() {
        return link;
    }
}
