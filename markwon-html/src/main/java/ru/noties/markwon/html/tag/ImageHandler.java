package ru.noties.markwon.html.tag;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.Map;

import ru.noties.markwon.MarkwonConfiguration;
import ru.noties.markwon.html.HtmlTag;
import ru.noties.markwon.html.CssInlineStyleParser;
import ru.noties.markwon.image.ImageSize;

public class ImageHandler extends SimpleTagHandler {

    interface ImageSizeParser {
        @Nullable
        ImageSize parse(@NonNull Map<String, String> attributes);
    }

    @NonNull
    public static ImageHandler create() {
        return new ImageHandler(new ImageSizeParserImpl(CssInlineStyleParser.create()));
    }

    private final ImageSizeParser imageSizeParser;

    ImageHandler(@NonNull ImageSizeParser imageSizeParser) {
        this.imageSizeParser = imageSizeParser;
    }

    @Nullable
    @Override
    public Object getSpans(@NonNull MarkwonConfiguration configuration, @NonNull HtmlTag tag) {

        final Map<String, String> attributes = tag.attributes();
        final String src = attributes.get("src");
        if (TextUtils.isEmpty(src)) {
            return null;
        }

        final String destination = configuration.urlProcessor().process(src);

        // todo: replacement text is link... as we are not at block level
        // and cannot inspect the parent of this node... (img and a are both inlines)
        //
        // but we can look and see if we are inside a LinkSpan (will have to extend TagHandler
        // to obtain an instance SpannableBuilder for inspection)

        return null;

//        return configuration.factory().image(
//                configuration.theme(),
//                destination,
//                configuration.asyncDrawableLoader(),
//                configuration.imageSizeResolver(),
//                imageSizeParser.parse(tag.attributes()),
//                false
//        );
    }
}
