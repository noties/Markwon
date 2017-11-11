package ru.noties.markwon.renderer.html;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.noties.markwon.UrlProcessor;
import ru.noties.markwon.spans.AsyncDrawable;
import ru.noties.markwon.spans.AsyncDrawableSpan;
import ru.noties.markwon.spans.SpannableTheme;

class ImageProviderImpl implements SpannableHtmlParser.ImageProvider {

    private static final Pattern STYLE_WIDTH = Pattern.compile(".*width:\\s*(\\d+)(%|em|px)*.*");
    private static final Pattern STYLE_HEIGHT = Pattern.compile(".*height:\\s*(\\d+)(%|em|px)*.*");

    private final SpannableTheme theme;
    private final AsyncDrawable.Loader loader;
    private final UrlProcessor urlProcessor;

    ImageProviderImpl(
            @NonNull SpannableTheme theme,
            @NonNull AsyncDrawable.Loader loader,
            @NonNull UrlProcessor urlProcessor) {
        this.theme = theme;
        this.loader = loader;
        this.urlProcessor = urlProcessor;
    }

    @Override
    public Spanned provide(@NonNull SpannableHtmlParser.Tag tag) {

        final Spanned spanned;

        final Map<String, String> attributes = tag.attributes();
        final String src = attributes.get("src");
        final String alt = attributes.get("alt");

        if (!TextUtils.isEmpty(src)) {

            final String destination = urlProcessor.process(src);

            final String replacement;
            if (!TextUtils.isEmpty(alt)) {
                replacement = alt;
            } else {
                replacement = "\uFFFC";
            }

            final AsyncDrawable drawable = new AsyncDrawable(destination, loader, parseImageSize(attributes));
            final AsyncDrawableSpan span = new AsyncDrawableSpan(theme, drawable);

            final SpannableString string = new SpannableString(replacement);
            string.setSpan(span, 0, string.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            spanned = string;
        } else {
            spanned = null;
        }

        return spanned;
    }

    @Nullable
    private static ImageSize parseImageSize(@NonNull Map<String, String> attributes) {

        return null;

//        final String width = attributes.get("width");
//        final String height = attributes.get("height");
//
//        final ImageSize imageSize;
//
//        final String width = extractWidth(attributes);
//        final String height = extractHeight(attributes);
//
//        if (TextUtils.isEmpty(width)
//                && TextUtils.isEmpty(height)) {
//            imageSize = null;
//        } else {
//            if (isRelative(width)) {
//                // check if height is NOT relative, if it is -> ignore
//                final int h = isRelative(height)
//                        ? 0
//                        : parseInt(height);
//                imageSize = new ImageSize(, parseInt(width), h);
//            } else {
//                imageSize = new ImageSize(false, parseInt(width), parseInt(height));
//            }
//        }
//
//        return imageSize;
    }

//    @Nullable
//    private static ImageSize.Dimension parseWidth(@NonNull Map<String, String> attributes) {
//
//        // so, we can have raw value specified via direct attribute
//
//        final ImageSize.Dimension dimension;
//
//        final String width = attributes.get("width");
//        if (!TextUtils.isEmpty(width)) {
//            final Matcher matcher =
//        }
//    }

    @Nullable
    private static String extractWidth(@NonNull Map<String, String> attributes) {

        // let's check style first

        String width = attributes.get("width");

        if (width == null) {
            final String style = attributes.get("style");
            if (!TextUtils.isEmpty(style)) {
                final Matcher matcher = STYLE_WIDTH.matcher(style);
                if (matcher.matches()) {
                    width = matcher.group(1);
                }
            }
        }

        return width;
    }

    @Nullable
    private static String extractHeight(@NonNull Map<String, String> attributes) {

        String height = attributes.get("height");

        if (height == null) {
            final String style = attributes.get("style");
            if (!TextUtils.isEmpty(style)) {
                final Matcher matcher = STYLE_HEIGHT.matcher(style);
                if (matcher.matches()) {
                    height = matcher.group(1);
                }
            }
        }

        return height;
    }

    @Nullable
    private static ImageSize.Dimension extractFromStyle(@Nullable String style, @NonNull Pattern pattern) {
        final ImageSize.Dimension dimension;
        if (style == null) {
            dimension = null;
        } else {
            final Matcher matcher = pattern.matcher(style);
            if (matcher.matches()) {
                dimension = new ImageSize.Dimension(
                        parseUnit(matcher.group(2)),
                        parseInt(matcher.group(1))
                );
            } else {
                dimension = null;
            }
        }
        return dimension;
    }

    @NonNull
    private static ImageSize.Unit parseUnit(@Nullable String s) {

        final ImageSize.Unit unit;

        if (TextUtils.isEmpty(s)) {

            unit = ImageSize.Unit.PIXELS;

        } else {

            final int length = s.length();

            if (length == 1
                    && '%' == s.charAt(length - 1)) {
                unit = ImageSize.Unit.PERCENT;
            } else if (length == 2
                    && 'e' == s.charAt(length - 2)
                    && 'm' == s.charAt(length - 1)) {
                unit = ImageSize.Unit.FONT_SIZE;
            } else {
                unit = ImageSize.Unit.PIXELS;
            }
        }

        return unit;
    }

    private static boolean isRelative(@Nullable String attr) {
        return attr != null && attr.charAt(attr.length() - 1) == '%';
    }

    private static int parseInt(@Nullable String s) {
        int result = 0;
        if (!TextUtils.isEmpty(s)) {
            try {
                result = Integer.parseInt(s.replaceAll("[^\\d]", ""));
            } catch (NumberFormatException e) {
                result = 0;
            }
        }
        return result;
    }
}
