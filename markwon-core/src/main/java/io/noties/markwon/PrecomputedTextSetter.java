package io.noties.markwon;

import android.os.AsyncTask;
import android.os.Build;
import android.text.PrecomputedText;
import android.text.Spanned;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.lang.ref.WeakReference;
import java.util.concurrent.Executor;

/**
 * @see io.noties.markwon.Markwon.TextSetter
 * @since 4.1.0-SNAPSHOT
 */
@RequiresApi(Build.VERSION_CODES.P)
public class PrecomputedTextSetter implements Markwon.TextSetter {

    @NonNull
    public static PrecomputedTextSetter create() {
        return create(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @NonNull
    public static PrecomputedTextSetter create(@NonNull Executor executor) {
        return new PrecomputedTextSetter(executor);
    }

    private final Executor executor;

    @SuppressWarnings("WeakerAccess")
    PrecomputedTextSetter(@NonNull Executor executor) {
        this.executor = executor;
    }

    @Override
    public void setText(
            @NonNull TextView textView,
            @NonNull final Spanned markdown,
            @NonNull final TextView.BufferType bufferType,
            @NonNull final Runnable onComplete) {
        final WeakReference<TextView> reference = new WeakReference<>(textView);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                final PrecomputedText precomputedText = precomputedText(reference.get(), markdown);
                if (precomputedText != null) {
                    apply(reference.get(), precomputedText, bufferType, onComplete);
                }
            }
        });
    }

    @Nullable
    private static PrecomputedText precomputedText(@Nullable TextView textView, @NonNull Spanned spanned) {
        return textView == null
                ? null
                : PrecomputedText.create(spanned, textView.getTextMetricsParams());
    }

    private static void apply(
            @Nullable final TextView textView,
            @NonNull final PrecomputedText precomputedText,
            @NonNull final TextView.BufferType bufferType,
            @NonNull final Runnable onComplete) {
        if (textView != null) {
            textView.post(new Runnable() {
                @Override
                public void run() {
                    textView.setText(precomputedText, bufferType);
                    onComplete.run();
                }
            });
        }
    }
}
