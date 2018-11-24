package ru.noties.markwon.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import ru.noties.markwon.Markwon;
import ru.noties.markwon.MarkwonConfiguration;

public class MarkwonViewHelper implements IMarkwonView {

    public static <V extends TextView> MarkwonViewHelper create(@NonNull V view) {
        return new MarkwonViewHelper(view);
    }

    private final TextView textView;

    private ConfigurationProvider provider;

    private MarkwonConfiguration configuration;
    private String markdown;

    private MarkwonViewHelper(@NonNull TextView textView) {
        this.textView = textView;
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
        this.provider = provider;
        this.configuration = provider.provide(textView.getContext());
        if (!TextUtils.isEmpty(markdown)) {
            // invalidate rendered toMarkdown
            setMarkdown(markdown);
        }
    }

    @Override
    public void setMarkdown(@Nullable String markdown) {
        setMarkdown(null, markdown);
    }

    @Override
    public void setMarkdown(@Nullable MarkwonConfiguration configuration, @Nullable String markdown) {
        this.markdown = markdown;
        if (configuration == null) {
            if (this.configuration == null) {
                if (provider != null) {
                    this.configuration = provider.provide(textView.getContext());
                } else {
                    this.configuration = MarkwonConfiguration.create(textView.getContext());
                }
            }
            configuration = this.configuration;
        }
        Markwon.setMarkdown(textView, configuration, markdown);
    }

    @Nullable
    @Override
    public String getMarkdown() {
        return markdown;
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
