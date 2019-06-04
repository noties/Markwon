package io.noties.markwon.core.suite;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import io.noties.markwon.test.TestSpan;

import static io.noties.markwon.test.TestSpan.document;
import static io.noties.markwon.test.TestSpan.span;
import static io.noties.markwon.test.TestSpan.text;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class BoldItalicTest extends BaseSuiteTest {

  @Test
  public void test() {

    final TestSpan.Document document = document(
      span(BOLD,
        span(ITALIC, text("bold italic"))));

    match("**_bold italic_**", document);
  }
}
