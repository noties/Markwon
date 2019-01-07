package ru.noties.markwon.image;

import android.support.annotation.NonNull;

class AsyncDrawableLoaderNoOp extends AsyncDrawableLoader {
    @Override
    public void load(@NonNull String destination, @NonNull AsyncDrawable drawable) {

    }

    @Override
    public void cancel(@NonNull String destination) {

    }
}
