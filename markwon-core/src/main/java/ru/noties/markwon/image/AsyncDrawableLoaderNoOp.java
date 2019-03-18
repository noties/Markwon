package ru.noties.markwon.image;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

class AsyncDrawableLoaderNoOp extends AsyncDrawableLoader {
    @Override
    public void load(@NonNull String destination, @NonNull AsyncDrawable drawable) {

    }

    @Override
    public void cancel(@NonNull String destination) {

    }

    @Nullable
    @Override
    public Drawable placeholder() {
        return null;
    }
}
