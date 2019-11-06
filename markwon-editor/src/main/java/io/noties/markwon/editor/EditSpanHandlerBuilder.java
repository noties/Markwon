package io.noties.markwon.editor;

import android.text.Editable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

class EditSpanHandlerBuilder {

    private final Map<Class<?>, MarkwonEditor.EditSpanHandler> map = new HashMap<>(3);

    <T> void include(@NonNull Class<T> type, @NonNull MarkwonEditor.EditSpanHandler<T> handler) {
        map.put(type, handler);
    }

    @Nullable
    MarkwonEditor.EditSpanHandler build() {
        if (map.size() == 0) {
            return null;
        }
        return new EditSpanHandlerImpl(map);
    }

    private static class EditSpanHandlerImpl implements MarkwonEditor.EditSpanHandler {

        private final Map<Class<?>, MarkwonEditor.EditSpanHandler> map;

        EditSpanHandlerImpl(@NonNull Map<Class<?>, MarkwonEditor.EditSpanHandler> map) {
            this.map = map;
        }

        @Override
        public void handle(@NonNull MarkwonEditor.SpanStore store, @NonNull Editable editable, @NonNull String input, @NonNull Object span, int spanStart, int spanTextLength) {
            final MarkwonEditor.EditSpanHandler handler = map.get(span.getClass());
            if (handler != null) {
                //noinspection unchecked
                handler.handle(store, editable, input, span, spanStart, spanTextLength);
            }
        }
    }
}
