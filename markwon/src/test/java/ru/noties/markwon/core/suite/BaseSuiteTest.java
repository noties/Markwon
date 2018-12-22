package ru.noties.markwon.core.suite;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Spanned;

import org.commonmark.node.BlockQuote;
import org.commonmark.node.Code;
import org.commonmark.node.Emphasis;
import org.commonmark.node.FencedCodeBlock;
import org.commonmark.node.Heading;
import org.commonmark.node.IndentedCodeBlock;
import org.commonmark.node.Link;
import org.commonmark.node.ListItem;
import org.commonmark.node.Node;
import org.commonmark.node.Paragraph;
import org.commonmark.node.StrongEmphasis;
import org.commonmark.node.ThematicBreak;
import org.robolectric.RuntimeEnvironment;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import ru.noties.markwon.AbstractMarkwonPlugin;
import ru.noties.markwon.Markwon;
import ru.noties.markwon.MarkwonConfiguration;
import ru.noties.markwon.MarkwonSpansFactory;
import ru.noties.markwon.RenderProps;
import ru.noties.markwon.SpanFactory;
import ru.noties.markwon.core.CorePlugin;
import ru.noties.markwon.core.CoreProps;
import ru.noties.markwon.test.TestSpan;
import ru.noties.markwon.test.TestSpanMatcher;
import ru.noties.markwon.test.TestUtil;

import static ru.noties.markwon.test.TestSpan.args;
import static ru.noties.markwon.test.TestSpan.span;

abstract class BaseSuiteTest {

  static final String BOLD = "bold";
  static final String ITALIC = "italic";
  static final String CODE = "code";
  static final String LINK = "link";
  static final String BLOCK_QUOTE = "blockquote";
  static final String PARAGRAPH = "paragraph";
  static final String ORDERED_LIST = "ordered-list";
  static final String UN_ORDERED_LIST = "un-ordered-list";
  static final String HEADING = "heading";
  static final String THEMATIC_BREAK = "thematic-break";

  void match(@NonNull String markdown, @NonNull TestSpan.Document document) {
    final Spanned spanned = markwon().toMarkdown(markdown);
    TestSpanMatcher.matches(spanned, document);
  }

  void matchInput(@NonNull String name, @NonNull TestSpan.Document document) {
    final Spanned spanned = markwon().toMarkdown(read(name));
    TestSpanMatcher.matches(spanned, document);
  }

  @NonNull
  private String read(@NonNull String name) {
    return TestUtil.read(this, "tests/" + name);
  }

  @NonNull
  Markwon markwon() {
    return Markwon.builder(RuntimeEnvironment.application)
      .usePlugin(CorePlugin.create(softBreakAddsNewLine()))
      .usePlugin(new AbstractMarkwonPlugin() {
        @Override
        public void configureSpansFactory(@NonNull MarkwonSpansFactory.Builder builder) {

          for (Map.Entry<Class<? extends Node>, SpanFactory> entry : CORE_NODES.entrySet()) {
            builder.setFactory(entry.getKey(), entry.getValue());
          }

          if (useParagraphs()) {
            builder.setFactory(Paragraph.class, new SpanFactory() {
              @Override
              public Object getSpans(@NonNull MarkwonConfiguration configuration, @NonNull RenderProps props) {
                return span(PARAGRAPH);
              }
            });
          }
        }
      })
      .build();
  }

  boolean useParagraphs() {
    return false;
  }

  boolean softBreakAddsNewLine() {
    return false;
  }

  private static final Map<Class<? extends Node>, SpanFactory> CORE_NODES;

  static {
    final Map<Class<? extends Node>, SpanFactory> factories = new HashMap<>();
    factories.put(BlockQuote.class, new NamedSpanFactory(BLOCK_QUOTE));
    factories.put(Code.class, new SpanFactory() {
      @Override
      public Object getSpans(@NonNull MarkwonConfiguration configuration, @NonNull RenderProps props) {
        return span(CODE, args("multiline", false));
      }
    });
    factories.put(FencedCodeBlock.class, new SpanFactory() {
      @Override
      public Object getSpans(@NonNull MarkwonConfiguration configuration, @NonNull RenderProps props) {
        return span(CODE, args("multiline", true));
      }
    });
    factories.put(IndentedCodeBlock.class, new SpanFactory() {
      @Override
      public Object getSpans(@NonNull MarkwonConfiguration configuration, @NonNull RenderProps props) {
        return span(CODE, args("multiline", true));
      }
    });
    factories.put(Emphasis.class, new NamedSpanFactory(ITALIC));
    factories.put(Heading.class, new SpanFactory() {
      @Override
      public Object getSpans(@NonNull MarkwonConfiguration configuration, @NonNull RenderProps props) {
        return span(HEADING, args("level", CoreProps.HEADING_LEVEL.require(props)));
      }
    });
    factories.put(Link.class, new SpanFactory() {
      @Override
      public Object getSpans(@NonNull MarkwonConfiguration configuration, @NonNull RenderProps props) {
        return span(LINK, args("href", CoreProps.LINK_DESTINATION.require(props)));
      }
    });
    factories.put(ListItem.class, new SpanFactory() {
      @Override
      public Object getSpans(@NonNull MarkwonConfiguration configuration, @NonNull RenderProps props) {
        final CoreProps.ListItemType type = CoreProps.LIST_ITEM_TYPE.require(props);
        if (CoreProps.ListItemType.BULLET == type) {
          return span(UN_ORDERED_LIST, args("level", CoreProps.BULLET_LIST_ITEM_LEVEL.require(props)));
        }
        return span(ORDERED_LIST, args("start", CoreProps.ORDERED_LIST_ITEM_NUMBER.require(props)));
      }
    });
    factories.put(StrongEmphasis.class, new NamedSpanFactory(BOLD));
    factories.put(ThematicBreak.class, new NamedSpanFactory(THEMATIC_BREAK));
    CORE_NODES = factories;
  }

  private static class NamedSpanFactory implements SpanFactory {

    private final String name;

    private NamedSpanFactory(@NonNull String name) {
      this.name = name;
    }

    @Nullable
    @Override
    public Object getSpans(@NonNull MarkwonConfiguration configuration, @NonNull RenderProps props) {
      return span(name, extractArgs(props));
    }

    @NonNull
    Map<String, Object> extractArgs(@NonNull RenderProps props) {
      return Collections.emptyMap();
    }
  }
}
