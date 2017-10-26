package ru.noties.markwon.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

import ru.noties.markwon.SpannableConfiguration;

@SuppressLint("AppCompatCustomView")
public class MarkwonView extends TextView implements IMarkwonView {

    private MarkwonViewHelper mHelper;

    public MarkwonView(Context context) {
        super(context);
        init(context, null);
    }

    public MarkwonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {
        mHelper = MarkwonViewHelper.create(this);
        mHelper.init(context, attributeSet);
    }

    @Override
    public void setConfigurationProvider(@NonNull ConfigurationProvider provider) {
        mHelper.setConfigurationProvider(provider);
    }

    public void setMarkdown(@Nullable String markdown) {
        mHelper.setMarkdown(markdown);
    }

    public void setMarkdown(@Nullable SpannableConfiguration configuration, @Nullable String markdown) {
        mHelper.setMarkdown(configuration, markdown);
    }

    @Nullable
    @Override
    public String getMarkdown() {
        return mHelper.getMarkdown();
    }
}
