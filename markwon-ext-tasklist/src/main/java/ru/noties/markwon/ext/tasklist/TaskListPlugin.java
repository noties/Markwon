package ru.noties.markwon.ext.tasklist;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.util.TypedValue;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;

import ru.noties.markwon.AbstractMarkwonPlugin;
import ru.noties.markwon.MarkwonSpansFactory;
import ru.noties.markwon.MarkwonVisitor;
import ru.noties.markwon.RenderProps;
import ru.noties.markwon.core.SimpleBlockNodeVisitor;

/**
 * @since 3.0.0
 */
public class TaskListPlugin extends AbstractMarkwonPlugin {

    /**
     * Supplied Drawable must be stateful ({@link Drawable#isStateful()} returns true). If a task
     * is marked as done, then this drawable will be updated with an {@code int[] { android.R.attr.state_checked }}
     * as the state, otherwise an empty array will be used. This library provides a ready to be
     * used Drawable: {@link TaskListDrawable}
     *
     * @see TaskListDrawable
     */
    @NonNull
    public static TaskListPlugin create(@NonNull Drawable drawable) {
        return new TaskListPlugin(drawable);
    }

    @NonNull
    public static TaskListPlugin create(@NonNull Context context) {

        // by default we will be using link color for the checkbox color
        // & window background as a checkMark color
        final int linkColor = resolve(context, android.R.attr.textColorLink);
        final int backgroundColor = resolve(context, android.R.attr.colorBackground);

        return new TaskListPlugin(new TaskListDrawable(linkColor, linkColor, backgroundColor));
    }

    @NonNull
    public static TaskListPlugin create(
            @ColorInt int checkedFillColor,
            @ColorInt int normalOutlineColor,
            @ColorInt int checkMarkColor) {
        return new TaskListPlugin(new TaskListDrawable(
                checkedFillColor,
                normalOutlineColor,
                checkMarkColor));
    }

    private final Drawable drawable;

    private TaskListPlugin(@NonNull Drawable drawable) {
        this.drawable = drawable;
    }

    @Override
    public void configureParser(@NonNull Parser.Builder builder) {
        builder.customBlockParserFactory(new TaskListBlockParser.Factory());
    }

    @Override
    public void configureSpansFactory(@NonNull MarkwonSpansFactory.Builder builder) {
        builder.setFactory(TaskListItem.class, new TaskListSpanFactory(drawable));
    }

    @Override
    public void configureVisitor(@NonNull MarkwonVisitor.Builder builder) {
        builder
                .on(TaskListBlock.class, new SimpleBlockNodeVisitor())
                .on(TaskListItem.class, new MarkwonVisitor.NodeVisitor<TaskListItem>() {
                    @Override
                    public void visit(@NonNull MarkwonVisitor visitor, @NonNull TaskListItem taskListItem) {

                        final int length = visitor.length();

                        visitor.visitChildren(taskListItem);

                        final RenderProps context = visitor.renderProps();

                        TaskListProps.BLOCK_INDENT.set(context, indent(taskListItem) + taskListItem.indent());
                        TaskListProps.DONE.set(context, taskListItem.done());

                        visitor.setSpansForNode(taskListItem, length);

                        if (visitor.hasNext(taskListItem)) {
                            visitor.ensureNewLine();
                        }
                    }
                });
    }

    private static int resolve(@NonNull Context context, @AttrRes int attr) {
        final TypedValue typedValue = new TypedValue();
        final int attrs[] = new int[]{attr};
        final TypedArray typedArray = context.obtainStyledAttributes(typedValue.data, attrs);
        try {
            return typedArray.getColor(0, 0);
        } finally {
            typedArray.recycle();
        }
    }

    private static int indent(@NonNull Node node) {
        int indent = 0;
        Node parent = node.getParent();
        if (parent != null) {
            parent = parent.getParent();
            while (parent != null) {
                indent += 1;
                parent = parent.getParent();
            }
        }
        return indent;
    }
}
