package io.noties.markwon.core.spans;

import android.text.Layout;
import android.text.Spannable;
import android.text.Spanned;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

/**
 * @since 4.4.0
 */
public class TextLayoutSpan {

    /**
     * @see #applyTo(Spannable, Layout)
     */
    @Nullable
    public static Layout layoutOf(@NonNull CharSequence cs) {
        if (cs instanceof Spanned) {
            return layoutOf((Spanned) cs);
        }
        return null;
    }

    @Nullable
    public static Layout layoutOf(@NonNull Spanned spanned) {
        final TextLayoutSpan[] spans = spanned.getSpans(
                0,
                spanned.length(),
                TextLayoutSpan.class
        );
        return spans != null && spans.length > 0
                ? spans[0].layout()
                : null;
    }

    public static void applyTo(@NonNull Spannable spannable, @NonNull Layout layout) {

        // remove all current ones (only one should be present)
        final TextLayoutSpan[] spans = spannable.getSpans(0, spannable.length(), TextLayoutSpan.class);
        if (spans != null) {
            for (TextLayoutSpan span : spans) {
                spannable.removeSpan(span);
            }
        }

        final TextLayoutSpan span = new TextLayoutSpan(layout);
        spannable.setSpan(
                span,
                0,
                spannable.length(),
                Spanned.SPAN_INCLUSIVE_INCLUSIVE
        );
    }

    private final WeakReference<Layout> reference;

    @SuppressWarnings("WeakerAccess")
    TextLayoutSpan(@NonNull Layout layout) {
        this.reference = new WeakReference<>(layout);
    }

    @Nullable
    public Layout layout() {
        return reference.get();
    }
}
