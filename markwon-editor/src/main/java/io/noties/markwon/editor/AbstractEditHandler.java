package io.noties.markwon.editor;

import androidx.annotation.NonNull;

import io.noties.markwon.Markwon;

/**
 * @see EditHandler
 * @see io.noties.markwon.editor.handler.EmphasisEditHandler
 * @see io.noties.markwon.editor.handler.StrongEmphasisEditHandler
 * @since 4.2.0
 */
public abstract class AbstractEditHandler<T> implements EditHandler<T> {
    @Override
    public void init(@NonNull Markwon markwon) {

    }
}
