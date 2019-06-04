package io.noties.markwon.core.suite;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import io.noties.markwon.test.TestSpan.Document;

import static io.noties.markwon.test.TestSpan.document;
import static io.noties.markwon.test.TestSpan.span;
import static io.noties.markwon.test.TestSpan.text;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class ParagraphTest extends BaseSuiteTest {

  /*
  So, this is a paragraph

  And this one is another
   */

  @Test
  public void test() {

    final Document document = document(
      span(PARAGRAPH,
        text("So, this is a paragraph")),
      text("\n\n"),
      span(PARAGRAPH,
        text("And this one is another"))
    );

    matchInput("paragraph.md", document);
  }

  @Override
  boolean useParagraphs() {
    return true;
  }
}
