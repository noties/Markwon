package io.noties.markwon.html.tag;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.commonmark.node.Image;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import io.noties.markwon.MarkwonConfiguration;
import io.noties.markwon.RenderProps;
import io.noties.markwon.SpanFactory;
import io.noties.markwon.html.CssInlineStyleParser;
import io.noties.markwon.html.HtmlTag;
import io.noties.markwon.image.ImageProps;
import io.noties.markwon.image.ImageSize;

public class ImageHandler extends SimpleTagHandler {

    @NonNull
    @Override
    public Collection<String> supportedTags() {
        return Collections.singleton("img");
    }

    interface ImageSizeParser {
        @Nullable
        ImageSize parse(@NonNull Map<String, String> attributes);
    }

    @NonNull
    public static ImageHandler create() {
        return new ImageHandler(new ImageSizeParserImpl(CssInlineStyleParser.create()));
    }

    private final ImageSizeParser imageSizeParser;

    @SuppressWarnings("WeakerAccess")
    ImageHandler(@NonNull ImageSizeParser imageSizeParser) {
        this.imageSizeParser = imageSizeParser;
    }

    @Nullable
    @Override
    public Object getSpans(
            @NonNull MarkwonConfiguration configuration,
            @NonNull RenderProps renderProps,
            @NonNull HtmlTag tag) {

        final Map<String, String> attributes = tag.attributes();
        final String src = attributes.get("src");
        if (TextUtils.isEmpty(src)) {
            return null;
        }

        final SpanFactory spanFactory = configuration.spansFactory().get(Image.class);
        if (spanFactory == null) {
            return null;
        }

        final String destination = configuration.imageDestinationProcessor().process(src);
        final ImageSize imageSize = imageSizeParser.parse(tag.attributes());

        // todo: replacement text is link... as we are not at block level
        // and cannot inspect the parent of this node... (img and a are both inlines)
        //
        // but we can look and see if we are inside a LinkSpan (will have to extend TagHandler
        // to obtain an instance SpannableBuilder for inspection)

        ImageProps.DESTINATION.set(renderProps, destination);
        ImageProps.IMAGE_SIZE.set(renderProps, imageSize);
        ImageProps.REPLACEMENT_TEXT_IS_LINK.set(renderProps, false);

        return spanFactory.getSpans(configuration, renderProps);
    }
}
