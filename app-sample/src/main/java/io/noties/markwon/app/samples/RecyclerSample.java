package io.noties.markwon.app.samples;

import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.commonmark.ext.gfm.tables.TableBlock;
import org.commonmark.node.FencedCodeBlock;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.MarkwonConfiguration;
import io.noties.markwon.MarkwonVisitor;
import io.noties.markwon.app.R;
import io.noties.markwon.app.readme.GithubImageDestinationProcessor;
import io.noties.markwon.app.sample.ui.MarkwonRecyclerViewSample;
import io.noties.markwon.app.utils.SampleUtilsKtKt;
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin;
import io.noties.markwon.ext.tasklist.TaskListPlugin;
import io.noties.markwon.html.HtmlPlugin;
import io.noties.markwon.image.ImagesPlugin;
import io.noties.markwon.recycler.MarkwonAdapter;
import io.noties.markwon.recycler.SimpleEntry;
import io.noties.markwon.recycler.table.TableEntry;
import io.noties.markwon.recycler.table.TableEntryPlugin;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;
import io.noties.markwon.sample.annotations.Tag;

@MarkwonSampleInfo(
  id = "20200702101750",
  title = "RecyclerView",
  description = "Usage with `RecyclerView`",
  artifacts = {MarkwonArtifact.RECYCLER, MarkwonArtifact.RECYCLER_TABLE},
  tags = Tag.recyclerView
)
public class RecyclerSample extends MarkwonRecyclerViewSample {
  @Override
  public void render() {
    final String md = SampleUtilsKtKt.loadReadMe(context);

    final Markwon markwon = Markwon.builder(context)
      .usePlugin(ImagesPlugin.create())
      .usePlugin(TableEntryPlugin.create(context))
      .usePlugin(HtmlPlugin.create())
      .usePlugin(StrikethroughPlugin.create())
      .usePlugin(TaskListPlugin.create(context))
      .usePlugin(new AbstractMarkwonPlugin() {
        @Override
        public void configureConfiguration(@NonNull MarkwonConfiguration.Builder builder) {
          builder.imageDestinationProcessor(new GithubImageDestinationProcessor());
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

    final MarkwonAdapter adapter = MarkwonAdapter.builderTextViewIsRoot(R.layout.adapter_node)
      .include(FencedCodeBlock.class, SimpleEntry.create(R.layout.adapter_node_code_block, R.id.text_view, Color.BLACK, "light"))
      .include(TableBlock.class, TableEntry.create(builder -> {
        builder
          .tableLayout(R.layout.adapter_node_table_block, R.id.table_layout)
          .textLayoutIsRoot(R.layout.view_table_entry_cell);
      }))
      .build();

    recyclerView.setLayoutManager(new LinearLayoutManager(context));
    recyclerView.setAdapter(adapter);

    adapter.setMarkdown(markwon, md);
    adapter.notifyDataSetChanged();
  }
}
