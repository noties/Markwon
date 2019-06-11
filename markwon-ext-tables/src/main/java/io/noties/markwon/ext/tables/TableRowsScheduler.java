package io.noties.markwon.ext.tables;

import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

abstract class TableRowsScheduler {

    static void schedule(@NonNull final TextView view) {
        final Object[] spans = extract(view);
        if (spans != null
                && spans.length > 0) {

            if (view.getTag(R.id.markwon_tables_scheduler) == null) {
                final View.OnAttachStateChangeListener listener = new View.OnAttachStateChangeListener() {
                    @Override
                    public void onViewAttachedToWindow(View v) {

                    }

                    @Override
                    public void onViewDetachedFromWindow(View v) {
                        unschedule(view);
                        view.removeOnAttachStateChangeListener(this);
                        view.setTag(R.id.markwon_tables_scheduler, null);
                    }
                };
                view.addOnAttachStateChangeListener(listener);
                view.setTag(R.id.markwon_tables_scheduler, listener);
            }

            final TableRowSpan.Invalidator invalidator = new TableRowSpan.Invalidator() {
                @Override
                public void invalidate() {
                    view.setText(view.getText());
                }
            };

            for (Object span : spans) {
                ((TableRowSpan) span).invalidator(invalidator);
            }
        }
    }

    static void unschedule(@NonNull TextView view) {
        final Object[] spans = extract(view);
        if (spans != null
                && spans.length > 0) {
            for (Object span : spans) {
                ((TableRowSpan) span).invalidator(null);
            }
        }
    }

    @Nullable
    private static Object[] extract(@NonNull TextView view) {
        final Object[] out;
        final CharSequence text = view.getText();
        if (!TextUtils.isEmpty(text) && text instanceof Spanned) {
            out = ((Spanned) text).getSpans(0, text.length(), TableRowSpan.class);
        } else {
            out = null;
        }
        return out;
    }

    private TableRowsScheduler() {
    }
}
