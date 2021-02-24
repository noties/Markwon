package io.noties.markwon.app.samples.tasklist;

import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import java.util.Objects;

import io.noties.markwon.Markwon;
import io.noties.markwon.app.R;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.ext.tasklist.TaskListPlugin;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;
import io.noties.markwon.sample.annotations.Tag;

import static io.noties.markwon.app.samples.tasklist.shared.TaskListHolder.MD;

@MarkwonSampleInfo(
  id = "20200702140749",
  title = "GFM task list custom drawable",
  artifacts = MarkwonArtifact.EXT_TASKLIST,
  tags = Tag.plugin
)
public class TaskListCustomDrawableSample extends MarkwonTextViewSample {
  @Override
  public void render() {
    final Drawable drawable = Objects.requireNonNull(
      ContextCompat.getDrawable(context, R.drawable.custom_task_list));

    final Markwon markwon = Markwon.builder(context)
      .usePlugin(TaskListPlugin.create(drawable))
      .build();

    markwon.setMarkdown(textView, MD);
  }
}
