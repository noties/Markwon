package ru.noties.markwon.spans;

import android.support.annotation.NonNull;

public interface SpanFactory {
    @NonNull
    Object createBlockQuote();

    @NonNull
    Object createBulletListItem(int level);

    @NonNull
    Object createCode(boolean multiline);

    @NonNull
    Object createEmphasis();

    @NonNull
    Object createHeading(int level);

    @NonNull
    Object createImage(@NonNull String destination, boolean link);

    @NonNull
    Object createLink(@NonNull String destination);

    @NonNull
    Object createOrderedListItem(int order);

    @NonNull
    Object createStrongEmphasis();

    @NonNull
    Object createStrikethrough();

    @NonNull
    Object createTaskList(int indent, boolean done);

    @NonNull
    Object createThematicBreak();

}
