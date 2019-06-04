package io.noties.markwon.sample;

import android.support.annotation.NonNull;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.noties.adapt.Item;
import io.noties.markwon.Markwon;
import io.noties.markwon.utils.NoCopySpannableFactory;

class SampleItem extends Item<SampleItem.SampleHolder> {

    interface OnClickListener {
        void onClick(@NonNull Sample sample);
    }

    private final Sample sample;

    private final Markwon markwon;

    private final OnClickListener onClickListener;

    // instance specific cache
    private Spanned cache;

    SampleItem(@NonNull Sample sample, @NonNull Markwon markwon, @NonNull OnClickListener onClickListener) {
        super(sample.ordinal());
        this.sample = sample;
        this.markwon = markwon;
        this.onClickListener = onClickListener;
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
        holder.textView.setSpannableFactory(NoCopySpannableFactory.getInstance());

        return holder;
    }

    @Override
    public void render(@NonNull SampleHolder holder) {

        // retrieve an item from cache or create new one
        // simple lazy loading pattern (cache on first call then re-use)
        Spanned spanned = this.cache;
        if (spanned == null) {
            spanned = cache = markwon.toMarkdown(
                    holder.textView.getResources().getString(sample.textResId()));
        }

        holder.textView.setText(spanned);

        holder.itemView.setOnClickListener(v -> onClickListener.onClick(sample));
    }

    static class SampleHolder extends Item.Holder {

        final TextView textView;

        SampleHolder(@NonNull View view) {
            super(view);

            this.textView = requireView(R.id.text);
        }
    }
}
