package io.noties.markwon;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Class to hold data in {@link RenderProps}. Represents a certain <em>property</em>.
 *
 * @param <T> represents the type that this instance holds
 * @see #of(String)
 * @see #of(Class, String)
 * @since 3.0.0
 */
public class Prop<T> {

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

    Prop(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public String name() {
        return name;
    }

    @Nullable
    public T get(@NonNull RenderProps props) {
        return props.get(this);
    }

    @NonNull
    public T get(@NonNull RenderProps props, @NonNull T defValue) {
        return props.get(this, defValue);
    }

    @NonNull
    public T require(@NonNull RenderProps props) {
        final T t = get(props);
        if (t == null) {
            throw new NullPointerException(name);
        }
        return t;
    }

    public void set(@NonNull RenderProps props, @Nullable T value) {
        props.set(this, value);
    }

    public void clear(@NonNull RenderProps props) {
        props.clear(this);
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
