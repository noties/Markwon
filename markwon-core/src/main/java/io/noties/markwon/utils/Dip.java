package io.noties.markwon.utils;

import android.content.Context;

import androidx.annotation.NonNull;

public class Dip {

    @NonNull
    public static Dip create(@NonNull Context context) {
        return new Dip(context.getResources().getDisplayMetrics().density);
    }

    @NonNull
    public static Dip create(float density) {
        return new Dip(density);
    }

    private final float density;

    @SuppressWarnings("WeakerAccess")
    public Dip(float density) {
        this.density = density;
    }

    public int toPx(int dp) {
        return (int) (dp * density + .5F);
    }
}
