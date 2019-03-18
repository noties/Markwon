package ru.noties.markwon.ext.tasklist;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.noties.markwon.MarkwonConfiguration;
import ru.noties.markwon.RenderProps;
import ru.noties.markwon.SpanFactory;

public class TaskListSpanFactory implements SpanFactory {

    private final Drawable drawable;

    public TaskListSpanFactory(@NonNull Drawable drawable) {
        this.drawable = drawable;
    }

    @Nullable
    @Override
    public Object getSpans(@NonNull MarkwonConfiguration configuration, @NonNull RenderProps props) {
        return new TaskListSpan(
                configuration.theme(),
                drawable,
                TaskListProps.BLOCK_INDENT.get(props, 0),
                TaskListProps.DONE.get(props, false)
        );
    }
}
