package io.noties.markwon.html.tag;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import java.util.Map;

import io.noties.markwon.html.CssInlineStyleParser;
import io.noties.markwon.html.CssProperty;
import io.noties.markwon.image.ImageSize;

class ImageSizeParserImpl implements ImageHandler.ImageSizeParser {

    private final CssInlineStyleParser inlineStyleParser;

    ImageSizeParserImpl(@NonNull CssInlineStyleParser inlineStyleParser) {
        this.inlineStyleParser = inlineStyleParser;
    }

    @Override
    public ImageSize parse(@NonNull Map<String, String> attributes) {

        // strictly speaking percents when specified directly on an attribute
        // are not part of the HTML spec (I couldn't find any reference)

        ImageSize.Dimension width = null;
        ImageSize.Dimension height = null;

        // okay, let's first check styles
        final String style = attributes.get("style");

        if (!TextUtils.isEmpty(style)) {

            String key;

            for (CssProperty cssProperty : inlineStyleParser.parse(style)) {

                key = cssProperty.key();

                if ("width".equals(key)) {
                    width = dimension(cssProperty.value());
                } else if ("height".equals(key)) {
                    height = dimension(cssProperty.value());
                }

                if (width != null
                        && height != null) {
                    break;
                }
            }
        }

        if (width != null
                && height != null) {
            return new ImageSize(width, height);
        }

        // check tag attributes
        if (width == null) {
            width = dimension(attributes.get("width"));
        }

        if (height == null) {
            height = dimension(attributes.get("height"));
        }

        if (width == null
                && height == null) {
            return null;
        }

        return new ImageSize(width, height);
    }

    @Nullable
    @VisibleForTesting
    ImageSize.Dimension dimension(@Nullable String value) {

        if (TextUtils.isEmpty(value)) {
            return null;
        }

        final int length = value.length();

        for (int i = length - 1; i > -1; i--) {

            if (Character.isDigit(value.charAt(i))) {

                try {
                    final float val = Float.parseFloat(value.substring(0, i + 1));
                    final String unit;
                    if (i == length - 1) {
                        // no unit info
                        unit = null;
                    } else {
                        unit = value.substring(i + 1, length);
                    }
                    return new ImageSize.Dimension(val, unit);
                } catch (NumberFormatException e) {
                    // value cannot not be represented as a float
                    return null;
                }
            }
        }

        return null;
    }
}
