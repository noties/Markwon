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
public class HeadingTest extends BaseSuiteTest {

  @Test
  public void single_headings() {

    final int[] levels = {1, 2, 3, 4, 5, 6};

    for (int level : levels) {

      final Document document = document(
        span(HEADING, args("level", level), text("head" + level))
      );

      final StringBuilder builder = new StringBuilder();
      for (int i = 0; i < level; i++) {
        builder.append('#');
      }
      builder.append(" head").append(level);

      match(builder.toString(), document);
    }
  }
}
