package ru.noties.markwon.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.widget.TextView;

import ru.noties.markwon.SpannableConfiguration;

@SuppressLint("AppCompatCustomView")
public class MarkwonView extends TextView implements IMarkwonView {

    private MarkwonViewHelper helper;

    public MarkwonView(@NonNull Context context) {
        super(context);
        init(context, null, R.attr.markwonViewStyle, 0);
    }

    public MarkwonView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, R.attr.markwonViewStyle, 0);
    }

    public MarkwonView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MarkwonView(Context context,
                       @Nullable AttributeSet attrs,
                       int defStyleAttr,
                       int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(@NonNull Context context,
                      @Nullable AttributeSet attributeSet,
                      @AttrRes int defStyleAttr,
                      @StyleRes int defStyleRes) {
        helper = MarkwonViewHelper.create(this);
        helper.init(context, attributeSet, defStyleAttr, defStyleRes);
    }

    @Override
    public void setConfigurationProvider(@NonNull ConfigurationProvider provider) {
        helper.setConfigurationProvider(provider);
    }

    public void setMarkdown(@Nullable String markdown) {
        helper.setMarkdown(markdown);
    }

    public void setMarkdown(@Nullable SpannableConfiguration configuration, @Nullable String markdown) {
        helper.setMarkdown(configuration, markdown);
    }

    @Nullable
    @Override
    public String getMarkdown() {
        return helper.getMarkdown();
    }
}
