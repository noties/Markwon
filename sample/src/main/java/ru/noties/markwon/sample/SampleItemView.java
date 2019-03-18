package ru.noties.markwon.sample;

import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.EnumMap;

import ru.noties.adapt.Holder;
import ru.noties.adapt.ItemView;
import ru.noties.markwon.Markwon;
import ru.noties.markwon.utils.NoCopySpannableFactory;

class SampleItemView extends ItemView<SampleItem, SampleItemView.SampleHolder> {

    private final Markwon markwon;

    // instance specific factory
    private final Spannable.Factory factory;

    // instance specific cache
    private final EnumMap<SampleItem, Spanned> cache;

    SampleItemView(@NonNull Markwon markwon) {
        this.markwon = markwon;
        this.factory = NoCopySpannableFactory.getInstance();
        this.cache = new EnumMap<>(SampleItem.class);
    }

    @NonNull
    @Override
    public SampleHolder createHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {

        final SampleHolder holder = new SampleHolder(inflater.inflate(
                R.layout.adapt_sample_item,
                parent,
                false));

        // set Spannable.Factory so when TextView will receive a new content
        // it won't create new Spannable and copy all the spans but instead
        // re-use existing Spannable thus improving performance
        holder.textView.setSpannableFactory(factory);

        return holder;
    }

    @Override
    public void bindHolder(@NonNull SampleHolder holder, @NonNull SampleItem item) {

        // retrieve an item from cache or create new one
        // simple lazy loading pattern (cache on first call then re-use)
        Spanned spanned = cache.get(item);
        if (spanned == null) {
            spanned = markwon.toMarkdown(context(holder).getString(item.textResId()));
            cache.put(item, spanned);
        }

        holder.textView.setText(spanned);
    }

    static class SampleHolder extends Holder {

        final TextView textView;

        SampleHolder(@NonNull View view) {
            super(view);

            this.textView = requireView(R.id.text);
        }
    }
}
