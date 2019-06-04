package ru.noties.markwon.movement;

import androidx.annotation.NonNull;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.widget.TextView;

import ru.noties.markwon.AbstractMarkwonPlugin;

/**
 * @since 3.0.0
 */
public class MovementMethodPlugin extends AbstractMarkwonPlugin {

    /**
     * Creates plugin that will ensure that there is movement method registered on a TextView.
     * Uses Android system LinkMovementMethod as default
     *
     * @see #create(MovementMethod)
     */
    @NonNull
    public static MovementMethodPlugin create() {
        return create(LinkMovementMethod.getInstance());
    }

    @NonNull
    public static MovementMethodPlugin create(@NonNull MovementMethod movementMethod) {
        return new MovementMethodPlugin(movementMethod);
    }

    private final MovementMethod movementMethod;

    @SuppressWarnings("WeakerAccess")
    MovementMethodPlugin(@NonNull MovementMethod movementMethod) {
        this.movementMethod = movementMethod;
    }

    @Override
    public void beforeSetText(@NonNull TextView textView, @NonNull Spanned markdown) {
        textView.setMovementMethod(movementMethod);
    }
}
