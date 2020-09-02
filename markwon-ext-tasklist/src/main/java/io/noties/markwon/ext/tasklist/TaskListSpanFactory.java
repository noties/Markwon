package io.noties.markwon.ext.tasklist;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.noties.markwon.MarkwonConfiguration;
import io.noties.markwon.RenderProps;
import io.noties.markwon.SpanFactory;

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
                TaskListProps.DONE.get(props, false)
        );
    }
}
