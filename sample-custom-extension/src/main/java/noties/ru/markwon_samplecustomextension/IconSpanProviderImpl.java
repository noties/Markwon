package noties.ru.markwon_samplecustomextension;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

public class IconSpanProviderImpl implements IconSpanProvider {

    private static final boolean IS_L = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;

    private final Context context;
    private final Resources resources;
    private final int fallBack;

    public IconSpanProviderImpl(@NonNull Context context, @DrawableRes int fallBack) {
        this.context = context;
        this.resources = context.getResources();
        this.fallBack = fallBack;
    }

    @NonNull
    @Override
    public IconSpan provide(@NonNull String name, @NonNull String color, @NonNull String size) {
        final String resName = materialIconName(name, color, size);
        int resId = resources.getIdentifier(resName, "drawable", context.getPackageName());
        if (resId == 0) {
            resId = fallBack;
        }
        return new IconSpan(getDrawable(resId), IconSpan.ALIGN_CENTER);
    }

    @NonNull
    private static String materialIconName(@NonNull String name, @NonNull String color, @NonNull String size) {
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
