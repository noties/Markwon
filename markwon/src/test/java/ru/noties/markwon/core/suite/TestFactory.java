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
    return null;
  }

  @Nullable
  @Override
  public Object code(@NonNull MarkwonTheme theme, boolean multiline) {
    return span(CODE, args("multiline", multiline));
  }

  @Nullable
  @Override
  public Object orderedListItem(@NonNull MarkwonTheme theme, int startNumber) {
    return null;
  }

  @Nullable
  @Override
  public Object bulletListItem(@NonNull MarkwonTheme theme, int level) {
    return null;
  }

  @Nullable
  @Override
  public Object thematicBreak(@NonNull MarkwonTheme theme) {
    return null;
  }

  @Nullable
  @Override
  public Object heading(@NonNull MarkwonTheme theme, int level) {
    return null;
  }

  @Nullable
  @Override
  public Object paragraph(boolean inTightList) {
    return null;
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
