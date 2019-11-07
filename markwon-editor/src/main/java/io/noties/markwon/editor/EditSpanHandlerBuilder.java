package io.noties.markwon.editor;

import android.text.Editable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * @since 4.2.0-SNAPSHOT
 */
public class EditSpanHandlerBuilder {

    public interface EditSpanHandlerTyped<T> {
        void handle(
                @NonNull MarkwonEditor.EditSpanStore store,
                @NonNull Editable editable,
                @NonNull String input,
                @NonNull T span,
                int spanStart,
                int spanTextLength);
    }

    @NonNull
    public static EditSpanHandlerBuilder create() {
        return new EditSpanHandlerBuilder();
    }

    private final Map<Class<?>, EditSpanHandlerTyped> map = new HashMap<>(3);

    @NonNull
    public <T> EditSpanHandlerBuilder handleMarkdownSpan(
            @NonNull Class<T> type,
            @NonNull EditSpanHandlerTyped<T> handler) {
        map.put(type, handler);
        return this;
    }

    @Nullable
    public MarkwonEditor.EditSpanHandler build() {
        if (map.size() == 0) {
            return null;
        }
        return new EditSpanHandlerImpl(map);
    }

    private static class EditSpanHandlerImpl implements MarkwonEditor.EditSpanHandler {

        private final Map<Class<?>, EditSpanHandlerTyped> map;

        EditSpanHandlerImpl(@NonNull Map<Class<?>, EditSpanHandlerTyped> map) {
            this.map = map;
        }

        @Override
        public void handle(
                @NonNull MarkwonEditor.EditSpanStore store,
                @NonNull Editable editable,
                @NonNull String input,
                @NonNull Object span,
                int spanStart,
                int spanTextLength) {
            final EditSpanHandlerTyped handler = map.get(span.getClass());
            if (handler != null) {
                //noinspection unchecked
                handler.handle(store, editable, input, span, spanStart, spanTextLength);
            }
        }
    }
}
