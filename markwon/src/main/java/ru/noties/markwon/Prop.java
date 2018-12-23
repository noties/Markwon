package ru.noties.markwon;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Class to hold data in {@link RenderProps}. Represents a certain <em>property</em>.
 *
 * @param <T> represents the type that this instance holds
 * @see #of(String)
 * @see #of(Class, String)
 * @since 3.0.0
 */
public final class Prop<T> {

    @SuppressWarnings("unused")
    @NonNull
    public static <T> Prop<T> of(@NonNull Class<T> type, @NonNull String name) {
        return new Prop<>(name);
    }

    @NonNull
    public static <T> Prop<T> of(@NonNull String name) {
        return new Prop<>(name);
    }

    private final String name;

    private Prop(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public String name() {
        return name;
    }

    @Nullable
    public T get(@NonNull RenderProps context) {
        return context.get(this);
    }

    @NonNull
    public T get(@NonNull RenderProps context, @NonNull T defValue) {
        return context.get(this, defValue);
    }

    @NonNull
    public T require(@NonNull RenderProps context) {
        final T t = get(context);
        if (t == null) {
            throw new NullPointerException();
        }
        return t;
    }

    public void set(@NonNull RenderProps context, @Nullable T value) {
        context.set(this, value);
    }

    public void clear(@NonNull RenderProps context) {
        context.clear(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Prop<?> prop = (Prop<?>) o;

        return name.equals(prop.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "Prop{" +
                "name='" + name + '\'' +
                '}';
    }
}
