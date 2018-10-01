package ru.noties.markwon.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.noties.markwon.SpannableConfiguration;

public interface IMarkwonView {

    interface ConfigurationProvider {
        @NonNull
        SpannableConfiguration provide(@NonNull Context context);
    }

    void setConfigurationProvider(@NonNull ConfigurationProvider provider);

    void setMarkdown(@Nullable String markdown);
    void setMarkdown(@Nullable SpannableConfiguration configuration, @Nullable String markdown);

    @Nullable
    String getMarkdown();
}
