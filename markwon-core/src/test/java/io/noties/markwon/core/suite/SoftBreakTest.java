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
public class SoftBreakTest extends BaseSuiteTest {

  @Test
  public void test() {

    final Document document = document(
      text("First line "),
      text("same line but with space between "),
      text("this is also the first line")
    );

    matchInput("soft-break.md", document);
  }
}
