package io.noties.markwon.sample.customextension;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

@SuppressWarnings("WeakerAccess")
public abstract class IconSpanProvider {

    @SuppressWarnings("SameParameterValue")
    @NonNull
    public static IconSpanProvider create(@NonNull Context context, @DrawableRes int fallBack) {
        return new Impl(context, fallBack);
    }


    @NonNull
    public abstract IconSpan provide(@NonNull String name, @NonNull String color, @NonNull String size);


    private static class Impl extends IconSpanProvider {

        private static final boolean IS_L = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;

        private final Context context;
        private final Resources resources;
        private final int fallBack;

        Impl(@NonNull Context context, @DrawableRes int fallBack) {
            this.context = context;
            this.resources = context.getResources();
            this.fallBack = fallBack;
        }

        @NonNull
        @Override
        public IconSpan provide(@NonNull String name, @NonNull String color, @NonNull String size) {
            final String resName = iconName(name, color, size);
            int resId = resources.getIdentifier(resName, "drawable", context.getPackageName());
            if (resId == 0) {
                resId = fallBack;
            }
            return new IconSpan(getDrawable(resId), IconSpan.ALIGN_CENTER);
        }


        @NonNull
        private static String iconName(@NonNull String name, @NonNull String color, @NonNull String size) {
            return "ic_" + name + "_" + color + "_" + size + "dp";
        }

        @NonNull
        private Drawable getDrawable(int resId) {
            final Drawable drawable;
            if (IS_L) {
                drawable = context.getDrawable(resId);
            } else {
                drawable = resources.getDrawable(resId);
            }
            //noinspection ConstantConditions
            return drawable;
        }
    }
}
