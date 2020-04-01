package io.noties.markwon.sample.basicplugins;

import android.text.TextUtils;
import android.util.SparseIntArray;

import androidx.annotation.NonNull;

import org.commonmark.node.BulletList;
import org.commonmark.node.ListItem;
import org.commonmark.node.Node;
import org.commonmark.node.OrderedList;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.MarkwonSpansFactory;
import io.noties.markwon.MarkwonVisitor;
import io.noties.markwon.Prop;
import io.noties.markwon.core.CoreProps;
import io.noties.markwon.core.spans.BulletListItemSpan;
import io.noties.markwon.core.spans.OrderedListItemSpan;

public class BulletListIsOrderedWithLettersWhenNestedPlugin extends AbstractMarkwonPlugin {

    private static final Prop<String> BULLET_LETTER = Prop.of("my-bullet-letter");

    // or introduce some kind of synchronization if planning to use from multiple threads,
    //  for example via ThreadLocal
    private final SparseIntArray bulletCounter = new SparseIntArray();

    @Override
    public void afterRender(@NonNull Node node, @NonNull MarkwonVisitor visitor) {
        // clear counter after render
        bulletCounter.clear();
    }

    @Override
    public void configureVisitor(@NonNull MarkwonVisitor.Builder builder) {
        // NB that both ordered and bullet lists are represented
        //  by ListItem (must inspect parent to detect the type)
        builder.on(ListItem.class, (visitor, listItem) -> {
            // mimic original behaviour (copy-pasta from CorePlugin)

            final int length = visitor.length();

            visitor.visitChildren(listItem);

            final Node parent = listItem.getParent();
            if (parent instanceof OrderedList) {

                final int start = ((OrderedList) parent).getStartNumber();

                CoreProps.LIST_ITEM_TYPE.set(visitor.renderProps(), CoreProps.ListItemType.ORDERED);
                CoreProps.ORDERED_LIST_ITEM_NUMBER.set(visitor.renderProps(), start);

                // after we have visited the children increment start number
                final OrderedList orderedList = (OrderedList) parent;
                orderedList.setStartNumber(orderedList.getStartNumber() + 1);

            } else {
                CoreProps.LIST_ITEM_TYPE.set(visitor.renderProps(), CoreProps.ListItemType.BULLET);

                if (isBulletOrdered(parent)) {
                    // obtain current count value
                    final int count = currentBulletCountIn(parent);
                    BULLET_LETTER.set(visitor.renderProps(), createBulletLetter(count));
                    // update current count value
                    setCurrentBulletCountIn(parent, count + 1);
                } else {
                    CoreProps.BULLET_LIST_ITEM_LEVEL.set(visitor.renderProps(), listLevel(listItem));
                    // clear letter info when regular bullet list is used
                    BULLET_LETTER.clear(visitor.renderProps());
                }
            }

            visitor.setSpansForNodeOptional(listItem, length);

            if (visitor.hasNext(listItem)) {
                visitor.ensureNewLine();
            }
        });
    }

    @Override
    public void configureSpansFactory(@NonNull MarkwonSpansFactory.Builder builder) {
        builder.setFactory(ListItem.class, (configuration, props) -> {
            final Object spans;

            if (CoreProps.ListItemType.BULLET == CoreProps.LIST_ITEM_TYPE.require(props)) {
                final String letter = BULLET_LETTER.get(props);
                if (!TextUtils.isEmpty(letter)) {
                    // NB, we are using OrderedListItemSpan here!
                    spans = new OrderedListItemSpan(
                            configuration.theme(),
                            letter
                    );
                } else {
                    spans = new BulletListItemSpan(
                            configuration.theme(),
                            CoreProps.BULLET_LIST_ITEM_LEVEL.require(props)
                    );
                }
            } else {

                final String number = String.valueOf(CoreProps.ORDERED_LIST_ITEM_NUMBER.require(props))
                        + "." + '\u00a0';

                spans = new OrderedListItemSpan(
                        configuration.theme(),
                        number
                );
            }

            return spans;
        });
    }

    private int currentBulletCountIn(@NonNull Node parent) {
        return bulletCounter.get(parent.hashCode(), 0);
    }

    private void setCurrentBulletCountIn(@NonNull Node parent, int count) {
        bulletCounter.put(parent.hashCode(), count);
    }

    @NonNull
    private static String createBulletLetter(int count) {
        // or lower `a`
        // `'u00a0` is non-breakable space char
        return ((char) ('A' + count)) + ".\u00a0";
    }

    private static int listLevel(@NonNull Node node) {
        int level = 0;
        Node parent = node.getParent();
        while (parent != null) {
            if (parent instanceof ListItem) {
                level += 1;
            }
            parent = parent.getParent();
        }
        return level;
    }

    private static boolean isBulletOrdered(@NonNull Node node) {
        node = node.getParent();
        while (node != null) {
            if (node instanceof OrderedList) {
                return true;
            }
            if (node instanceof BulletList) {
                return false;
            }
            node = node.getParent();
        }
        return false;
    }
}
