package io.noties.markwon.sample.tasklist;

import android.app.Activity;
import android.os.Bundle;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.noties.debug.Debug;
import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.MarkwonSpansFactory;
import io.noties.markwon.SpanFactory;
import io.noties.markwon.ext.tasklist.TaskListItem;
import io.noties.markwon.ext.tasklist.TaskListPlugin;
import io.noties.markwon.ext.tasklist.TaskListSpan;
import io.noties.markwon.sample.R;

public class TaskListActivity extends Activity {

    private TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_view);

        textView = findViewById(R.id.text_view);

        mutate();
    }

    private void mutate() {

        final Markwon markwon = Markwon.builder(this)
                .usePlugin(TaskListPlugin.create(this))
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

                            return new Object[]{
                                    span,
                                    new TaskListToggleSpan(span)
                            };
                        });
                    }
                })
                .build();

        final String md = "" +
                "- [ ] Not done here!\n" +
                "- [x] and done\n" +
                "- [X] and again!\n" +
                "* [ ] **and** syntax _included_ `code`";

        markwon.setMarkdown(textView, md);
    }

    private static class TaskListToggleSpan extends ClickableSpan {

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
}
