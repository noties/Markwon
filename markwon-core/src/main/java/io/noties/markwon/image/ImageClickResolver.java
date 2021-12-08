package io.noties.markwon.image;

import android.view.View;

import androidx.annotation.NonNull;

public interface ImageClickResolver {
    void clickResolve(@NonNull View view, @NonNull String link);
}
