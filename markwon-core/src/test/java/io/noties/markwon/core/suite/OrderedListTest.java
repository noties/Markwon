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
public class OrderedListTest extends BaseSuiteTest {

  /*
  1. First
     1. Second
        1. Third
   */

  @Test
  public void nested() {

    // wanted to use 1,2,3 as start numbers, but anything but `1` won't be treated as sub-list

    final Document document = document(
      span(ORDERED_LIST,
        args("start", 1),
        text("First\n"),
        span(ORDERED_LIST,
          args("start", 1),
          text("Second\n"),
          span(ORDERED_LIST,
            args("start", 1),
            text("Third"))))
    );

    matchInput("ol.md", document);
  }

  /*
  1. First
    2. Second
      3. Third
   */
  @Test
  public void two_spaces() {
    // just a regular flat-list (no sub-lists)
    // UPD: cannot have more than 3 spaces (0.29), now it is:
    // 1. First
    // 2. Second 3. Third

//    final Document document = document(
//      span(ORDERED_LIST,
//        args("start", 1),
//        text("First")),
//      text("\n"),
//      span(ORDERED_LIST,
//        args("start", 2),
//        text("Second")),
//      text("\n"),
//      span(ORDERED_LIST,
//        args("start", 3),
//        text("Third"))
//    );

    final Document document = document(
      span(ORDERED_LIST,
        args("start", 1),
        text("First")),
      text("\n"),
      span(ORDERED_LIST,
        args("start", 2),
        text("Second 3. Third"))
    );

    matchInput("ol-2-spaces.md", document);
  }

  /*
  5. Five
  6. Six
  7. Seven
   */
  @Test
  public void starts_with_5() {

    final Document document = document(
      span(ORDERED_LIST,
        args("start", 5),
        text("Five")),
      text("\n"),
      span(ORDERED_LIST,
        args("start", 6),
        text("Six")),
      text("\n"),
      span(ORDERED_LIST,
        args("start", 7),
        text("Seven"))
    );

    matchInput("ol-starts-with-5.md", document);
  }

  @Test
  public void single() {

    final Document document = document(
      span(ORDERED_LIST,
        args("start", 1),
        text("ol"))
    );

    match("1. ol", document);
  }
}
