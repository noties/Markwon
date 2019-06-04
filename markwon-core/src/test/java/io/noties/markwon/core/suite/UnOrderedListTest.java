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
public class UnOrderedListTest extends BaseSuiteTest {

  @Test
  public void single() {

    final Document document = document(
      span(UN_ORDERED_LIST, args("level", 0), text("ul"))
    );

    match("* ul", document);
  }

  @Test
  public void test() {
    /*
     * First
     * Second
     * Third
     */

    final Document document = document(
      span(UN_ORDERED_LIST,
        args("level", 0),
        text("First\n"),
        span(UN_ORDERED_LIST,
          args("level", 1),
          text("Second\n"),
          span(UN_ORDERED_LIST,
            args("level", 2),
            text("Third"))))
    );

    matchInput("ul.md", document);
  }

  @Test
  public void levels() {

    /*
     * First
     * * Second
     * * * Third
     */

    final Document document = document(
      span(UN_ORDERED_LIST,
        args("level", 0),
        text("First")),
      text("\n"),
      span(UN_ORDERED_LIST,
        args("level", 0),
        span(UN_ORDERED_LIST,
          args("level", 1),
          text("Second"))),
      text("\n"),
      span(UN_ORDERED_LIST,
        args("level", 0),
        span(UN_ORDERED_LIST,
          args("level", 1),
          span(UN_ORDERED_LIST,
            args("level", 2),
            text("Third"))))
    );

    matchInput("ul-levels.md", document);
  }
}
