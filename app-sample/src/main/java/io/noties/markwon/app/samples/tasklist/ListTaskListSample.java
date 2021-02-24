package io.noties.markwon.app.samples.tasklist;

import io.noties.markwon.Markwon;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.ext.tasklist.TaskListPlugin;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;
import io.noties.markwon.sample.annotations.Tag;

@MarkwonSampleInfo(
  id = "20200902174132",
  title = "Task list items with other lists",
  description = "Mix of task list items with other lists (bullet and ordered)",
  artifacts = MarkwonArtifact.EXT_TASKLIST,
  tags = Tag.lists
)
public class ListTaskListSample extends MarkwonTextViewSample {
  @Override
  public void render() {
    final String md = "" +
      "- [ ] Task **1**\n" +
      "- [ ] _Task_ 2\n" +
      "- [ ] Task 3\n" +
      "  - Sub Task 3.1\n" +
      "  - Sub Task 3.2\n" +
      "    * [X] Sub Task 4.1\n" +
      "    * [X] Sub Task 4.2\n" +
      "- [ ] Task 4\n" +
      "  - [ ] Sub Task 3.1\n" +
      "  - [ ] Sub Task 3.2";

    final Markwon markwon = Markwon.builder(context)
      .usePlugin(TaskListPlugin.create(context))
      .build();

    markwon.setMarkdown(textView, md);
  }
}
