package io.noties.markwon.app.samples;

import android.text.style.BulletSpan;

import androidx.annotation.NonNull;

import org.commonmark.node.ListItem;

import io.noties.debug.Debug;
import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.MarkwonSpansFactory;
import io.noties.markwon.SpanFactory;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.core.CoreProps;
import io.noties.markwon.ext.tasklist.TaskListPlugin;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;
import io.noties.markwon.sample.annotations.Tag;

@MarkwonSampleInfo(
  id = "20201208150530",
  title = "Change bullet span",
  description = "Use a different span implementation to render bullet lists",
  artifacts = MarkwonArtifact.CORE,
  tags = {Tag.rendering, Tag.spanFactory, Tag.span}
)
public class ChangeBulletSpanSample extends MarkwonTextViewSample {
  @Override
  public void render() {
    final String md = "" +
      "* one\n" +
      "* two\n" +
      "* three\n" +
      "* * four\n" +
      "  * five\n\n" +
      "- [ ] and task?\n" +
      "- [x] it is";

    final Markwon markwon = Markwon.builder(context)
      .usePlugin(TaskListPlugin.create(context))
      .usePlugin(new AbstractMarkwonPlugin() {
        @Override
        public void configureSpansFactory(@NonNull MarkwonSpansFactory.Builder builder) {

          // store original span factory (provides both bullet and ordered lists)
          final SpanFactory original = builder.getFactory(ListItem.class);

          builder.setFactory(ListItem.class, (configuration, props) -> {
            if (CoreProps.LIST_ITEM_TYPE.require(props) == CoreProps.ListItemType.BULLET) {
              // additionally inspect bullet level
              final int level = CoreProps.BULLET_LIST_ITEM_LEVEL.require(props);
              Debug.i("rendering bullet list with level: %d", level);

              // return _system_ bullet span, but can be any
              return new BulletSpan();
            }
            return original != null ? original.getSpans(configuration, props) : null;
          });
        }
      })
      .build();

    markwon.setMarkdown(textView, md);
  }
}
