package io.noties.markwon;

import android.text.Spanned;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.text.PrecomputedTextCompat;

import java.util.concurrent.Executor;

/**
 * Please note this class requires `androidx.core:core` artifact being explicitly added to your dependencies.
 * This is intended to be used in a RecyclerView.
 *
 * @see io.noties.markwon.Markwon.TextSetter
 */
public class PrecomputedFutureTextSetterCompat implements Markwon.TextSetter {

    /**
     * @param executor for background execution of text pre-computation,
     *                 if not provided the standard, single threaded one will be used.
     */
    @NonNull
    public static PrecomputedFutureTextSetterCompat create(@Nullable Executor executor) {
        return new PrecomputedFutureTextSetterCompat(executor);
    }

    @NonNull
    public static PrecomputedFutureTextSetterCompat create() {
        return new PrecomputedFutureTextSetterCompat(null);
    }

    @Nullable
    private final Executor executor;

    @SuppressWarnings("WeakerAccess")
    PrecomputedFutureTextSetterCompat(@Nullable Executor executor) {
        this.executor = executor;
    }

    @Override
    public void setText(
            @NonNull TextView textView,
            @NonNull Spanned markdown,
            @NonNull TextView.BufferType bufferType,
            @NonNull Runnable onComplete) {

        if (textView instanceof AppCompatTextView) {
            ((AppCompatTextView) textView).setTextFuture(PrecomputedTextCompat.getTextFuture(
                    markdown, ((AppCompatTextView) textView).getTextMetricsParamsCompat(), executor
            ));

            onComplete.run();
        } else {
            Log.w("Markwon, no-op", "PrecomputedFutureTextSetterCompat: textView provided is not an AppCompatTextView!");
        }
    }
}
