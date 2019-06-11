package io.noties.markwon.image;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;

/**
 * @since 3.0.1
 */
public abstract class DrawableUtils {

    @NonNull
    @CheckResult
    public static Rect intrinsicBounds(@NonNull Drawable drawable) {
        return new Rect(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
    }

    public static void applyIntrinsicBounds(@NonNull Drawable drawable) {
        drawable.setBounds(intrinsicBounds(drawable));
    }

    public static void applyIntrinsicBoundsIfEmpty(@NonNull Drawable drawable) {
        if (drawable.getBounds().isEmpty()) {
            drawable.setBounds(intrinsicBounds(drawable));
        }
    }

    private DrawableUtils() {
    }
}
