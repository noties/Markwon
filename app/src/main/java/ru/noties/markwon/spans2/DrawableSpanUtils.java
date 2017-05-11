package ru.noties.markwon.spans2;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.text.Spanned;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.noties.debug.Debug;

public class DrawableSpanUtils {

    // this method is not completely valid because DynamicDrawableSpan stores
    // a drawable in weakReference & it could easily be freed, thus we might need
    // to re-schedule a new one, but we have no means to do it
    public static void scheduleDrawables(@NonNull final TextView textView) {

        final CharSequence cs = textView.getText();
        final int length = cs != null
                ? cs.length()
                : 0;
        if (length == 0 || !(cs instanceof Spanned)) {
            return;
        }

        final Object[] spans = ((Spanned) cs).getSpans(0, length, Object.class);
        if (spans != null
                && spans.length > 0) {

            final List<Drawable> list = new ArrayList<>(2);

            for (Object span: spans) {
                if (span instanceof DrawableSpan) {
                    list.add(((DrawableSpan) span).getDrawable());
                }
            }

            if (list.size() > 0) {
                textView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                    @Override
                    public void onViewAttachedToWindow(View v) {
                        // can it happen that the same view first detached & them attached with all previous content? hm..
                        // no op for now
                    }

                    @Override
                    public void onViewDetachedFromWindow(View v) {
                        // remove callbacks...
                        textView.removeOnAttachStateChangeListener(this);
                        for (Drawable drawable: list) {
                            drawable.setCallback(null);
                        }
                    }
                });

                for (Drawable drawable: list) {
                    drawable.setCallback(new DrawableCallbackImpl(textView, drawable.getBounds()));
                }
            }
        }
    }

    private DrawableSpanUtils() {}

    private static class DrawableCallbackImpl implements Drawable.Callback {

        private final TextView view;
        private Rect previousBounds;

        DrawableCallbackImpl(TextView view, Rect initialBounds) {
            this.view = view;
            this.previousBounds = new Rect(initialBounds);
        }

        @Override
        public void invalidateDrawable(@NonNull Drawable who) {

            // okay... teh thing is IF we do not change bounds size, normal invalidate would do
            // but if the size has changed, then we need to update the whole layout...

            final Rect rect = who.getBounds();

            if (!previousBounds.equals(rect)) {
                // the only method that seems to work when bounds have changed
                view.setText(view.getText());
                previousBounds = new Rect(rect);
            } else {
                // if bounds are the same then simple invalidate would do
                final int scrollX = view.getScrollX();
                final int scrollY = view.getScrollY();
                view.postInvalidate(
                        scrollX + rect.left,
                        scrollY + rect.top,
                        scrollX + rect.right,
                        scrollY + rect.bottom
                );
            }
        }

        @Override
        public void scheduleDrawable(@NonNull Drawable who, @NonNull Runnable what, long when) {
            final long delay = when - SystemClock.uptimeMillis();
            view.postDelayed(what, delay);
        }

        @Override
        public void unscheduleDrawable(@NonNull Drawable who, @NonNull Runnable what) {
            view.removeCallbacks(what);
        }
    }
}
