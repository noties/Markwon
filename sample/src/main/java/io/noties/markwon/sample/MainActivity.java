package io.noties.markwon.sample;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.noties.adapt.Adapt;
import io.noties.adapt.Item;
import io.noties.debug.AndroidLogDebugOutput;
import io.noties.debug.Debug;
import io.noties.markwon.Markwon;
import io.noties.markwon.sample.basicplugins.BasicPluginsActivity;
import io.noties.markwon.sample.core.CoreActivity;
import io.noties.markwon.sample.customextension.CustomExtensionActivity;
import io.noties.markwon.sample.customextension2.CustomExtensionActivity2;
import io.noties.markwon.sample.editor.EditorActivity;
import io.noties.markwon.sample.html.HtmlActivity;
import io.noties.markwon.sample.latex.LatexActivity;
import io.noties.markwon.sample.precomputed.PrecomputedActivity;
import io.noties.markwon.sample.recycler.RecyclerActivity;
import io.noties.markwon.sample.simpleext.SimpleExtActivity;

public class MainActivity extends Activity {

    static {
        Debug.init(new AndroidLogDebugOutput(true));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // obtain an instance of Markwon
        // here we are creating as core markwon (no additional plugins are registered)
        final Markwon markwon = Markwon.create(this);

        final Adapt adapt = Adapt.create();

        final List<Item> items = new ArrayList<>();
        final SampleItem.OnClickListener onClickListener = this::showSample;
        for (Sample sample : Sample.values()) {
            items.add(new SampleItem(sample, markwon, onClickListener));
        }
        adapt.setItems(items);

        final RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(createSampleItemDecoration());
        recyclerView.setAdapter(adapt);
    }

    @NonNull
    private SampleItemDecoration createSampleItemDecoration() {
        final float density = getResources().getDisplayMetrics().density;
        return new SampleItemDecoration(
                0xffeeeeee,
                (int) (24 * density + .5F),
                (int) (1 * density + .5F),
                0xFFBDBDBD
        );
    }

    private void showSample(@NonNull Sample item) {
        startActivity(sampleItemIntent(this, item));
    }

    @VisibleForTesting
    static Intent sampleItemIntent(@NonNull Context context, @NonNull Sample item) {

        final Class<? extends Activity> activity;

        switch (item) {

            case CORE:
                activity = CoreActivity.class;
                break;

            case BASIC_PLUGINS:
                activity = BasicPluginsActivity.class;
                break;

            case LATEX:
                activity = LatexActivity.class;
                break;

            case CUSTOM_EXTENSION:
                activity = CustomExtensionActivity.class;
                break;

            case RECYCLER:
                activity = RecyclerActivity.class;
                break;

            case HTML:
                activity = HtmlActivity.class;
                break;

            case SIMPLE_EXT:
                activity = SimpleExtActivity.class;
                break;

            case CUSTOM_EXTENSION_2:
                activity = CustomExtensionActivity2.class;
                break;

            case PRECOMPUTED_TEXT:
                activity = PrecomputedActivity.class;
                break;

            case EDITOR:
                activity = EditorActivity.class;
                break;

            default:
                throw new IllegalStateException("No Activity is associated with sample-item: " + item);
        }

        return new Intent(context, activity);
    }
}
