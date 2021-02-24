package io.noties.markwon.app.samples.inlineparsing;

import android.graphics.Color;
import android.text.style.ForegroundColorSpan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.commonmark.node.CustomNode;
import org.commonmark.node.Node;

import java.util.regex.Pattern;

import io.noties.debug.Debug;
import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.MarkwonSpansFactory;
import io.noties.markwon.MarkwonVisitor;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.inlineparser.InlineProcessor;
import io.noties.markwon.inlineparser.MarkwonInlineParserPlugin;
import io.noties.markwon.inlineparser.OpenBracketInlineProcessor;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;
import io.noties.markwon.sample.annotations.Tag;

@MarkwonSampleInfo(
  id = "20200819071751",
  title = "Inline Parsing of square brackets",
  description = "Disable OpenBracketInlineParser in order " +
    "to parse own markdown syntax based on `[` character(s). This would disable native " +
    "markdown [links](#) but not images ![image-alt](#)",
  artifacts = MarkwonArtifact.INLINE_PARSER,
  tags = {Tag.parsing}
)
public class InlineParsingSquareBracketsSample extends MarkwonTextViewSample {
  @Override
  public void render() {
    final String md = "" +
      "# Hello\n" +
      "Hey! [[my text]] here and what to do with it?\n\n" +
      "[[at the beginning]] of a line with [links](#) disabled";

    final Markwon markwon = Markwon.builder(context)
      .usePlugin(MarkwonInlineParserPlugin.create(factoryBuilder ->
        factoryBuilder
          .addInlineProcessor(new MyTextInlineProcessor())
          .excludeInlineProcessor(OpenBracketInlineProcessor.class)))
      .usePlugin(new AbstractMarkwonPlugin() {
        @Override
        public void configureVisitor(@NonNull MarkwonVisitor.Builder builder) {
          builder
            .on(MyTextNode.class, new GenericInlineNodeVisitor())
            .on(NotMyTextNode.class, new GenericInlineNodeVisitor());
        }

        @Override
        public void configureSpansFactory(@NonNull MarkwonSpansFactory.Builder builder) {
          builder
            .setFactory(MyTextNode.class, (configuration, props) -> new ForegroundColorSpan(Color.RED))
            .setFactory(NotMyTextNode.class, (configuration, props) -> new ForegroundColorSpan(Color.GREEN));
        }
      })
      .build();

    markwon.setMarkdown(textView, md);
  }

  private static class GenericInlineNodeVisitor implements MarkwonVisitor.NodeVisitor<Node> {
    @Override
    public void visit(@NonNull MarkwonVisitor visitor, @NonNull Node node) {
      final int length = visitor.length();
      visitor.visitChildren(node);
      visitor.setSpansForNodeOptional(node, length);
    }
  }

  private static class MyTextInlineProcessor extends InlineProcessor {

    private static final Pattern RE = Pattern.compile("\\[\\[(.+?)\\]\\]");

    @Override
    public char specialCharacter() {
      return '[';
    }

    @Nullable
    @Override
    protected Node parse() {
      final String match = match(RE);
      Debug.i(match);
      if (match != null) {
        // consume syntax
        final String text = match.substring(2, match.length() - 2);
        final Node node;
        // for example some condition checking
        if (text.equals("my text")) {
          node = new MyTextNode();
        } else {
          node = new NotMyTextNode();
        }
        node.appendChild(text(text));
        return node;
      }
      return null;
    }
  }

  private static class MyTextNode extends CustomNode {
  }

  private static class NotMyTextNode extends CustomNode {
  }
}
