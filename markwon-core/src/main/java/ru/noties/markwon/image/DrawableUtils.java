package ru.noties.markwon.image;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

/**
 * @since 3.0.1-SNAPSHOT
 */
public abstract class DrawableUtils {

    @NonNull
    public static Rect intrinsicBounds(@NonNull Drawable drawable) {
        return new Rect(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
    }

    public static void applyIntrinsicBoundsIfEmpty(@NonNull Drawable drawable) {
        if (drawable.getBounds().isEmpty()) {
            drawable.setBounds(intrinsicBounds(drawable));
        }
    }

    private DrawableUtils() {
    }
}
