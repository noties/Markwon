package ru.noties.markwon.core.suite;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import ru.noties.markwon.test.TestSpan.Document;

import static ru.noties.markwon.test.TestSpan.args;
import static ru.noties.markwon.test.TestSpan.document;
import static ru.noties.markwon.test.TestSpan.span;
import static ru.noties.markwon.test.TestSpan.text;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class FirstTest extends BaseSuiteTest {

  /*
  Here is some [link](https://my.href)
  **bold _bold italic_ bold** normal
   */

  @Test
  public void test() {

    final Document document = document(
      text("Here is some "),
      span(LINK,
        args("href", "https://my.href"),
        text("link")),
      text(" "),
      span(BOLD,
        text("bold "),
        span(ITALIC,
          text("bold italic")),
        text(" bold")),
      text(" normal")
    );

    matchInput("first.md", document);
  }
}
