package io.noties.markwon.core.suite;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import io.noties.markwon.test.TestSpan.Document;

import static io.noties.markwon.test.TestSpan.document;
import static io.noties.markwon.test.TestSpan.text;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class NoParagraphsTest extends BaseSuiteTest {
  /*
  This could be a paragraph

  But it is not and this one is not also
   */

  @Test
  public void test() {

    final Document document = document(
      text("This could be a paragraph"),
      text("\n\n"),
      text("But it is not and this one is not also")
    );

    matchInput("no-paragraphs.md", document);
  }
}
