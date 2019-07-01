package io.noties.markwon.app;

import android.view.View;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;

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
