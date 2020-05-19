package io.noties.markwon.sample.html;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.commonmark.node.Image;

import java.util.Collection;
import java.util.Collections;

import io.noties.debug.Debug;
import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.MarkwonConfiguration;
import io.noties.markwon.RenderProps;
import io.noties.markwon.html.HtmlPlugin;
import io.noties.markwon.html.HtmlTag;
import io.noties.markwon.html.tag.SimpleTagHandler;
import io.noties.markwon.image.ImageProps;
import io.noties.markwon.image.ImageSize;

public class IFrameHtmlPlugin extends AbstractMarkwonPlugin {
    @Override
    public void configure(@NonNull Registry registry) {
        registry.require(HtmlPlugin.class, htmlPlugin -> {
            // TODO: empty tag replacement
            htmlPlugin.addHandler(new EmbedTagHandler());
        });
    }

    private static class EmbedTagHandler extends SimpleTagHandler {

        @Nullable
        @Override
        public Object getSpans(@NonNull MarkwonConfiguration configuration, @NonNull RenderProps renderProps, @NonNull HtmlTag tag) {
            final ImageSize imageSize = new ImageSize(new ImageSize.Dimension(640, "px"), new ImageSize.Dimension(480, "px"));
            ImageProps.IMAGE_SIZE.set(renderProps, imageSize);
            ImageProps.DESTINATION.set(renderProps, "https://hey.com/1.png");
            return configuration.spansFactory().require(Image.class)
                    .getSpans(configuration, renderProps);
        }

        @NonNull
        @Override
        public Collection<String> supportedTags() {
            return Collections.singleton("iframe");
        }
    }
}
