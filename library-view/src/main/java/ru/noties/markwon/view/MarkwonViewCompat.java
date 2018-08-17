package ru.noties.markwon.view;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import ru.noties.markwon.SpannableConfiguration;

public class MarkwonViewCompat extends AppCompatTextView implements IMarkwonView {

    private MarkwonViewHelper helper;

    public MarkwonViewCompat(@NonNull Context context) {
        super(context);
        init(context, null, R.attr.markwonViewStyle);
    }

    public MarkwonViewCompat(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, R.attr.markwonViewStyle);
    }

    public MarkwonViewCompat(@NonNull Context context,
                             @Nullable AttributeSet attrs,
                             @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(@NonNull Context context,
                      @Nullable AttributeSet attributeSet,
                      @AttrRes int defStyleAttr) {
        helper = MarkwonViewHelper.create(this);
        helper.init(context, attributeSet, defStyleAttr, 0);
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
