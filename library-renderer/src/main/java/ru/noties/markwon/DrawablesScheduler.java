package ru.noties.markwon;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.text.Spanned;
import android.text.style.DynamicDrawableSpan;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.noties.markwon.spans.AsyncDrawable;
import ru.noties.markwon.spans.AsyncDrawableSpan;

abstract class DrawablesScheduler {

    static void schedule(@NonNull final TextView textView) {

        final List<AsyncDrawable> list = extract(textView);
        if (list.size() > 0) {
            textView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View v) {

                }

                @Override
                public void onViewDetachedFromWindow(View v) {
                    // we obtain a new list in case text was changed
                    unschedule(textView);
                }
            });

            for (AsyncDrawable d : list) {
                d.setCallback2(new DrawableCallbackImpl(textView, d.getBounds()));
            }
        }
    }

    // must be called when text manually changed in TextView
    static void unschedule(@NonNull TextView view) {
        for (AsyncDrawable d : extract(view)) {
            d.setCallback2(null);
        }
    }

    private static List<AsyncDrawable> extract(@NonNull TextView view) {

        final List<AsyncDrawable> list;

        final CharSequence cs = view.getText();
        final int length = cs != null
                ? cs.length()
                : 0;

        if (length == 0 || !(cs instanceof Spanned)) {
            //noinspection unchecked
            list = Collections.EMPTY_LIST;
        } else {

            final Object[] spans = ((Spanned) cs).getSpans(0, length, Object.class);
            if (spans != null
                    && spans.length > 0) {

                list = new ArrayList<>(2);

                for (Object span : spans) {
                    if (span instanceof AsyncDrawableSpan) {
                        list.add(((AsyncDrawableSpan) span).getDrawable());
                    } else if (span instanceof DynamicDrawableSpan) {
                        // it's really not optimal thing because it stores Drawable in WeakReference...
                        // which is why it will be most likely already de-referenced...
                        final Drawable d = ((DynamicDrawableSpan) span).getDrawable();
                        if (d != null
                                && d instanceof AsyncDrawable) {
                            list.add((AsyncDrawable) d);
                        }
                    }
                }
            } else {
                //noinspection unchecked
                list = Collections.EMPTY_LIST;
            }
        }

        return list;
    }

    private DrawablesScheduler() {
    }

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
