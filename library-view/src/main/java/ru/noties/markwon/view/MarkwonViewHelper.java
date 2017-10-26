package ru.noties.markwon.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import ru.noties.markwon.Markwon;
import ru.noties.markwon.SpannableConfiguration;

public class MarkwonViewHelper implements IMarkwonView {

    public static <V extends TextView> MarkwonViewHelper create(@NonNull V view) {
        return new MarkwonViewHelper(view);
    }

    private final TextView mTextView;

    private ConfigurationProvider mProvider;

    private SpannableConfiguration mConfiguration;
    private String mMarkdown;

    private MarkwonViewHelper(@NonNull TextView textView) {
        mTextView = textView;
    }

    public void init(Context context, AttributeSet attributeSet) {

        if (attributeSet != null) {
            final TypedArray array = context.obtainStyledAttributes(attributeSet, R.styleable.MarkwonView);
            try {

                final String configurationProvider = array.getString(R.styleable.MarkwonView_mv_configurationProvider);
                final ConfigurationProvider provider;
                if (!TextUtils.isEmpty(configurationProvider)) {
                    provider = MarkwonViewHelper.obtainProvider(configurationProvider);
                } else {
                    provider = null;
                }
                if (provider != null) {
                    setConfigurationProvider(provider);
                }

                final String markdown = array.getString(R.styleable.MarkwonView_mv_markdown);
                if (!TextUtils.isEmpty(markdown)) {
                    setMarkdown(markdown);
                }
            } finally {
                array.recycle();
            }
        }
    }

    @Override
    public void setConfigurationProvider(@NonNull ConfigurationProvider provider) {
        mProvider = provider;
        mConfiguration = provider.provide(mTextView.getContext());
        if (!TextUtils.isEmpty(mMarkdown)) {
            // invalidate rendered markdown
            setMarkdown(mMarkdown);
        }
    }

    @Override
    public void setMarkdown(@Nullable String markdown) {
        setMarkdown(null, markdown);
    }

    @Override
    public void setMarkdown(@Nullable SpannableConfiguration configuration, @Nullable String markdown) {
        mMarkdown = markdown;
        if (configuration == null) {
            if (mConfiguration == null) {
                if (mProvider != null) {
                    mConfiguration = mProvider.provide(mTextView.getContext());
                } else {
                    mConfiguration = SpannableConfiguration.create(mTextView.getContext());
                }
            }
            configuration = mConfiguration;
        }
        Markwon.setMarkdown(mTextView, configuration, markdown);
    }

    @Nullable
    @Override
    public String getMarkdown() {
        return mMarkdown;
    }

    @Nullable
    public static IMarkwonView.ConfigurationProvider obtainProvider(@NonNull String className) {
        try {
            final Class<?> cl = Class.forName(className);
            return (IMarkwonView.ConfigurationProvider) cl.newInstance();
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }
}
