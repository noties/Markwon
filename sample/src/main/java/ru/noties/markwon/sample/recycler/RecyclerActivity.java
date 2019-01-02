package ru.noties.markwon.sample.recycler;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import org.commonmark.ext.gfm.tables.TableBlock;
import org.commonmark.node.FencedCodeBlock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import ru.noties.debug.AndroidLogDebugOutput;
import ru.noties.debug.Debug;
import ru.noties.markwon.AbstractMarkwonPlugin;
import ru.noties.markwon.Markwon;
import ru.noties.markwon.MarkwonConfiguration;
import ru.noties.markwon.core.CorePlugin;
import ru.noties.markwon.ext.tables.TablePlugin;
import ru.noties.markwon.html.HtmlPlugin;
import ru.noties.markwon.image.ImagesPlugin;
import ru.noties.markwon.image.svg.SvgPlugin;
import ru.noties.markwon.recycler.MarkwonAdapter;
import ru.noties.markwon.recycler.SimpleEntry;
import ru.noties.markwon.sample.R;
import ru.noties.markwon.urlprocessor.UrlProcessor;
import ru.noties.markwon.urlprocessor.UrlProcessorRelativeToAbsolute;

public class RecyclerActivity extends Activity {

    static {
        Debug.init(new AndroidLogDebugOutput(true));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler);

        // create MarkwonAdapter and register two blocks that will be rendered differently
        // * fenced code block (can also specify the same Entry for indended code block)
        // * table block
        final MarkwonAdapter adapter = MarkwonAdapter.builder()
                // we can simply use bundled SimpleEntry, that will lookup a TextView
                // with `@+id/text` id
                .include(FencedCodeBlock.class, new SimpleEntry(R.layout.adapter_fenced_code_block))
                // create own implementation of entry for different rendering
                .include(TableBlock.class, new TableEntry())
                // specify default entry (for all other blocks)
                .defaultEntry(new SimpleEntry(R.layout.adapter_default_entry))
                .build();

        final RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        final Markwon markwon = markwon(this);
        adapter.setMarkdown(markwon, loadReadMe(this));

        // please note that we should notify updates (adapter doesn't do it implicitly)
        adapter.notifyDataSetChanged();

        // NB, there is no currently available widget to render tables gracefully
        // TableEntryView is here for demonstration purposes only (to show that rendering
        // tables
    }

    @NonNull
    private static Markwon markwon(@NonNull Context context) {
        return Markwon.builder(context)
                .usePlugin(CorePlugin.create())
                .usePlugin(ImagesPlugin.createWithAssets(context))
                .usePlugin(SvgPlugin.create(context.getResources()))
                .usePlugin(TablePlugin.create(context))
                .usePlugin(HtmlPlugin.create())
                .usePlugin(new AbstractMarkwonPlugin() {
                    @Override
                    public void configureConfiguration(@NonNull MarkwonConfiguration.Builder builder) {
                        builder.urlProcessor(new UrlProcessorInitialReadme());
                    }
                })
                .build();
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

    private static class UrlProcessorInitialReadme implements UrlProcessor {

        private static final String GITHUB_BASE = "https://github.com/noties/Markwon/raw/master/";

        private final UrlProcessorRelativeToAbsolute processor
                = new UrlProcessorRelativeToAbsolute(GITHUB_BASE);

        @NonNull
        @Override
        public String process(@NonNull String destination) {
            String out;
            final Uri uri = Uri.parse(destination);
            if (TextUtils.isEmpty(uri.getScheme())) {
                out = processor.process(destination);
            } else {
                out = destination;
            }
            return out;
        }
    }
}
