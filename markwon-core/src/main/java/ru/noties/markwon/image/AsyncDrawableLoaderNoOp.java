package ru.noties.markwon.image;

import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
