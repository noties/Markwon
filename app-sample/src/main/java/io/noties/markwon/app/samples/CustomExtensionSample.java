package io.noties.markwon.app.samples;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.text.style.ReplacementSpan;

import androidx.annotation.DrawableRes;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.commonmark.node.CustomNode;
import org.commonmark.node.Delimited;
import org.commonmark.node.Node;
import org.commonmark.node.Text;
import org.commonmark.parser.Parser;
import org.commonmark.parser.delimiter.DelimiterProcessor;
import org.commonmark.parser.delimiter.DelimiterRun;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.MarkwonVisitor;
import io.noties.markwon.app.sample.Tags;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;

@MarkwonSampleInfo(
  id = "202006181163248",
  title = "Custom extension",
  description = "Custom extension that adds an " +
    "icon from resources and renders it as image with " +
    "`@ic-name` syntax",
  artifacts = MarkwonArtifact.CORE,
  tags = {Tags.parsing, Tags.rendering, Tags.plugin, Tags.image, Tags.extension, Tags.span}
)
public class CustomExtensionSample extends MarkwonTextViewSample {
  @Override
  public void render() {
    final String md = "" +
      "# Hello! @ic-android-black-24\n\n" +
      "" +
      "Home 36 black: @ic-home-black-36\n\n" +
      "" +
      "Memory 48 black: @ic-memory-black-48\n\n" +
      "" +
      "### I AM ANOTHER HEADER\n\n" +
      "" +
      "Sentiment Satisfied 64 red: @ic-sentiment_satisfied-red-64" +
      "";

    // note that we haven't registered CorePlugin, as it's the only one that can be
    // implicitly deducted and added automatically. All other plugins require explicit
    // `usePlugin` call
    final Markwon markwon = Markwon.builder(context)
      .usePlugin(IconPlugin.create(IconSpanProvider.create(context, 0)))
      .build();

    markwon.setMarkdown(textView, md);
  }
}

class IconPlugin extends AbstractMarkwonPlugin {

  @NonNull
  public static IconPlugin create(@NonNull IconSpanProvider iconSpanProvider) {
    return new IconPlugin(iconSpanProvider);
  }

  private final IconSpanProvider iconSpanProvider;

  IconPlugin(@NonNull IconSpanProvider iconSpanProvider) {
    this.iconSpanProvider = iconSpanProvider;
  }

  @Override
  public void configureParser(@NonNull Parser.Builder builder) {
    builder.customDelimiterProcessor(IconProcessor.create());
  }

  @Override
  public void configureVisitor(@NonNull MarkwonVisitor.Builder builder) {
    builder.on(IconNode.class, (visitor, iconNode) -> {

      final String name = iconNode.name();
      final String color = iconNode.color();
      final String size = iconNode.size();

      if (!TextUtils.isEmpty(name)
        && !TextUtils.isEmpty(color)
        && !TextUtils.isEmpty(size)) {

        final int length = visitor.length();

        visitor.builder().append(name);
        visitor.setSpans(length, iconSpanProvider.provide(name, color, size));
        visitor.builder().append(' ');
      }
    });
  }

  @NonNull
  @Override
  public String processMarkdown(@NonNull String markdown) {
    return IconProcessor.prepare(markdown);
  }
}

abstract class IconSpanProvider {

  @SuppressWarnings("SameParameterValue")
  @NonNull
  public static IconSpanProvider create(@NonNull Context context, @DrawableRes int fallBack) {
    return new Impl(context, fallBack);
  }


  @NonNull
  public abstract IconSpan provide(@NonNull String name, @NonNull String color, @NonNull String size);


  private static class Impl extends IconSpanProvider {

    private final Context context;
    private final Resources resources;
    private final int fallBack;

    Impl(@NonNull Context context, @DrawableRes int fallBack) {
      this.context = context;
      this.resources = context.getResources();
      this.fallBack = fallBack;
    }

    @NonNull
    @Override
    public IconSpan provide(@NonNull String name, @NonNull String color, @NonNull String size) {
      final String resName = iconName(name, color, size);
      int resId = resources.getIdentifier(resName, "drawable", context.getPackageName());
      if (resId == 0) {
        resId = fallBack;
      }
      return new IconSpan(getDrawable(resId), IconSpan.ALIGN_CENTER);
    }


    @NonNull
    private static String iconName(@NonNull String name, @NonNull String color, @NonNull String size) {
      return "ic_" + name + "_" + color + "_" + size + "dp";
    }

    @NonNull
    private Drawable getDrawable(int resId) {
      //noinspection ConstantConditions
      return context.getDrawable(resId);
    }
  }
}

class IconSpan extends ReplacementSpan {

  @IntDef({ALIGN_BOTTOM, ALIGN_BASELINE, ALIGN_CENTER})
  @Retention(RetentionPolicy.CLASS)
  @interface Alignment {
  }

  public static final int ALIGN_BOTTOM = 0;
  public static final int ALIGN_BASELINE = 1;
  public static final int ALIGN_CENTER = 2; // will only center if drawable height is less than text line height


  private final Drawable drawable;

  private final int alignment;

  public IconSpan(@NonNull Drawable drawable, @Alignment int alignment) {
    this.drawable = drawable;
    this.alignment = alignment;
    if (drawable.getBounds().isEmpty()) {
      drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
    }
  }

  @Override
  public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, @Nullable Paint.FontMetricsInt fm) {

    final Rect rect = drawable.getBounds();

    if (fm != null) {
      fm.ascent = -rect.bottom;
      fm.descent = 0;

      fm.top = fm.ascent;
      fm.bottom = 0;
    }

    return rect.right;
  }

  @Override
  public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {

    final int b = bottom - drawable.getBounds().bottom;

    final int save = canvas.save();
    try {
      final int translationY;
      if (ALIGN_CENTER == alignment) {
        translationY = b - ((bottom - top - drawable.getBounds().height()) / 2);
      } else if (ALIGN_BASELINE == alignment) {
        translationY = b - paint.getFontMetricsInt().descent;
      } else {
        translationY = b;
      }
      canvas.translate(x, translationY);
      drawable.draw(canvas);
    } finally {
      canvas.restoreToCount(save);
    }
  }
}

class IconProcessor implements DelimiterProcessor {

  @NonNull
  public static IconProcessor create() {
    return new IconProcessor();
  }

  // ic-home-black-24
  private static final Pattern PATTERN = Pattern.compile("ic-(\\w+)-(\\w+)-(\\d+)");

  private static final String TO_FIND = IconNode.DELIMITER_STRING + "ic-";

  /**
   * Should be used when input string does not wrap icon definition with `@` from both ends.
   * So, `@ic-home-white-24` would become `@ic-home-white-24@`. This way parsing is easier
   * and more predictable (cannot specify multiple ending delimiters, as we would require them:
   * space, newline, end of a document, and a lot of more)
   *
   * @param input to process
   * @return processed string
   * @see #prepare(StringBuilder)
   */
  @NonNull
  public static String prepare(@NonNull String input) {
    final StringBuilder builder = new StringBuilder(input);
    prepare(builder);
    return builder.toString();
  }

  public static void prepare(@NonNull StringBuilder builder) {

    int start = builder.indexOf(TO_FIND);
    int end;

    while (start > -1) {

      end = iconDefinitionEnd(start + TO_FIND.length(), builder);

      // if we match our pattern, append `@` else ignore
      if (iconDefinitionValid(builder.subSequence(start + 1, end))) {
        builder.insert(end, '@');
      }

      // move to next
      start = builder.indexOf(TO_FIND, end);
    }
  }

  @Override
  public char getOpeningCharacter() {
    return IconNode.DELIMITER;
  }

  @Override
  public char getClosingCharacter() {
    return IconNode.DELIMITER;
  }

  @Override
  public int getMinLength() {
    return 1;
  }

  @Override
  public int getDelimiterUse(DelimiterRun opener, DelimiterRun closer) {
    return opener.length() >= 1 && closer.length() >= 1 ? 1 : 0;
  }

  @Override
  public void process(Text opener, Text closer, int delimiterUse) {

    final IconGroupNode iconGroupNode = new IconGroupNode();

    final Node next = opener.getNext();

    boolean handled = false;

    // process only if we have exactly one Text node
    if (next instanceof Text && next.getNext() == closer) {

      final String text = ((Text) next).getLiteral();

      if (!TextUtils.isEmpty(text)) {

        // attempt to match
        final Matcher matcher = PATTERN.matcher(text);
        if (matcher.matches()) {
          final IconNode iconNode = new IconNode(
            matcher.group(1),
            matcher.group(2),
            matcher.group(3)
          );
          iconGroupNode.appendChild(iconNode);
          next.unlink();
          handled = true;
        }
      }
    }

    if (!handled) {

      // restore delimiters if we didn't match

      iconGroupNode.appendChild(new Text(IconNode.DELIMITER_STRING));

      Node node;
      for (Node tmp = opener.getNext(); tmp != null && tmp != closer; tmp = node) {
        node = tmp.getNext();
        // append a child anyway
        iconGroupNode.appendChild(tmp);
      }

      iconGroupNode.appendChild(new Text(IconNode.DELIMITER_STRING));
    }

    opener.insertBefore(iconGroupNode);
  }

  private static int iconDefinitionEnd(int index, @NonNull StringBuilder builder) {

    // all spaces, new lines, non-words or digits,

    char c;

    int end = -1;
    for (int i = index; i < builder.length(); i++) {
      c = builder.charAt(i);
      if (Character.isWhitespace(c)
        || !(Character.isLetterOrDigit(c) || c == '-' || c == '_')) {
        end = i;
        break;
      }
    }

    if (end == -1) {
      end = builder.length();
    }

    return end;
  }

  private static boolean iconDefinitionValid(@NonNull CharSequence cs) {
    final Matcher matcher = PATTERN.matcher(cs);
    return matcher.matches();
  }
}

class IconNode extends CustomNode implements Delimited {

  public static final char DELIMITER = '@';

  public static final String DELIMITER_STRING = "" + DELIMITER;


  private final String name;

  private final String color;

  private final String size;

  public IconNode(@NonNull String name, @NonNull String color, @NonNull String size) {
    this.name = name;
    this.color = color;
    this.size = size;
  }

  @NonNull
  public String name() {
    return name;
  }

  @NonNull
  public String color() {
    return color;
  }

  @NonNull
  public String size() {
    return size;
  }

  @Override
  public String getOpeningDelimiter() {
    return DELIMITER_STRING;
  }

  @Override
  public String getClosingDelimiter() {
    return DELIMITER_STRING;
  }

  @Override
  @NonNull
  public String toString() {
    return "IconNode{" +
      "name='" + name + '\'' +
      ", color='" + color + '\'' +
      ", size='" + size + '\'' +
      '}';
  }
}

class IconGroupNode extends CustomNode {

}
