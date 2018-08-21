package ru.noties.markwon;

import android.content.res.Resources;

import ru.noties.markwon.il.AsyncDrawableLoader;
import ru.noties.markwon.il.GifMediaDecoder;
import ru.noties.markwon.il.ImageMediaDecoder;
import ru.noties.markwon.il.SvgMediaDecoder;

public class Blah {

    static {
AsyncDrawableLoader.builder()
        .mediaDecoders(
SvgMediaDecoder.create(Resources),
GifMediaDecoder.create(boolean),
ImageMediaDecoder.create(Resources)
        )
.build();
    }
}
