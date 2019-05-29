package ru.noties.markwon.image;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Spanned;
import android.view.View;
import android.widget.TextView;

import ru.noties.markwon.renderer.R;

public abstract class AsyncDrawableScheduler {

    public static void schedule(@NonNull final TextView textView) {

        // we need a simple check if current text has already scheduled drawables
        // we need this in order to allow multiple calls to schedule (different plugins
        // might use AsyncDrawable), but we do not want to repeat the task
        //
        // hm... we need the same thing for unschedule then... we can check if last hash is !null,
        // if it's not -> unschedule, else ignore

        final Integer lastTextHashCode =
                (Integer) textView.getTag(R.id.markwon_drawables_scheduler_last_text_hashcode);
        final int textHashCode = textView.getText().hashCode();
        if (lastTextHashCode != null
                && lastTextHashCode == textHashCode) {
            return;
        }
        textView.setTag(R.id.markwon_drawables_scheduler_last_text_hashcode, textHashCode);


        final AsyncDrawableSpan[] spans = extractSpans(textView);
        if (spans != null
                && spans.length > 0) {

            if (textView.getTag(R.id.markwon_drawables_scheduler) == null) {
                final View.OnAttachStateChangeListener listener = new View.OnAttachStateChangeListener() {
                    @Override
                    public void onViewAttachedToWindow(View v) {

                    }

                    @Override
                    public void onViewDetachedFromWindow(View v) {
                        unschedule(textView);
                        v.removeOnAttachStateChangeListener(this);
                        v.setTag(R.id.markwon_drawables_scheduler, null);
                    }
                };
                textView.addOnAttachStateChangeListener(listener);
                textView.setTag(R.id.markwon_drawables_scheduler, listener);
            }

            AsyncDrawable drawable;

            for (AsyncDrawableSpan span : spans) {
                drawable = span.getDrawable();
                drawable.setCallback2(new DrawableCallbackImpl(textView, drawable.getBounds()));
            }
        }
    }

    // must be called when text manually changed in TextView
    public static void unschedule(@NonNull TextView view) {

        if (view.getTag(R.id.markwon_drawables_scheduler_last_text_hashcode) == null) {
            return;
        }
        view.setTag(R.id.markwon_drawables_scheduler_last_text_hashcode, null);


        final AsyncDrawableSpan[] spans = extractSpans(view);
        if (spans != null
                && spans.length > 0) {
            for (AsyncDrawableSpan span : spans) {
                span.getDrawable().setCallback2(null);
            }
        }
    }

    @Nullable
    private static AsyncDrawableSpan[] extractSpans(@NonNull TextView textView) {

        final CharSequence cs = textView.getText();
        final int length = cs != null
                ? cs.length()
                : 0;

        if (length == 0
                || !(cs instanceof Spanned)) {
            return null;
        }

        // we also could've tried the `nextSpanTransition`, but strangely it leads to worse performance
        // then direct getSpans

        return ((Spanned) cs).getSpans(0, length, AsyncDrawableSpan.class);
    }

    private AsyncDrawableScheduler() {
    }

    private static class DrawableCallbackImpl implements Drawable.Callback {

        private final TextView view;
        private Rect previousBounds;

        DrawableCallbackImpl(TextView view, Rect initialBounds) {
            this.view = view;
            this.previousBounds = new Rect(initialBounds);
        }

        @Override
        public void invalidateDrawable(@NonNull final Drawable who) {

            if (Looper.myLooper() != Looper.getMainLooper()) {
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        invalidateDrawable(who);
                    }
                });
                return;
            }

            final Rect rect = who.getBounds();

            // okay... the thing is IF we do not change bounds size, normal invalidate would do
            // but if the size has changed, then we need to update the whole layout...

            if (!previousBounds.equals(rect)) {
                // the only method that seems to work when bounds have changed
                view.setText(view.getText());
                previousBounds = new Rect(rect);
            } else {

                view.postInvalidate();
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
