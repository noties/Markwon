package io.noties.markwon.app.samples;

import androidx.annotation.NonNull;

import org.commonmark.node.Emphasis;
import org.commonmark.node.Node;
import org.commonmark.node.Text;
import org.commonmark.parser.Parser;
import org.commonmark.parser.delimiter.DelimiterProcessor;
import org.commonmark.parser.delimiter.DelimiterRun;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;
import io.noties.markwon.sample.annotations.Tag;

@MarkwonSampleInfo(
  id = "20200630194017",
  title = "Custom delimiter processor",
  description = "Custom parsing delimiter processor with `?` character",
  artifacts = MarkwonArtifact.CORE,
  tags = Tag.parsing
)
public class DelimiterProcessorSample extends MarkwonTextViewSample {
  @Override
  public void render() {
    final String md = "" +
      "?hello? there!";

    final Markwon markwon = Markwon.builder(context)
      .usePlugin(new AbstractMarkwonPlugin() {
        @Override
        public void configureParser(@NonNull Parser.Builder builder) {
          builder.customDelimiterProcessor(new QuestionDelimiterProcessor());
        }
      })
      .build();

    markwon.setMarkdown(textView, md);
  }
}

class QuestionDelimiterProcessor implements DelimiterProcessor {

  @Override
  public char getOpeningCharacter() {
    return '?';
  }

  @Override
  public char getClosingCharacter() {
    return '?';
  }

  @Override
  public int getMinLength() {
    return 1;
  }

  @Override
  public int getDelimiterUse(DelimiterRun opener, DelimiterRun closer) {
    if (opener.length() >= 1 && closer.length() >= 1) {
      return 1;
    }
    return 0;
  }

  @Override
  public void process(Text opener, Text closer, int delimiterUse) {
    final Node node = new Emphasis();

    Node tmp = opener.getNext();
    while (tmp != null && tmp != closer) {
      Node next = tmp.getNext();
      node.appendChild(tmp);
      tmp = next;
    }

    opener.insertAfter(node);
  }
}