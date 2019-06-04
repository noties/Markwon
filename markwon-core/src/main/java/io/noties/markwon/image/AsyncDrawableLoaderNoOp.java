package io.noties.markwon.image;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

class AsyncDrawableLoaderNoOp extends AsyncDrawableLoader {
    @Override
    public void load(@NonNull AsyncDrawable drawable) {

    }

    @Override
    public void cancel(@NonNull AsyncDrawable drawable) {

    }

    @Nullable
    @Override
    public Drawable placeholder(@NonNull AsyncDrawable drawable) {
        return null;
    }
}
