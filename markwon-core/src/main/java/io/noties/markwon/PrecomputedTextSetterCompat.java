package io.noties.markwon;

import android.os.Build;
import android.text.Spanned;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.text.PrecomputedTextCompat;

import java.lang.ref.WeakReference;
import java.util.concurrent.Executor;

/**
 * Please note this class requires `androidx.core:core` artifact being explicitly added to your dependencies.
 * Please do not use with `markwon-recycler` as it will lead to bad item rendering (due to async nature)
 *
 * @see io.noties.markwon.Markwon.TextSetter
 * @since 4.1.0
 */
public class PrecomputedTextSetterCompat implements Markwon.TextSetter {

    /**
     * @param executor for background execution of text pre-computation
     */
    @NonNull
    public static PrecomputedTextSetterCompat create(@NonNull Executor executor) {
        return new PrecomputedTextSetterCompat(executor);
    }

    private final Executor executor;

    @SuppressWarnings("WeakerAccess")
    PrecomputedTextSetterCompat(@NonNull Executor executor) {
        this.executor = executor;
    }

    @Override
    public void setText(
            @NonNull TextView textView,
            @NonNull final Spanned markdown,
            @NonNull final TextView.BufferType bufferType,
            @NonNull final Runnable onComplete) {

        // insert version check and do not execute on a device < 21
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            // it's still no-op, so there is no need to start background execution
            applyText(textView, markdown, bufferType, onComplete);
            return;
        }

        final WeakReference<TextView> reference = new WeakReference<>(textView);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final PrecomputedTextCompat precomputedTextCompat = precomputedText(reference.get(), markdown);
                    if (precomputedTextCompat != null) {
                        applyText(reference.get(), precomputedTextCompat, bufferType, onComplete);
                    }
                } catch (Throwable t) {
                    Log.e("PrecomputdTxtSetterCmpt", "Exception during pre-computing text", t);
                    // apply initial markdown
                    applyText(reference.get(), markdown, bufferType, onComplete);
                }
            }
        });
    }

    @Nullable
    private static PrecomputedTextCompat precomputedText(@Nullable TextView textView, @NonNull Spanned spanned) {

        if (textView == null) {
            return null;
        }

        final PrecomputedTextCompat.Params params;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // use native parameters on P
            params = new PrecomputedTextCompat.Params(textView.getTextMetricsParams());
        } else {

            final PrecomputedTextCompat.Params.Builder builder =
                    new PrecomputedTextCompat.Params.Builder(textView.getPaint());

            // please note that text-direction initialization is omitted
            // by default it will be determined by the first locale-specific character

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // another miss on API surface, this can easily be done by the compat class itself
                builder
                        .setBreakStrategy(textView.getBreakStrategy())
                        .setHyphenationFrequency(textView.getHyphenationFrequency());
            }

            params = builder.build();
        }

        return PrecomputedTextCompat.create(spanned, params);
    }

    private static void applyText(
            @Nullable final TextView textView,
            @NonNull final Spanned text,
            @NonNull final TextView.BufferType bufferType,
            @NonNull final Runnable onComplete) {
        if (textView != null) {
            textView.post(new Runnable() {
                @Override
                public void run() {
                    textView.setText(text, bufferType);
                    onComplete.run();
                }
            });
        }
    }
}
