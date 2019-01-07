package ru.noties.markwon.core.suite;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import ru.noties.markwon.test.TestSpan.Document;

import static ru.noties.markwon.test.TestSpan.document;
import static ru.noties.markwon.test.TestSpan.text;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class SoftBreakAddsNewLineTest extends BaseSuiteTest {

  /*
hello there!
this one is on the next line
hard break to the full extend
   */

  @Test
  public void test() {

    final Document document = document(
      text("hello there!\n"),
      text("this one is on the next line\n"),
      text("hard break to the full extend")
    );

    matchInput("soft-break-adds-new-line.md", document);
  }

  @Override
  boolean softBreakAddsNewLine() {
    return true;
  }
}
