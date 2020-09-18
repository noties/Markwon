package io.noties.markwon.ext.tasklist;

import android.text.TextUtils;

import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.ListItem;
import org.commonmark.node.Node;
import org.commonmark.node.Paragraph;
import org.commonmark.node.Text;
import org.commonmark.parser.PostProcessor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.noties.markwon.utils.ParserUtils;

// @since 4.6.0
// Hint taken from commonmark-ext-task-list-items artifact
class TaskListPostProcessor implements PostProcessor {

    @Override
    public Node process(Node node) {
        final TaskListVisitor visitor = new TaskListVisitor();
        node.accept(visitor);
        return node;
    }

    private static class TaskListVisitor extends AbstractVisitor {

        private static final Pattern REGEX_TASK_LIST_ITEM = Pattern.compile("^\\[([xX\\s])]\\s+(.*)");

        @Override
        public void visit(ListItem listItem) {
            // Takes first child and checks if it is Text (we are looking for exact `[xX\s]` without any formatting)
            final Node child = listItem.getFirstChild();
            // check if it is paragraph (can contain text)
            if (child instanceof Paragraph) {
                final Node node = child.getFirstChild();
                if (node instanceof Text) {

                    final Text textNode = (Text) node;
                    final Matcher matcher = REGEX_TASK_LIST_ITEM.matcher(textNode.getLiteral());

                    if (matcher.matches()) {
                        final String checked = matcher.group(1);
                        final boolean isChecked = "x".equals(checked) || "X".equals(checked);

                        final TaskListItem taskListItem = new TaskListItem(isChecked);

                        final Paragraph paragraph = new Paragraph();

                        // insert before list item (directly before inside parent)
                        listItem.insertBefore(taskListItem);

                        // append the rest of matched text (can be empty)
                        final String restMatchedText = matcher.group(2);
                        if (!TextUtils.isEmpty(restMatchedText)) {
                            paragraph.appendChild(new Text(restMatchedText));
                        }

                        // move all the rest children (from the first paragraph)
                        ParserUtils.moveChildren(paragraph, node);

                        // append our created paragraph
                        taskListItem.appendChild(paragraph);

                        // move all the rest children from the listItem (further nested lists, etc)
                        ParserUtils.moveChildren(taskListItem, child);

                        // remove list item from node
                        listItem.unlink();

                        // visit taskListItem children
                        visitChildren(taskListItem);
                        return;
                    }
                }
            }
            visitChildren(listItem);
        }
    }
}
