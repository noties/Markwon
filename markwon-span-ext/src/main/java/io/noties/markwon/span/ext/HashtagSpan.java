package io.noties.markwon.span.ext;

import android.text.TextPaint;
import android.text.style.URLSpan;
import android.view.View;

import androidx.annotation.NonNull;

import io.noties.markwon.LinkResolver;

public class HashtagSpan extends URLSpan {
    private final String link;
    private final LinkResolver resolver;
    private final int linkColor;

    public HashtagSpan(
            @NonNull String link,
            @NonNull int linkColor,
            @NonNull LinkResolver resolver) {
        super(link);
        this.link = link;
        this.linkColor = linkColor;
        this.resolver = resolver;
    }

    @Override
    public void onClick(View widget) {
        resolver.resolve(widget, link);
    }

    @Override
    public void updateDrawState(@NonNull TextPaint ds) {
        applyLinkStyle(ds);
    }

    private void applyLinkStyle(TextPaint paint){
        paint.setUnderlineText(true);
        paint.setColor(linkColor);
    }

    /**
     * @since 4.2.0
     */
    @NonNull
    public String getLink() {
        return link;
    }
}
