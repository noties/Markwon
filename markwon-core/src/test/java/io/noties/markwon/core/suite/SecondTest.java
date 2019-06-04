package io.noties.markwon.core.suite;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import io.noties.markwon.test.TestSpan.Document;

import static io.noties.markwon.test.TestSpan.args;
import static io.noties.markwon.test.TestSpan.document;
import static io.noties.markwon.test.TestSpan.span;
import static io.noties.markwon.test.TestSpan.text;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class SecondTest extends BaseSuiteTest {

  /*
First **line** is *always*

> Some quote here!

# Header 1
## Header 2

and `some code` and more:

```java
the code in multiline
```
   */

  @Test
  public void test() {

    final Document document = document(
      text("First "),
      span(BOLD, text("line")),
      text(" is "),
      span(ITALIC, text("always")),
      text("\n\n"),
      span(BLOCK_QUOTE, text("Some quote here!")),
      text("\n\n"),
      span(HEADING, args("level", 1), text("Header 1")),
      text("\n\n"),
      span(HEADING, args("level", 2), text("Header 2")),
      text("\n\n"),
      text("and "),
      span(CODE, args("multiline", false), text("\u00a0some code\u00a0")),
      text(" and more:"),
      text("\n\n"),
      span(CODE, args("multiline", true), text("\u00a0\nthe code in multiline\n\u00a0"))
    );

    matchInput("second.md", document);
  }
}
