package io.noties.markwon.app.samples;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.MarkwonConfiguration;
import io.noties.markwon.PrecomputedFutureTextSetterCompat;
import io.noties.markwon.app.R;
import io.noties.markwon.app.readme.GithubImageDestinationProcessor;
import io.noties.markwon.app.sample.Tags;
import io.noties.markwon.app.sample.ui.MarkwonRecyclerViewSample;
import io.noties.markwon.app.utils.SampleUtilsKtKt;
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin;
import io.noties.markwon.ext.tables.TablePlugin;
import io.noties.markwon.ext.tasklist.TaskListPlugin;
import io.noties.markwon.image.ImagesPlugin;
import io.noties.markwon.recycler.MarkwonAdapter;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;

@MarkwonSampleInfo(
  id = "20200702092446",
  title = "PrecomputedFutureTextSetterCompat",
  description = "Usage of `PrecomputedFutureTextSetterCompat` " +
    "inside a `RecyclerView` with appcompat",
  artifacts = {MarkwonArtifact.RECYCLER},
  tags = {Tags.recyclerView, Tags.precomputedText}
)
public class PrecomputedFutureSample extends MarkwonRecyclerViewSample {
  @Override
  public void render() {
    if (!hasAppCompat()) {
      /*
        PLEASE COMPILE WITH `APPCOMPAT` dependency
       */
      return;
    }

    final String md = SampleUtilsKtKt.loadReadMe(context);

    final Markwon markwon = Markwon.builder(context)
      .textSetter(PrecomputedFutureTextSetterCompat.create())
      .usePlugin(ImagesPlugin.create())
      .usePlugin(TablePlugin.create(context))
      .usePlugin(TaskListPlugin.create(context))
      .usePlugin(StrikethroughPlugin.create())
      .usePlugin(new AbstractMarkwonPlugin() {
        @Override
        public void configureConfiguration(@NonNull MarkwonConfiguration.Builder builder) {
          builder.imageDestinationProcessor(new GithubImageDestinationProcessor());
        }
      })
      .build();

    final MarkwonAdapter adapter = MarkwonAdapter
      .createTextViewIsRoot(R.layout.adapter_appcompat_default_entry);

    recyclerView.setLayoutManager(new LinearLayoutManager(context));
    recyclerView.setAdapter(adapter);

    adapter.setMarkdown(markwon, md);
    adapter.notifyDataSetChanged();
  }

  private static boolean hasAppCompat() {
    try {
      Class.forName("androidx.appcompat.widget.AppCompatTextView");
      return true;
    } catch (Throwable t) {
      return false;
    }
  }
}
