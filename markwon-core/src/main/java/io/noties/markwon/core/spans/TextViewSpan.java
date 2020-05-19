package io.noties.markwon.core.spans;

import android.text.Spannable;
import android.text.Spanned;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

/**
 * A special span that allows to obtain {@code TextView} in which spans are displayed
 *
 * @since 4.4.0
 */
public class TextViewSpan {

    @Nullable
    public static TextView textViewOf(@NonNull CharSequence cs) {
        if (cs instanceof Spanned) {
            return textViewOf((Spanned) cs);
        }
        return null;
    }

    @Nullable
    public static TextView textViewOf(@NonNull Spanned spanned) {
        final TextViewSpan[] spans = spanned.getSpans(0, spanned.length(), TextViewSpan.class);
        return spans != null && spans.length > 0
                ? spans[0].textView()
                : null;
    }

    public static void applyTo(@NonNull Spannable spannable, @NonNull TextView textView) {

        final TextViewSpan[] spans = spannable.getSpans(0, spannable.length(), TextViewSpan.class);
        if (spans != null) {
            for (TextViewSpan span : spans) {
                spannable.removeSpan(span);
            }
        }

        final TextViewSpan span = new TextViewSpan(textView);
        // `SPAN_INCLUSIVE_INCLUSIVE` to persist in case of possible text change (deletion, etc)
        spannable.setSpan(
                span,
                0,
                spannable.length(),
                Spanned.SPAN_INCLUSIVE_INCLUSIVE
        );
    }

    private final WeakReference<TextView> reference;

    public TextViewSpan(@NonNull TextView textView) {
        this.reference = new WeakReference<>(textView);
    }

    @Nullable
    public TextView textView() {
        return reference.get();
    }
}
