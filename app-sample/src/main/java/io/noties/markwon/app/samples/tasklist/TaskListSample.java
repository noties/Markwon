package io.noties.markwon.app.samples.tasklist;

import io.noties.markwon.Markwon;
import io.noties.markwon.app.sample.Tags;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.ext.tasklist.TaskListPlugin;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;

import static io.noties.markwon.app.samples.tasklist.shared.TaskListHolder.MD;

@MarkwonSampleInfo(
  id = "20200702140352",
  title = "GFM task list",
  description = "Github Flavored Markdown (GFM) task list extension",
  artifacts = MarkwonArtifact.EXT_TASKLIST,
  tags = Tags.plugin
)
public class TaskListSample extends MarkwonTextViewSample {
  @Override
  public void render() {
    final Markwon markwon = Markwon.builder(context)
      .usePlugin(TaskListPlugin.create(context))
      .build();

    markwon.setMarkdown(textView, MD);
  }
}
