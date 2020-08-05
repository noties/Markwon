package io.noties.markwon.movement;

import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.core.CorePlugin;

/**
 * @since 3.0.0
 */
public class MovementMethodPlugin extends AbstractMarkwonPlugin {

    /**
     * Creates plugin that will ensure that there is movement method registered on a TextView.
     * Uses Android system LinkMovementMethod as default
     *
     * @see #create(MovementMethod)
     * @see #link()
     * @deprecated 4.5.0 use {@link #link()}
     */
    @NonNull
    @Deprecated
    public static MovementMethodPlugin create() {
        return create(LinkMovementMethod.getInstance());
    }

    /**
     * @since 4.5.0
     */
    @NonNull
    public static MovementMethodPlugin link() {
        return create(LinkMovementMethod.getInstance());
    }

    /**
     * Special {@link MovementMethodPlugin} that is <strong>not</strong> applying a MovementMethod on a TextView
     * implicitly
     *
     * @since 4.5.0
     */
    @NonNull
    public static MovementMethodPlugin none() {
        return new MovementMethodPlugin(null);
    }

    @NonNull
    public static MovementMethodPlugin create(@NonNull MovementMethod movementMethod) {
        return new MovementMethodPlugin(movementMethod);
    }

    @Nullable
    private final MovementMethod movementMethod;

    /**
     * Since 4.5.0 change to be <em>nullable</em>
     */
    @SuppressWarnings("WeakerAccess")
    MovementMethodPlugin(@Nullable MovementMethod movementMethod) {
        this.movementMethod = movementMethod;
    }

    @Override
    public void configure(@NonNull Registry registry) {
        registry.require(CorePlugin.class)
                .hasExplicitMovementMethod(true);
    }

    @Override
    public void beforeSetText(@NonNull TextView textView, @NonNull Spanned markdown) {
        // @since 4.5.0 check for equality
        final MovementMethod current = textView.getMovementMethod();
        if (current != movementMethod) {
            textView.setMovementMethod(movementMethod);
        }
    }
}
