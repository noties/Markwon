package ru.noties.markwon.core.suite;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import ru.noties.markwon.test.TestSpan;

import static ru.noties.markwon.core.suite.TestFactory.CODE;
import static ru.noties.markwon.test.TestSpan.args;
import static ru.noties.markwon.test.TestSpan.document;
import static ru.noties.markwon.test.TestSpan.span;
import static ru.noties.markwon.test.TestSpan.text;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class CodeBlocks extends BaseSuiteTest {

  /*
  ```java
  final String s = null;
  ```
  ```html
  <html></html>
  ```
  ```
  nothing here
  ```
   */

  @Test
  public void test() {

    final TestSpan.Document document = document(
      span(CODE,
        args("multiline", true),
        text("\u00a0\nfinal String s = null;\n\u00a0")),
      text("\n\n"),
      span(CODE,
        args("multiline", true),
        text("\u00a0\n<html></html>\n\u00a0")),
      text("\n\n"),
      span(CODE,
        args("multiline", true),
        text("\u00a0\nnothing here\n\u00a0"))
    );

    matchInput("code-blocks.md", document);
  }
}
