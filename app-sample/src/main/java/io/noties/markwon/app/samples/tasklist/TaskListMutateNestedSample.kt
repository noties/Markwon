package io.noties.markwon.app.samples.tasklist

import android.text.style.ClickableSpan
import android.view.View
import io.noties.debug.Debug
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonVisitor
import io.noties.markwon.SoftBreakAddsNewLinePlugin
import io.noties.markwon.SpannableBuilder
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample
import io.noties.markwon.ext.tasklist.TaskListItem
import io.noties.markwon.ext.tasklist.TaskListPlugin
import io.noties.markwon.ext.tasklist.TaskListProps
import io.noties.markwon.ext.tasklist.TaskListSpan
import io.noties.markwon.sample.annotations.MarkwonArtifact
import io.noties.markwon.sample.annotations.MarkwonSampleInfo
import io.noties.markwon.sample.annotations.Tag
import org.commonmark.node.AbstractVisitor
import org.commonmark.node.Block
import org.commonmark.node.HardLineBreak
import org.commonmark.node.Node
import org.commonmark.node.Paragraph
import org.commonmark.node.SoftLineBreak
import org.commonmark.node.Text

@MarkwonSampleInfo(
  id = "20201228120444",
  title = "Task list mutate nested",
  description = "Task list mutation with nested items",
  artifacts = [MarkwonArtifact.EXT_TASKLIST],
  tags = [Tag.plugin]
)
class TaskListMutateNestedSample : MarkwonTextViewSample() {
  override fun render() {
    val md = """
      # Task list
      - [ ] not done
      - [X] done
        - [ ] nested not done 
          and text and textand text and text
        - [X] nested done
    """.trimIndent()

    val markwon = Markwon.builder(context)
      .usePlugin(TaskListPlugin.create(context))
      .usePlugin(SoftBreakAddsNewLinePlugin.create())
      .usePlugin(object : AbstractMarkwonPlugin() {
        override fun configureVisitor(builder: MarkwonVisitor.Builder) {
          builder.on(TaskListItem::class.java) { visitor, node ->

            val length = visitor.length()

            visitor.visitChildren(node)

            TaskListProps.DONE.set(visitor.renderProps(), node.isDone)

            val spans = visitor.configuration()
              .spansFactory()
              .get(TaskListItem::class.java)
              ?.getSpans(visitor.configuration(), visitor.renderProps())

            if (spans != null) {

              val taskListSpan = if (spans is Array<*>) {
                spans.first { it is TaskListSpan } as? TaskListSpan
              } else {
                spans as? TaskListSpan
              }

              Debug.i("#### ${visitor.builder().substring(length, length + 3)}")
              val content = TaskListContextVisitor.contentLength(node)
              Debug.i("#### content: $content, '${visitor.builder().subSequence(length, length + content)}'")

              if (content > 0 && taskListSpan != null) {
                // maybe additionally identify this task list (for persistence)
                visitor.builder().setSpan(
                  ToggleTaskListSpan(taskListSpan, visitor.builder().substring(length, length + content)),
                  length,
                  length + content
                )
              }
            }

            SpannableBuilder.setSpans(
              visitor.builder(),
              spans,
              length,
              visitor.length()
            )

            if (visitor.hasNext(node)) {
              visitor.ensureNewLine()
            }
          }
        }
      })
      .build()

    markwon.setMarkdown(textView, md)
  }

  class TaskListContextVisitor : AbstractVisitor() {

    companion object {
      fun contentLength(node: Node): Int {
        val visitor = TaskListContextVisitor()
        visitor.visitChildren(node)
        return visitor.contentLength
      }
    }

    var contentLength: Int = 0

    override fun visit(text: Text) {
      super.visit(text)
      contentLength += text.literal.length
    }

    // NB! if count both soft and hard breaks as having length of 1
    override fun visit(softLineBreak: SoftLineBreak?) {
      super.visit(softLineBreak)
      contentLength += 1
    }

    // NB! if count both soft and hard breaks as having length of 1
    override fun visit(hardLineBreak: HardLineBreak?) {
      super.visit(hardLineBreak)
      contentLength += 1
    }

    override fun visitChildren(parent: Node) {
      var node = parent.firstChild
      while (node != null) {
        // A subclass of this visitor might modify the node, resulting in getNext returning a different node or no
        // node after visiting it. So get the next node before visiting.
        val next = node.next
        if (node is Block && node !is Paragraph) {
          break
        }
        node.accept(this)
        node = next
      }
    }
  }

  class ToggleTaskListSpan(
    val span: TaskListSpan,
    val content: String
  ) : ClickableSpan() {
    override fun onClick(widget: View) {
      span.isDone = !span.isDone
      widget.invalidate()
      Debug.i("task-list click, isDone: ${span.isDone}, content: '$content'")
    }
  }
}