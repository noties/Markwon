package ru.noties.markwon.spans.heading;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static ru.noties.markwon.spans.heading.HeadingType.H1;
import static ru.noties.markwon.spans.heading.HeadingType.H2;
import static ru.noties.markwon.spans.heading.HeadingType.H3;
import static ru.noties.markwon.spans.heading.HeadingType.H4;
import static ru.noties.markwon.spans.heading.HeadingType.H5;
import static ru.noties.markwon.spans.heading.HeadingType.H6;

/**
 * Created by daniel.leal on 13.12.17.
 */
@Retention(RetentionPolicy.SOURCE)
@IntDef({H1, H2, H3, H4, H5, H6})
public @interface HeadingType {
    int H1 = 1;
    int H2 = 2;
    int H3 = 3;
    int H4 = 4;
    int H5 = 5;
    int H6 = 6;
}
