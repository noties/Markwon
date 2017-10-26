package ru.noties.markwon.view.debug;

import android.content.Context;
import android.support.annotation.NonNull;

import ru.noties.markwon.SpannableConfiguration;
import ru.noties.markwon.spans.SpannableTheme;
import ru.noties.markwon.view.IMarkwonView;

public class DebugConfigurationProvider implements IMarkwonView.ConfigurationProvider {

    private SpannableConfiguration mCached;

    @NonNull
    @Override
    public SpannableConfiguration provide(@NonNull Context context) {
        if (mCached == null) {
            mCached = SpannableConfiguration.builder(context)
                    .theme(debugTheme(context))
                    .build();
        }
        return mCached;
    }

    private static SpannableTheme debugTheme(@NonNull Context context) {
        return SpannableTheme.builderWithDefaults(context)
                .blockQuoteColor(0xFFff0000)
                .codeBackgroundColor(0x40FF0000)
                .build();
    }
}
