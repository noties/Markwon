package ru.noties.markwon;

import android.app.Activity;
import android.support.annotation.IdRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.view.View;

@SuppressWarnings("WeakerAccess")
public abstract class Views {

    @IntDef({View.INVISIBLE, View.GONE})
    @interface NotVisible {
    }

    public static <V extends View> V findView(@NonNull View view, @IdRes int id) {
        //noinspection unchecked
        return (V) view.findViewById(id);
    }

    public static <V extends View> V findView(@NonNull Activity activity, @IdRes int id) {
        //noinspection unchecked
        return (V) activity.findViewById(id);
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
