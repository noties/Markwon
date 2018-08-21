package ru.noties.markwon;

import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.style.ForegroundColorSpan;

import ru.noties.markwon.il.AsyncDrawableLoader;
import ru.noties.markwon.il.GifMediaDecoder;
import ru.noties.markwon.il.ImageMediaDecoder;
import ru.noties.markwon.il.SvgMediaDecoder;
import ru.noties.markwon.renderer.html2.CssInlineStyleParser;
import ru.noties.markwon.renderer.html2.CssProperty;
import ru.noties.markwon.renderer.html2.MarkwonHtmlRenderer;

public class Blah {

    static {
final CssInlineStyleParser inlineStyleParser = CssInlineStyleParser.create();
for (CssProperty property: inlineStyleParser.parse("width: 100%; height: 100%;")) {
    // [0] = CssProperty({width=100%}),
    // [1] = CssProperty({height=100%})
}
    }
}
