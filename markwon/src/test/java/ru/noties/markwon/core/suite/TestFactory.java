package ru.noties.markwon.core.suite;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.noties.markwon.SpannableFactory;
import ru.noties.markwon.core.MarkwonTheme;
import ru.noties.markwon.core.spans.LinkSpan;
import ru.noties.markwon.image.AsyncDrawableLoader;
import ru.noties.markwon.image.ImageSize;
import ru.noties.markwon.image.ImageSizeResolver;

import static ru.noties.markwon.test.TestSpan.args;
import static ru.noties.markwon.test.TestSpan.span;

class TestFactory implements SpannableFactory {

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

  private final boolean useParagraphs;

  TestFactory(boolean useParagraphs) {
    this.useParagraphs = useParagraphs;
  }

  @Nullable
  @Override
  public Object strongEmphasis() {
    return span(BOLD);
  }

  @Nullable
  @Override
  public Object emphasis() {
    return span(ITALIC);
  }

  @Nullable
  @Override
  public Object blockQuote(@NonNull MarkwonTheme theme) {
    return span(BLOCK_QUOTE);
  }

  @Nullable
  @Override
  public Object code(@NonNull MarkwonTheme theme, boolean multiline) {
    return span(CODE, args("multiline", multiline));
  }

  @Nullable
  @Override
  public Object orderedListItem(@NonNull MarkwonTheme theme, int startNumber) {
    return span(ORDERED_LIST, args("start", startNumber));
  }

  @Nullable
  @Override
  public Object bulletListItem(@NonNull MarkwonTheme theme, int level) {
    return span(UN_ORDERED_LIST, args("level", level));
  }

  @Nullable
  @Override
  public Object thematicBreak(@NonNull MarkwonTheme theme) {
    return span(THEMATIC_BREAK);
  }

  @Nullable
  @Override
  public Object heading(@NonNull MarkwonTheme theme, int level) {
    return span(HEADING, args("level", level));
  }

  @Nullable
  @Override
  public Object paragraph(boolean inTightList) {
    return useParagraphs
      ? span(PARAGRAPH)
      : null;
  }

  @Nullable
  @Override
  public Object image(@NonNull MarkwonTheme theme, @NonNull String destination, @NonNull AsyncDrawableLoader loader, @NonNull ImageSizeResolver imageSizeResolver, @Nullable ImageSize imageSize, boolean replacementTextIsLink) {
    return null;
  }

  @Nullable
  @Override
  public Object link(@NonNull MarkwonTheme theme, @NonNull String destination, @NonNull LinkSpan.Resolver resolver) {
    return span(LINK, args("href", destination));
  }
}
