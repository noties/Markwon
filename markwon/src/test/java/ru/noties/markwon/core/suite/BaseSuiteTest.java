package ru.noties.markwon.core.suite;

import android.support.annotation.NonNull;
import android.text.Spanned;

import org.robolectric.RuntimeEnvironment;

import ru.noties.markwon.AbstractMarkwonPlugin;
import ru.noties.markwon.Markwon;
import ru.noties.markwon.MarkwonConfiguration;
import ru.noties.markwon.core.CorePlugin;
import ru.noties.markwon.test.TestSpan;
import ru.noties.markwon.test.TestSpanMatcher;
import ru.noties.markwon.test.TestUtil;

abstract class BaseSuiteTest {

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
      .use(CorePlugin.create(softBreakAddsNewLine()))
      .use(new AbstractMarkwonPlugin() {
        @Override
        public void configureConfiguration(@NonNull MarkwonConfiguration.Builder builder) {
          builder.factory(new TestFactory(useParagraphs()));
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
}
