package io.noties.markwon.app.samples.tasklist;

import android.graphics.Color;

import io.noties.markwon.Markwon;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.ext.tasklist.TaskListPlugin;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;
import io.noties.markwon.sample.annotations.Tag;

import static io.noties.markwon.app.samples.tasklist.shared.TaskListHolder.MD;

@MarkwonSampleInfo(
  id = "20200702140536",
  title = "GFM task list custom colors",
  description = "Custom colors for task list extension",
  artifacts = MarkwonArtifact.EXT_TASKLIST,
  tags = Tag.parsing
)
public class TaskListCustomColorsSample extends MarkwonTextViewSample {
  @Override
  public void render() {
    final int checkedFillColor = Color.RED;
    final int normalOutlineColor = Color.GREEN;
    final int checkMarkColor = Color.BLUE;

    final Markwon markwon = Markwon.builder(context)
      .usePlugin(TaskListPlugin.create(checkedFillColor, normalOutlineColor, checkMarkColor))
      .build();

    markwon.setMarkdown(textView, MD);
  }
}
