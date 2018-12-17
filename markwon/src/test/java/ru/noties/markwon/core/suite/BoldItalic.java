package ru.noties.markwon.core.suite;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import ru.noties.markwon.test.TestSpan;

import static ru.noties.markwon.core.suite.TestFactory.BOLD;
import static ru.noties.markwon.core.suite.TestFactory.ITALIC;
import static ru.noties.markwon.test.TestSpan.document;
import static ru.noties.markwon.test.TestSpan.span;
import static ru.noties.markwon.test.TestSpan.text;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class BoldItalic extends BaseSuiteTest {

  /*
   **_bold italic_**
   */

  @Test
  public void test() {

    final TestSpan.Document document = document(
      span(BOLD,
        span(ITALIC, text("bold italic"))));

    matchInput("bold-italic.md", document);
  }
}
