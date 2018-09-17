package ru.noties.markwon.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import ru.noties.markwon.SpannableConfiguration;

public class MarkwonViewCompat extends AppCompatTextView implements IMarkwonView {

    private MarkwonViewHelper helper;

    public MarkwonViewCompat(Context context) {
        super(context);
        init(context, null);
    }

    public MarkwonViewCompat(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {
        helper = MarkwonViewHelper.create(this);
        helper.init(context, attributeSet);
    }

    @Override
    public void setConfigurationProvider(@NonNull ConfigurationProvider provider) {
        helper.setConfigurationProvider(provider);
    }

    @Override
    public void setMarkdown(@Nullable String markdown) {
        helper.setMarkdown(markdown);
    }

    @Override
    public void setMarkdown(@Nullable SpannableConfiguration configuration, @Nullable String markdown) {
        helper.setMarkdown(configuration, markdown);
    }

    @Nullable
    @Override
    public String getMarkdown() {
        return helper.getMarkdown();
    }
}
