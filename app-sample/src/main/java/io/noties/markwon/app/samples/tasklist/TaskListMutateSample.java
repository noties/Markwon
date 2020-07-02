package io.noties.markwon.app.samples.tasklist;

import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import io.noties.debug.Debug;
import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.MarkwonSpansFactory;
import io.noties.markwon.SpanFactory;
import io.noties.markwon.app.sample.Tags;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.ext.tasklist.TaskListItem;
import io.noties.markwon.ext.tasklist.TaskListPlugin;
import io.noties.markwon.ext.tasklist.TaskListSpan;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;

import static io.noties.markwon.app.samples.tasklist.shared.TaskListHolder.MD;

@MarkwonSampleInfo(
  id = "202007184140901",
  title = "GFM task list mutate",
  artifacts = MarkwonArtifact.EXT_TASKLIST,
  tags = Tags.plugin
)
public class TaskListMutateSample extends MarkwonTextViewSample {
  @Override
  public void render() {
    final Markwon markwon = Markwon.builder(context)
      .usePlugin(TaskListPlugin.create(context))
      .usePlugin(new AbstractMarkwonPlugin() {
        @Override
        public void configureSpansFactory(@NonNull MarkwonSpansFactory.Builder builder) {
          // obtain origin task-list-factory
          final SpanFactory origin = builder.getFactory(TaskListItem.class);
          if (origin == null) {
            return;
          }

          builder.setFactory(TaskListItem.class, (configuration, props) -> {
            // maybe it's better to validate the actual type here also
            // and not force cast to task-list-span
            final TaskListSpan span = (TaskListSpan) origin.getSpans(configuration, props);
            if (span == null) {
              return null;
            }

            // NB, toggle click will intercept possible links inside task-list-item
            return new Object[]{
              span,
              new TaskListToggleSpan(span)
            };
          });
        }
      })
      .build();

    markwon.setMarkdown(textView, MD);
  }
}

class TaskListToggleSpan extends ClickableSpan {

  private final TaskListSpan span;

  TaskListToggleSpan(@NonNull TaskListSpan span) {
    this.span = span;
  }

  @Override
  public void onClick(@NonNull View widget) {
    // toggle span (this is a mere visual change)
    span.setDone(!span.isDone());
    // request visual update
    widget.invalidate();

    // it must be a TextView
    final TextView textView = (TextView) widget;
    // it must be spanned
    final Spanned spanned = (Spanned) textView.getText();

    // actual text of the span (this can be used along with the  `span`)
    final CharSequence task = spanned.subSequence(
      spanned.getSpanStart(this),
      spanned.getSpanEnd(this)
    );

    Debug.i("task done: %s, '%s'", span.isDone(), task);
  }

  @Override
  public void updateDrawState(@NonNull TextPaint ds) {
    // no op, so text is not rendered as a link
  }
}
