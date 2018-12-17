package ru.noties.markwon.core.suite;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import ru.noties.markwon.test.TestSpan.Document;

import static ru.noties.markwon.core.suite.TestFactory.CODE;
import static ru.noties.markwon.test.TestSpan.args;
import static ru.noties.markwon.test.TestSpan.document;
import static ru.noties.markwon.test.TestSpan.span;
import static ru.noties.markwon.test.TestSpan.text;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class CodeTest extends BaseSuiteTest {

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
  public void multiple_blocks() {

    final Document document = document(
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

  @Test
  public void single() {

    final Document document = document(
      span(CODE, args("multiline", false), text("\u00a0code\u00a0"))
    );

    matchInput("single-code.md", document);
  }

  @Test
  public void single_block() {

    final Document document = document(
      span(CODE, args("multiline", true), text("\u00a0\ncode block\n\u00a0"))
    );

    matchInput("single-code-block.md", document);
  }
}
