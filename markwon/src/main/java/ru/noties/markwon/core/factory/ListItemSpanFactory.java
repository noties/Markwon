package ru.noties.markwon.core.factory;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.noties.markwon.MarkwonConfiguration;
import ru.noties.markwon.RenderProps;
import ru.noties.markwon.SpanFactory;
import ru.noties.markwon.core.CoreProps;
import ru.noties.markwon.core.spans.BulletListItemSpan;
import ru.noties.markwon.core.spans.OrderedListItemSpan;

public class ListItemSpanFactory implements SpanFactory {

    @Nullable
    @Override
    public Object getSpans(@NonNull MarkwonConfiguration configuration, @NonNull RenderProps context) {

        // type of list item
        // bullet : level
        // ordered: number
        final Object spans;

        if (CoreProps.ListItemType.BULLET == CoreProps.LIST_ITEM_TYPE.require(context)) {
            spans = new BulletListItemSpan(
                    configuration.theme(),
                    CoreProps.BULLET_LIST_ITEM_LEVEL.require(context)
            );
        } else {

            // todo| in order to provide real RTL experience there must be a way to provide this string
            final String number = String.valueOf(CoreProps.ORDERED_LIST_ITEM_NUMBER.require(context))
                    + "." + '\u00a0';

            spans = new OrderedListItemSpan(
                    configuration.theme(),
                    number
            );
        }

        return spans;
    }
}
