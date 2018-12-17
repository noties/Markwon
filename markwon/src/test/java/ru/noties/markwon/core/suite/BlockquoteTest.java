package ru.noties.markwon.core.suite;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import ru.noties.markwon.test.TestSpan.Document;

import static ru.noties.markwon.core.suite.TestFactory.BLOCK_QUOTE;
import static ru.noties.markwon.test.TestSpan.document;
import static ru.noties.markwon.test.TestSpan.span;
import static ru.noties.markwon.test.TestSpan.text;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class BlockquoteTest extends BaseSuiteTest {

  /*
    > First
    > > Second
    > > > Third
   */

  @Test
  public void nested() {

    final Document document = document(
      span(BLOCK_QUOTE,
        text("First\n\n"),
        span(BLOCK_QUOTE,
          text("Second\n\n"),
          span(BLOCK_QUOTE,
            text("Third"))))
    );

    matchInput("nested-blockquotes.md", document);
  }

  @Test
  public void single() {

    final Document document = document(
      span(BLOCK_QUOTE, text("blockquote")));

    matchInput("single-blockquote.md", document);
  }
}
