package ru.noties.markwon.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.FontRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.annotation.StyleableRes;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.MetricAffectingSpan;
import android.text.style.TextAppearanceSpan;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.widget.TextView;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import ru.noties.markwon.Markwon;
import ru.noties.markwon.SpannableConfiguration;
import ru.noties.markwon.SpannableFactory;
import ru.noties.markwon.SpannableFactoryDef;
import ru.noties.markwon.renderer.ImageSize;
import ru.noties.markwon.renderer.ImageSizeResolver;
import ru.noties.markwon.spans.AsyncDrawable;
import ru.noties.markwon.spans.LinkSpan;
import ru.noties.markwon.spans.SpannableTheme;
import ru.noties.markwon.spans.TableRowSpan;

public class MarkwonViewHelper implements IMarkwonView {

    public static <V extends TextView> MarkwonViewHelper create(@NonNull V view) {
        return new MarkwonViewHelper(view);
    }

    private final TextView textView;

    private ConfigurationProvider provider;

    private SpannableConfiguration configuration;
    private String markdown;

    @NonNull
    private final SparseIntArray styles;

    private MarkwonViewHelper(@NonNull TextView textView) {
        this.textView = textView;
        this.styles = new SparseIntArray();
    }

    public void init(@NonNull Context context,
                     @Nullable AttributeSet attributeSet,
                     @AttrRes int defStyleAttr,
                     @StyleRes int defStyleRes) {
        final TypedArray array = context.obtainStyledAttributes(
                attributeSet,
                R.styleable.MarkwonView,
                defStyleAttr,
                defStyleRes);
        try {
            final int count = array.getIndexCount();
            for (int idx = 0; idx < count; idx++) {
                @StyleableRes final int relativeIndex = array.getIndex(idx);

                styles.put(relativeIndex, array.getResourceId(relativeIndex, 0));
            }

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

    @Override
    public void setConfigurationProvider(@NonNull ConfigurationProvider provider) {
        this.provider = provider;
        this.configuration = provider.provide(textView.getContext());
        if (!TextUtils.isEmpty(markdown)) {
            // invalidate rendered markdown
            setMarkdown(markdown);
        }
    }

    @Override
    public void setMarkdown(@Nullable String markdown) {
        setMarkdown(null, markdown);
    }

    @Override
    public void setMarkdown(@Nullable SpannableConfiguration configuration, @Nullable String markdown) {
        this.markdown = markdown;
        if (configuration == null) {
            if (this.configuration == null) {
                if (provider != null) {
                    this.configuration = provider.provide(textView.getContext());
                } else {
                    this.configuration = SpannableConfiguration
                            .builder(textView.getContext())
                            .factory(new StyleableSpanFactory(textView.getContext(), styles))
                            .build();
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
    private static IMarkwonView.ConfigurationProvider obtainProvider(@NonNull String className) {
        try {
            final Class<?> cl = Class.forName(className);
            return (IMarkwonView.ConfigurationProvider) cl.newInstance();
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }

    /**
     * A SpannableFactory that creates spans based on style attributes of MarkwonView
     */
    private static final class StyleableSpanFactory implements SpannableFactory {
        @NonNull
        private static final int[] TEXT_APPEARANCE_ATTR = new int[]{android.R.attr.textAppearance};

        private static final int NO_VALUE = -1;

        @NonNull
        private static final SparseIntArray HEADING_STYLE_MAP = new SparseIntArray();

        static {
            // Unlike library attributes, the index of styleables are stable across builds making
            // them safe to declare statically.
            HEADING_STYLE_MAP.put(1, R.styleable.MarkwonView_mv_H1Style);
            HEADING_STYLE_MAP.put(2, R.styleable.MarkwonView_mv_H2Style);
            HEADING_STYLE_MAP.put(3, R.styleable.MarkwonView_mv_H3Style);
            HEADING_STYLE_MAP.put(4, R.styleable.MarkwonView_mv_H4Style);
            HEADING_STYLE_MAP.put(5, R.styleable.MarkwonView_mv_H5Style);
            HEADING_STYLE_MAP.put(6, R.styleable.MarkwonView_mv_H6Style);
        }

        @NonNull
        private final Context context;

        @NonNull
        private final SparseIntArray styles;

        @NonNull
        private final SpannableFactory defaultFactory;

        StyleableSpanFactory(@NonNull Context context, @NonNull SparseIntArray styles) {
            this.context = context;
            this.styles = styles;
            this.defaultFactory = SpannableFactoryDef.create();

        }

        @Nullable
        @Override
        public Object strongEmphasis() {
            final int style = styles.get(R.styleable.MarkwonView_mv_StrongEmphasisStyle, NO_VALUE);
            if (style != NO_VALUE) {
                return createFromStyle(style);
            } else {
                return defaultFactory.strongEmphasis();
            }
        }

        @Nullable
        @Override
        public Object emphasis() {
            final int style = styles.get(R.styleable.MarkwonView_mv_EmphasisStyle, NO_VALUE);
            if (style != NO_VALUE) {
                return createFromStyle(style);
            } else {
                return defaultFactory.emphasis();
            }
        }

        @Nullable
        @Override
        public Object blockQuote(@NonNull SpannableTheme theme) {
            final int style = styles.get(R.styleable.MarkwonView_mv_BlockQuoteStyle, NO_VALUE);
            if (style != NO_VALUE) {
                return createFromStyle(style,
                        createSpanCollection(defaultFactory.blockQuote(theme)));
            }

            return defaultFactory.blockQuote(theme);
        }

        @Nullable
        @Override
        public Object code(@NonNull SpannableTheme theme, boolean multiline) {
            final int styleAttr = multiline ? R.styleable.MarkwonView_mv_MultilineCodeSpanStyle :
                    R.styleable.MarkwonView_mv_CodeSpanStyle;
            final int style = styles.get(styleAttr, NO_VALUE);
            if (style != NO_VALUE) {
                return createFromStyle(style,
                        createSpanCollection(defaultFactory.code(theme, multiline)));
            }

            return defaultFactory.code(theme, multiline);
        }

        @Nullable
        @Override
        public Object orderedListItem(@NonNull SpannableTheme theme, int startNumber) {
            final int style = styles.get(R.styleable.MarkwonView_mv_OrderedListItemStyle, NO_VALUE);
            if (style != NO_VALUE) {
                return createFromStyle(style,
                        createSpanCollection(defaultFactory.orderedListItem(theme, startNumber)));
            }

            return defaultFactory.orderedListItem(theme, startNumber);
        }

        @Nullable
        @Override
        public Object bulletListItem(@NonNull SpannableTheme theme, int level) {
            final int style = styles.get(R.styleable.MarkwonView_mv_BulletListItemStyle, NO_VALUE);
            if (style != NO_VALUE) {
                return createFromStyle(style,
                        createSpanCollection(defaultFactory.bulletListItem(theme, level)));
            }
            return defaultFactory.bulletListItem(theme, level);
        }

        @Nullable
        @Override
        public Object thematicBreak(@NonNull SpannableTheme theme) {
            return defaultFactory.thematicBreak(theme);
        }

        @Nullable
        @Override
        public Object heading(@NonNull SpannableTheme theme, int level) {
            final int style = styles.get(HEADING_STYLE_MAP.get(level), NO_VALUE);
            if (style != NO_VALUE) {
                return createFromStyle(style);
            }
            return defaultFactory.heading(theme, level);
        }

        @Nullable
        @Override
        public Object strikethrough() {
            return defaultFactory.strikethrough();
        }

        @Nullable
        @Override
        public Object taskListItem(@NonNull SpannableTheme theme, int blockIndent, boolean isDone) {
            final int style = styles.get(R.styleable.MarkwonView_mv_TaskListItemStyle, NO_VALUE);
            if (style != NO_VALUE) {
                return createFromStyle(style,
                        createSpanCollection(defaultFactory
                                .taskListItem(theme, blockIndent, isDone)));
            }

            return defaultFactory.taskListItem(theme, blockIndent, isDone);
        }

        @Nullable
        @Override
        public Object tableRow(@NonNull SpannableTheme theme,
                               @NonNull List<TableRowSpan.Cell> cells,
                               boolean isHeader,
                               boolean isOdd) {
            final int style = styles.get(R.styleable.MarkwonView_mv_TableRowStyle, NO_VALUE);
            if (style != NO_VALUE) {
                return createFromStyle(style,
                        createSpanCollection(defaultFactory
                                .tableRow(theme, cells, isHeader, isOdd)));
            }

            return defaultFactory.tableRow(theme, cells, isHeader, isOdd);
        }

        @Nullable
        @Override
        public Object image(@NonNull SpannableTheme theme,
                            @NonNull String destination,
                            @NonNull AsyncDrawable.Loader loader,
                            @NonNull ImageSizeResolver imageSizeResolver,
                            @Nullable ImageSize imageSize,
                            boolean replacementTextIsLink) {
            return defaultFactory.image(
                    theme,
                    destination,
                    loader,
                    imageSizeResolver,
                    imageSize,
                    replacementTextIsLink);
        }

        @Nullable
        @Override
        public Object link(@NonNull SpannableTheme theme,
                           @NonNull String destination,
                           @NonNull LinkSpan.Resolver resolver) {
            final int style = styles.get(R.styleable.MarkwonView_mv_LinkStyle, NO_VALUE);
            if (style != NO_VALUE) {
                return createFromStyle(style);
            }

            return defaultFactory.link(theme, destination, resolver);
        }

        @Nullable
        @Override
        public Object superScript(@NonNull SpannableTheme theme) {
            return defaultFactory.superScript(theme);
        }

        @Nullable
        @Override
        public Object subScript(@NonNull SpannableTheme theme) {
            return defaultFactory.subScript(theme);
        }

        @Nullable
        @Override
        public Object underline() {
            return defaultFactory.underline();
        }


        @NonNull
        private Object[] createFromStyle(@StyleRes int styleResource) {
            return createFromStyle(styleResource, new ArrayDeque<>());
        }

        @NonNull
        private Object[] createFromStyle(@StyleRes int styleResource,
                                         @NonNull Deque<Object> spans) {
            TypedArray a = context.obtainStyledAttributes(styleResource, TEXT_APPEARANCE_ATTR);
            final int ap = a.getResourceId(0, NO_VALUE);
            a.recycle();
            if (ap != -1) {
                spans.addFirst(new TextAppearanceSpan(context, ap));
                handleCustomFont(ap, spans);
            }

            spans.addFirst(new TextAppearanceSpan(context, styleResource));
            handleCustomFont(styleResource, spans);

            return spans.toArray();
        }

        private void handleCustomFont(@StyleRes int style, @NonNull Deque<Object> spans) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                // In SDK 27, custom fonts are handled in TextAppearanceSpan so no need to re-invent
                // the wheel.

                return;
            }

            final TypedArray a = context.obtainStyledAttributes(
                    style, R.styleable.MV_CustomFonts);

            if (a.hasValue(R.styleable.MV_CustomFonts_android_fontFamily) ||
                    a.hasValue(R.styleable.MV_CustomFonts_fontFamily)) {
                @StyleableRes int resolvedFontFamily =
                        a.hasValue(R.styleable.MV_CustomFonts_android_fontFamily)
                                ? R.styleable.MV_CustomFonts_android_fontFamily
                                : R.styleable.MV_CustomFonts_fontFamily;
                try {
                    @FontRes int fontResId = a.getResourceId(resolvedFontFamily, NO_VALUE);
                    if (fontResId != NO_VALUE) {
                        final Typeface typeface = ResourcesCompat.getFont(context, fontResId);
                        if (typeface != null) {
                            spans.addFirst(new CustomTypefaceSpan(typeface));
                        }
                    }
                } catch (UnsupportedOperationException | Resources.NotFoundException e) {
                    // Expected if it is not a font resource.
                }
            }

            a.recycle();
        }

        private static Deque<Object> createSpanCollection(@Nullable Object defaultSpan) {
            final Deque<Object> spanCollection = new ArrayDeque<>();
            if (defaultSpan instanceof Object[]) {
                for (final Object span : (Object[]) defaultSpan) {
                    spanCollection.addLast(span);
                }
            } else if (defaultSpan != null) {
                spanCollection.addLast(defaultSpan);
            }

            return spanCollection;
        }

        // taken from https://stackoverflow.com/a/17961854
        private static class CustomTypefaceSpan extends MetricAffectingSpan {
            private final Typeface typeface;

            CustomTypefaceSpan(final Typeface typeface) {
                this.typeface = typeface;
            }

            @Override
            public void updateDrawState(final TextPaint drawState) {
                apply(drawState);
            }

            @Override
            public void updateMeasureState(final TextPaint paint) {
                apply(paint);
            }

            private void apply(final Paint paint) {
                final Typeface oldTypeface = paint.getTypeface();
                final int oldStyle = oldTypeface != null ? oldTypeface.getStyle() : 0;
                final int fakeStyle = oldStyle & ~typeface.getStyle();

                if ((fakeStyle & Typeface.BOLD) != 0) {
                    paint.setFakeBoldText(true);
                }

                if ((fakeStyle & Typeface.ITALIC) != 0) {
                    paint.setTextSkewX(-0.25f);
                }

                paint.setTypeface(typeface);
            }
        }
    }
}
