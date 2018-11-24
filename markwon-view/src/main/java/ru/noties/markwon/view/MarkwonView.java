package ru.noties.markwon.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

import ru.noties.markwon.MarkwonConfiguration;

@SuppressLint("AppCompatCustomView")
public class MarkwonView extends TextView implements IMarkwonView {

    private MarkwonViewHelper helper;

    public MarkwonView(Context context) {
        super(context);
        init(context, null);
    }

    public MarkwonView(Context context, AttributeSet attrs) {
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

    public void setMarkdown(@Nullable String markdown) {
        helper.setMarkdown(markdown);
    }

    public void setMarkdown(@Nullable MarkwonConfiguration configuration, @Nullable String markdown) {
        helper.setMarkdown(configuration, markdown);
    }

    @Nullable
    @Override
    public String getMarkdown() {
        return helper.getMarkdown();
    }
}
