package io.noties.markwon.sample.precomputed;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import io.noties.markwon.Markwon;
import io.noties.markwon.PrecomputedFutureTextSetterCompat;
import io.noties.markwon.recycler.MarkwonAdapter;
import io.noties.markwon.sample.R;

public class PrecomputedFutureActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler);

        final Markwon markwon = Markwon.builder(this)
                .textSetter(PrecomputedFutureTextSetterCompat.create())
                .build();

        // create MarkwonAdapter and register two blocks that will be rendered differently
        final MarkwonAdapter adapter = MarkwonAdapter.builder(R.layout.adapter_appcompat_default_entry, R.id.text)
                .build();

        final RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        adapter.setMarkdown(markwon, loadReadMe(this));

        // please note that we should notify updates (adapter doesn't do it implicitly)
        adapter.notifyDataSetChanged();
    }

    @NonNull
    private static String loadReadMe(@NonNull Context context) {
        InputStream stream = null;
        try {
            stream = context.getAssets().open("README.md");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return readStream(stream);
    }

    @NonNull
    private static String readStream(@Nullable InputStream inputStream) {

        String out = null;

        if (inputStream != null) {
            BufferedReader reader = null;
            //noinspection TryFinallyCanBeTryWithResources
            try {
                reader = new BufferedReader(new InputStreamReader(inputStream));
                final StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line)
                            .append('\n');
                }
                out = builder.toString();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        // no op
                    }
                }
            }
        }

        if (out == null) {
            throw new RuntimeException("Cannot read stream");
        }

        return out;
    }
}
