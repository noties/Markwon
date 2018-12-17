package ru.noties.markwon.core.suite;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import ru.noties.markwon.test.TestSpan.Document;

import static ru.noties.markwon.core.suite.TestFactory.BOLD;
import static ru.noties.markwon.core.suite.TestFactory.CODE;
import static ru.noties.markwon.core.suite.TestFactory.ITALIC;
import static ru.noties.markwon.test.TestSpan.args;
import static ru.noties.markwon.test.TestSpan.document;
import static ru.noties.markwon.test.TestSpan.span;
import static ru.noties.markwon.test.TestSpan.text;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class DeeplyNestedTest extends BaseSuiteTest {

  /*
   **bold *bold italic `bold italic code` bold italic* bold** normal
   */

  @Test
  public void test() {

    final Document document = document(
      span(BOLD,
        text("bold "),
        span(ITALIC,
          text("bold italic "),
          span(CODE,
            args("multiline", false),
            text("\u00a0bold italic code\u00a0")),
          text(" bold italic")),
        text(" bold")),
      text(" normal")
    );

    matchInput("deeply-nested.md", document);
  }
}
