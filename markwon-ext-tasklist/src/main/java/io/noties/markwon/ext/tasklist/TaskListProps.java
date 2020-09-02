package io.noties.markwon.ext.tasklist;

import io.noties.markwon.Prop;

/**
 * @since 3.0.0
 */
public abstract class TaskListProps {

    public static final Prop<Boolean> DONE = Prop.of("task-list-done");

    private TaskListProps() {
    }
}
