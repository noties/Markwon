package ru.noties.markwon;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.view.View;

@SuppressWarnings("WeakerAccess")
public abstract class Views {

    @IntDef({View.INVISIBLE, View.GONE})
    @interface NotVisible {
    }

    public static void setVisible(@NonNull View view, boolean visible) {
        setVisible(view, visible, View.GONE);
    }

    public static void setVisible(@NonNull View view, boolean visible, @NotVisible int notVisible) {
        final int visibility = visible
                ? View.VISIBLE
                : notVisible;
        view.setVisibility(visibility);
    }

    private Views() {
    }
}
