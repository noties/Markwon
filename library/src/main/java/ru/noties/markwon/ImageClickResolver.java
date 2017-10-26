package ru.noties.markwon;

import android.support.annotation.NonNull;
import android.view.View;

/**
 * @author pa.gulko zTrap (25.10.2017)
 * @since 1.0.1
 */
public interface ImageClickResolver {

    void resolve(View view, @NonNull String link);
}
