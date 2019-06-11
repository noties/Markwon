package io.noties.markwon.app.gif;

import android.text.Spannable;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.noties.markwon.image.AsyncDrawableSpan;
import pl.droidsonroids.gif.GifDrawable;

public abstract class GifProcessor {

    public abstract void process(@NonNull TextView textView);

    @NonNull
    public static GifProcessor create() {
        return new Impl();
    }

    static class Impl extends GifProcessor {

        @Override
        public void process(@NonNull final TextView textView) {

            // here is what we will do additionally:
            // we query for all asyncDrawableSpans
            // we check if they are inside clickableSpan
            // if not we apply onGifListener

            final Spannable spannable = spannable(textView);

            if (spannable == null) {
                return;
            }

            final AsyncDrawableSpan[] asyncDrawableSpans =
                    spannable.getSpans(0, spannable.length(), AsyncDrawableSpan.class);
            if (asyncDrawableSpans == null
                    || asyncDrawableSpans.length == 0) {
                return;
            }

            int start;
            int end;
            ClickableSpan[] clickableSpans;

            for (final AsyncDrawableSpan asyncDrawableSpan : asyncDrawableSpans) {

                start = spannable.getSpanStart(asyncDrawableSpan);
                end = spannable.getSpanEnd(asyncDrawableSpan);

                if (start < 0
                        || end < 0) {
                    continue;
                }

                clickableSpans = spannable.getSpans(start, end, ClickableSpan.class);
                if (clickableSpans != null
                        && clickableSpans.length > 0) {
                    continue;
                }

                ((GifAwareAsyncDrawable) asyncDrawableSpan.getDrawable()).onGifResultListener(new GifAwareAsyncDrawable.OnGifResultListener() {
                    @Override
                    public void onGifResult(@NonNull GifAwareAsyncDrawable drawable) {
                        addGifClickSpan(textView, asyncDrawableSpan, drawable);
                    }
                });
            }
        }

        @Nullable
        private static Spannable spannable(@NonNull TextView textView) {
            final CharSequence charSequence = textView.getText();
            if (charSequence instanceof Spannable) {
                return (Spannable) charSequence;
            }
            return null;
        }

        private static void addGifClickSpan(
                @NonNull TextView textView,
                @NonNull AsyncDrawableSpan span,
                @NonNull GifAwareAsyncDrawable drawable) {

            // important thing here is to obtain new spannable from textView
            // as with each `setText()` new spannable is created and keeping reference
            // to an older one won't affect textView
            final Spannable spannable = spannable(textView);

            if (spannable == null) {
                return;
            }

            final int start = spannable.getSpanStart(span);
            final int end = spannable.getSpanEnd(span);
            if (start < 0
                    || end < 0) {
                return;
            }

            final GifDrawable gifDrawable = (GifDrawable) drawable.getResult();
            spannable.setSpan(new GifToggleClickableSpan(gifDrawable), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        private static class GifToggleClickableSpan extends ClickableSpan {

            private final GifDrawable gifDrawable;

            GifToggleClickableSpan(@NonNull GifDrawable gifDrawable) {
                this.gifDrawable = gifDrawable;
            }

            @Override
            public void onClick(@NonNull View widget) {
                if (gifDrawable.isPlaying()) {
                    gifDrawable.pause();
                } else {
                    gifDrawable.start();
                }
                widget.invalidate();
            }
        }
    }
}
