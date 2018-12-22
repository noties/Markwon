package ru.noties.markwon;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * @since 3.0.0
 */
public interface RenderProps {

    @Nullable
    <T> T get(@NonNull Prop<T> prop);

    @NonNull
    <T> T get(@NonNull Prop<T> prop, @NonNull T defValue);

    <T> void set(@NonNull Prop<T> prop, @Nullable T value);

    <T> void clear(@NonNull Prop<T> prop);
}
