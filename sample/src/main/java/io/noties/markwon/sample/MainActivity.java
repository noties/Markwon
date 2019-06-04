package io.noties.markwon.sample;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.Arrays;

import io.noties.markwon.Markwon;
import io.noties.markwon.sample.basicplugins.BasicPluginsActivity;
import io.noties.markwon.sample.core.CoreActivity;
import io.noties.markwon.sample.customextension.CustomExtensionActivity;
import io.noties.markwon.sample.latex.LatexActivity;
import io.noties.markwon.sample.recycler.RecyclerActivity;
import ru.noties.adapt.Adapt;
import ru.noties.adapt.OnClickViewProcessor;
import ru.noties.debug.AndroidLogDebugOutput;
import ru.noties.debug.Debug;

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

        final Adapt<SampleItem> adapt = Adapt.builder(SampleItem.class)
                .include(SampleItem.class, new SampleItemView(markwon), new OnClickViewProcessor<SampleItem>() {
                    @Override
                    public void onClick(@NonNull SampleItem item, @NonNull View view) {
                        showSample(item);
                    }
                })
                .build();
        adapt.setItems(Arrays.asList(SampleItem.values()));

        final RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(createSampleItemDecoration());
        recyclerView.setAdapter(adapt.recyclerViewAdapter());
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

    private void showSample(@NonNull SampleItem item) {
        startActivity(sampleItemIntent(this, item));
    }

    @VisibleForTesting
    static Intent sampleItemIntent(@NonNull Context context, @NonNull SampleItem item) {

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

            default:
                throw new IllegalStateException("No Activity is associated with sample-item: " + item);
        }

        return new Intent(context, activity);
    }
}
