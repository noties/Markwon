package io.noties.markwon.sample.recycler;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.commonmark.ext.gfm.tables.TableBlock;
import org.commonmark.node.FencedCodeBlock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import io.noties.debug.AndroidLogDebugOutput;
import io.noties.debug.Debug;
import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.MarkwonConfiguration;
import io.noties.markwon.MarkwonVisitor;
import io.noties.markwon.core.CorePlugin;
import io.noties.markwon.html.HtmlPlugin;
import io.noties.markwon.image.picasso.PicassoImagesPlugin;
import io.noties.markwon.recycler.MarkwonAdapter;
import io.noties.markwon.recycler.SimpleEntry;
import io.noties.markwon.recycler.table.TableEntry;
import io.noties.markwon.recycler.table.TableEntryPlugin;
import io.noties.markwon.sample.R;
import io.noties.markwon.urlprocessor.UrlProcessor;
import io.noties.markwon.urlprocessor.UrlProcessorRelativeToAbsolute;

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
        final MarkwonAdapter adapter = MarkwonAdapter.builder(R.layout.adapter_default_entry, R.id.text)
                // we can simply use bundled SimpleEntry
                .include(FencedCodeBlock.class, SimpleEntry.create(R.layout.adapter_fenced_code_block, R.id.text))
                .include(TableBlock.class, TableEntry.create(builder -> builder
                        .tableLayout(R.layout.adapter_table_block, R.id.table_layout)
                        .textLayoutIsRoot(R.layout.view_table_entry_cell)))
                .build();

        final RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        final Markwon markwon = markwon(this);
        adapter.setMarkdown(markwon, loadReadMe(this));

        // please note that we should notify updates (adapter doesn't do it implicitly)
        adapter.notifyDataSetChanged();
    }

    @NonNull
    private static Markwon markwon(@NonNull Context context) {
        return Markwon.builder(context)
                .usePlugin(CorePlugin.create())
//                .usePlugin(ImagesPlugin.create(plugin -> {
//                    plugin
//                            .addSchemeHandler(FileSchemeHandler.createWithAssets(context))
//                            .addSchemeHandler(OkHttpNetworkSchemeHandler.create())
//                            .addMediaDecoder(SvgMediaDecoder.create());
//                }))
                .usePlugin(PicassoImagesPlugin.create(context))
//                .usePlugin(GlideImagesPlugin.create(context))
                // important to use TableEntryPlugin instead of TablePlugin
                .usePlugin(TableEntryPlugin.create(context))
                .usePlugin(HtmlPlugin.create())
//                .usePlugin(SyntaxHighlightPlugin.create())
                .usePlugin(new AbstractMarkwonPlugin() {
                    @Override
                    public void configureConfiguration(@NonNull MarkwonConfiguration.Builder builder) {
                        builder.urlProcessor(new UrlProcessorInitialReadme());
                    }

                    @Override
                    public void configureVisitor(@NonNull MarkwonVisitor.Builder builder) {
                        builder.on(FencedCodeBlock.class, (visitor, fencedCodeBlock) -> {
                            // we actually won't be applying code spans here, as our custom view will
                            // draw background and apply mono typeface
                            //
                            // NB the `trim` operation on literal (as code will have a new line at the end)
                            final CharSequence code = visitor.configuration()
                                    .syntaxHighlight()
                                    .highlight(fencedCodeBlock.getInfo(), fencedCodeBlock.getLiteral().trim());
                            visitor.builder().append(code);
                        });
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
