package io.noties.markwon.utils;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

/**
 * @deprecated Please use {@link io.noties.markwon.image.DrawableUtils}
 */
@Deprecated
public abstract class DrawableUtils {

    public static void intrinsicBounds(@NonNull Drawable drawable) {
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
    }

    private DrawableUtils() {
    }
}
