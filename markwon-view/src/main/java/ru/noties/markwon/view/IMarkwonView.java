package ru.noties.markwon.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.noties.markwon.MarkwonConfiguration;

public interface IMarkwonView {

    interface ConfigurationProvider {
        @NonNull
        MarkwonConfiguration provide(@NonNull Context context);
    }

    void setConfigurationProvider(@NonNull ConfigurationProvider provider);

    void setMarkdown(@Nullable String markdown);
    void setMarkdown(@Nullable MarkwonConfiguration configuration, @Nullable String markdown);

    @Nullable
    String getMarkdown();
}
