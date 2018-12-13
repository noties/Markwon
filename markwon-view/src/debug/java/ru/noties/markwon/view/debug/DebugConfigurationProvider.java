package ru.noties.markwon.view.debug;

import android.content.Context;
import android.support.annotation.NonNull;

import ru.noties.markwon.MarkwonConfiguration;
import ru.noties.markwon.core.MarkwonTheme;
import ru.noties.markwon.view.IMarkwonView;

public class DebugConfigurationProvider implements IMarkwonView.ConfigurationProvider {

    private MarkwonConfiguration cached;

    @NonNull
    @Override
    public MarkwonConfiguration provide(@NonNull Context context) {
        if (cached == null) {
            cached = MarkwonConfiguration.builder(context)
                    .theme(debugTheme(context))
                    .build();
        }
        return cached;
    }

    private static MarkwonTheme debugTheme(@NonNull Context context) {
        return MarkwonTheme.builderWithDefaults(context)
                .blockQuoteColor(0xFFff0000)
                .codeBackgroundColor(0x40FF0000)
                .build();
    }
}
