package io.noties.markwon.app.samples;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.ReplacementSpan;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.image.ImagesPlugin;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;
import io.noties.markwon.sample.annotations.Tag;

@MarkwonSampleInfo(
  id = "20200629161505",
  title = "Read more plugin",
  description = "Plugin that adds expand/collapse (\"show all\"/\"show less\")",
  artifacts = MarkwonArtifact.CORE,
  tags = {Tag.plugin}
)
public class ReadMorePluginSample extends MarkwonTextViewSample {
  @Override
  public void render() {
    final String md = "" +
      "Lorem **ipsum** ![dolor](https://avatars2.githubusercontent.com/u/30618885?s=460&v=4) sit amet, consectetur adipiscing elit. Morbi vitae enim ut sem aliquet ultrices. Nunc a accumsan orci. Suspendisse tortor ante, lacinia ac scelerisque sed, dictum eget metus. Morbi ante augue, tristique eget quam in, vestibulum rutrum lacus. Nulla aliquam auctor cursus. Nulla at lacus condimentum, viverra lacus eget, sollicitudin ex. Cras efficitur leo dui, sit amet rutrum tellus venenatis et. Sed in facilisis libero. Etiam ultricies, nulla ut venenatis tincidunt, tortor erat tristique ante, non aliquet massa arcu eget nisl. Etiam gravida erat ante, sit amet lobortis mauris commodo nec. Praesent vitae sodales quam. Vivamus condimentum porta suscipit. Donec posuere id felis ac scelerisque. Vestibulum lacinia et leo id lobortis. Sed vitae dolor nec ligula dapibus finibus vel eu libero. Nam tincidunt maximus elit, sit amet tincidunt lacus laoreet malesuada.\n\n" +
      "here we ![are](https://avatars2.githubusercontent.com/u/30618885?s=460&v=4)";

    final Markwon markwon = Markwon.builder(context)
      .usePlugin(ImagesPlugin.create())
      .usePlugin(new ReadMorePlugin())
      .build();

    markwon.setMarkdown(textView, md);
  }
}

/**
 * Read more plugin based on text length. It is easier to implement than lines (need to adjust
 * last line to include expand/collapse text).
 */
class ReadMorePlugin extends AbstractMarkwonPlugin {

  @SuppressWarnings("FieldCanBeLocal")
  private final int maxLength = 150;

  @SuppressWarnings("FieldCanBeLocal")
  private final String labelMore = "Show more...";

  @SuppressWarnings("FieldCanBeLocal")
  private final String labelLess = "...Show less";

  @Override
  public void configure(@NonNull Registry registry) {
    // establish connections with all _dynamic_ content that your markdown supports,
    //  like images, tables, latex, etc
    registry.require(ImagesPlugin.class);
//    registry.require(TablePlugin.class);
  }

  @Override
  public void afterSetText(@NonNull TextView textView) {
    final CharSequence text = textView.getText();
    if (text.length() < maxLength) {
      // everything is OK, no need to ellipsize)
      return;
    }

    final int breakAt = breakTextAt(text, 0, maxLength);
    final CharSequence cs = createCollapsedString(text, 0, breakAt);
    textView.setText(cs);
  }

  @SuppressWarnings("SameParameterValue")
  @NonNull
  private CharSequence createCollapsedString(@NonNull CharSequence text, int start, int end) {
    final SpannableStringBuilder builder = new SpannableStringBuilder(text, start, end);

    // NB! each table row is represented as a space character and new-line (so length=2) no
    //  matter how many characters are inside table cells

    // we can _clean_ this builder, for example remove all dynamic content (like images and tables,
    //  but keep them in full/expanded version)
    //noinspection ConstantConditions
    if (true) {
      // it is an implementation detail but _mostly_ dynamic content is implemented as
      //  ReplacementSpans
      final ReplacementSpan[] spans = builder.getSpans(0, builder.length(), ReplacementSpan.class);
      if (spans != null) {
        for (ReplacementSpan span : spans) {
          builder.removeSpan(span);
        }
      }

      // NB! if there will be a table in _preview_ (collapsed) then each row will be represented as a
      // space and new-line
      trim(builder);
    }

    final CharSequence fullText = createFullText(text, builder);

    builder.append(' ');

    final int length = builder.length();
    builder.append(labelMore);
    builder.setSpan(new ClickableSpan() {
      @Override
      public void onClick(@NonNull View widget) {
        ((TextView) widget).setText(fullText);
      }
    }, length, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

    return builder;
  }

  @NonNull
  private CharSequence createFullText(@NonNull CharSequence text, @NonNull CharSequence collapsedText) {
    // full/expanded text can also be different,
    //  for example it can be kept as-is and have no `collapse` functionality (once expanded cannot collapse)
    //  or can contain collapse feature
    final CharSequence fullText;
    //noinspection ConstantConditions
    if (true) {
      // for example let's allow collapsing
      final SpannableStringBuilder builder = new SpannableStringBuilder(text);
      builder.append(' ');

      final int length = builder.length();
      builder.append(labelLess);
      builder.setSpan(new ClickableSpan() {
        @Override
        public void onClick(@NonNull View widget) {
          ((TextView) widget).setText(collapsedText);
        }
      }, length, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

      fullText = builder;
    } else {
      fullText = text;
    }

    return fullText;
  }

  private static void trim(@NonNull SpannableStringBuilder builder) {

    // NB! tables use `\u00a0` (non breaking space) which is not reported as white-space

    char c;

    for (int i = 0, length = builder.length(); i < length; i++) {
      c = builder.charAt(i);
      if (!Character.isWhitespace(c) && c != '\u00a0') {
        if (i > 0) {
          builder.replace(0, i, "");
        }
        break;
      }
    }

    for (int i = builder.length() - 1; i >= 0; i--) {
      c = builder.charAt(i);
      if (!Character.isWhitespace(c) && c != '\u00a0') {
        if (i < builder.length() - 1) {
          builder.replace(i, builder.length(), "");
        }
        break;
      }
    }
  }

  // depending on your locale these can be different
  // There is a BreakIterator in Android, but it is not reliable, still theoretically
  //  it should work better than hand-written and hardcoded rules
  @SuppressWarnings("SameParameterValue")
  private static int breakTextAt(@NonNull CharSequence text, int start, int max) {

    int last = start;

    // no need to check for _start_ (anyway will be ignored)
    for (int i = start + max - 1; i > start; i--) {
      final char c = text.charAt(i);
      if (Character.isWhitespace(c)
        || c == '.'
        || c == ','
        || c == '!'
        || c == '?') {
        // include this special character
        last = i - 1;
        break;
      }
    }

    if (last <= start) {
      // when used in subSequence last index is exclusive,
      //  so given max=150 would result in 0-149 subSequence
      return start + max;
    }

    return last;
  }
}

