package ru.noties.markwon.tasklist;

import org.commonmark.node.CustomNode;

public class TaskListItem extends CustomNode {

    private boolean done;

    public boolean done() {
        return done;
    }

    public TaskListItem done(boolean done) {
        this.done = done;
        return this;
    }
}
