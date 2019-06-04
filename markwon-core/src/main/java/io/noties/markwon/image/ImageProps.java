package io.noties.markwon.image;

import io.noties.markwon.Prop;

/**
 * @since 3.0.0
 */
public abstract class ImageProps {

    public static final Prop<String> DESTINATION = Prop.of("image-destination");

    public static final Prop<Boolean> REPLACEMENT_TEXT_IS_LINK =
            Prop.of("image-replacement-text-is-link");

    public static final Prop<ImageSize> IMAGE_SIZE = Prop.of("image-size");


    private ImageProps() {
    }
}
