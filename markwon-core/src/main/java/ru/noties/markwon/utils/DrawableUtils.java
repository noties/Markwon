package ru.noties.markwon.utils;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

public abstract class DrawableUtils {

    public static void intrinsicBounds(@NonNull Drawable drawable) {
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
    }

    private DrawableUtils() {}
}
