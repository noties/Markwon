package ru.noties.markwon.renderer.html2.tag;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.Map;

import ru.noties.markwon.SpannableConfiguration;
import ru.noties.markwon.html.api.HtmlTag;
import ru.noties.markwon.renderer.ImageSize;

public class ImageHandler extends SimpleTagHandler {

    @Nullable
    @Override
    public Object getSpans(@NonNull SpannableConfiguration configuration, @NonNull HtmlTag tag) {

        final Map<String, String> attributes = tag.attributes();
        final String src = attributes.get("src");
        if (TextUtils.isEmpty(src)) {
            return null;
        }

        final String destination = configuration.urlProcessor().process(src);

        // todo: replacement text is link... as we are not at block level
        // and cannot inspect the parent of this node... (img and a are both inlines)

        return configuration.factory().image(
                configuration.theme(),
                destination,
                configuration.asyncDrawableLoader(),
                configuration.imageSizeResolver(),
                parseImageSize(attributes),
                false
        );
    }

    @Nullable
    private static ImageSize parseImageSize(@NonNull Map<String, String> attributes) {
        // strictly speaking percents when specified directly on an attribute
        // are not part of the HTML spec (I couldn't find any reference)
        return null;
    }
}
