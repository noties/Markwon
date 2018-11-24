package ru.noties.markwon.tasklist;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;

import org.commonmark.parser.Parser;

import ru.noties.markwon.AbstractMarkwonPlugin;
import ru.noties.markwon.MarkwonVisitor;

public class TaskListPlugin extends AbstractMarkwonPlugin {

    /**
     * @see TaskListDrawable
     */
    @NonNull
    public static TaskListPlugin create(@NonNull Drawable drawable) {
        return new TaskListPlugin(drawable);
    }

    @NonNull
    public static TaskListPlugin create(@NonNull Context context) {
        // resolve link color and background color
        return null;
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
    public void configureVisitor(@NonNull MarkwonVisitor.Builder builder) {
        builder
                .on(TaskListBlock.class, new MarkwonVisitor.NodeVisitor<TaskListBlock>() {
                    @Override
                    public void visit(@NonNull MarkwonVisitor visitor, @NonNull TaskListBlock taskListBlock) {

                        visitor.ensureNewLine();

                        visitor.incrementBlockQuoteIndent();
                        visitor.visitChildren(taskListBlock);
                        visitor.decrementBlockQuoteIndent();

                        if (visitor.hasNext(taskListBlock)) {
                            visitor.ensureNewLine();
                            visitor.forceNewLine();
                        }
                    }
                })
                .on(TaskListItem.class, new MarkwonVisitor.NodeVisitor<TaskListItem>() {
                    @Override
                    public void visit(@NonNull MarkwonVisitor visitor, @NonNull TaskListItem taskListItem) {

                        final int length = visitor.length();

                        final int indent = visitor.blockQuoteIndent();
                        visitor.blockQuoteIntent(indent + taskListItem.indent());
                        visitor.visitChildren(taskListItem);
                        visitor.setSpans(length, new TaskListSpan(
                                visitor.theme(),
                                drawable,
                                visitor.blockQuoteIndent(),
                                taskListItem.done()));

                        if (visitor.hasNext(taskListItem)) {
                            visitor.ensureNewLine();
                        }

                        visitor.blockQuoteIntent(indent);
                    }
                });
    }
}
