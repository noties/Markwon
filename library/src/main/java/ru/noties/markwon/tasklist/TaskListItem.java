package ru.noties.markwon.tasklist;

import org.commonmark.node.CustomNode;

/**
 * @since 1.0.1
 */
@SuppressWarnings("WeakerAccess")
public class TaskListItem extends CustomNode {

    private boolean mDone;
    private int mIndent;

    public boolean done() {
        return mDone;
    }

    public TaskListItem done(boolean done) {
        mDone = done;
        return this;
    }

    public int indent() {
        return mIndent;
    }

    public TaskListItem indent(int indent) {
        mIndent = indent;
        return this;
    }
}
