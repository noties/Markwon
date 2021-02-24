package io.noties.markwon.app.samples.html;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Layout;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.LeadingMarginSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import io.noties.markwon.Markwon;
import io.noties.markwon.MarkwonVisitor;
import io.noties.markwon.SpannableBuilder;
import io.noties.markwon.app.BuildConfig;
import io.noties.markwon.app.R;
import io.noties.markwon.app.sample.ui.MarkwonSample;
import io.noties.markwon.core.MarkwonTheme;
import io.noties.markwon.html.HtmlPlugin;
import io.noties.markwon.html.HtmlTag;
import io.noties.markwon.html.MarkwonHtmlRenderer;
import io.noties.markwon.html.TagHandler;
import io.noties.markwon.image.ImagesPlugin;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;
import io.noties.markwon.sample.annotations.Tag;
import io.noties.markwon.utils.LeadingMarginUtils;
import io.noties.markwon.utils.NoCopySpannableFactory;

@MarkwonSampleInfo(
  id = "20200630120752",
  title = "Details HTML tag",
  description = "Handling of `details` HTML tag",
  artifacts = {MarkwonArtifact.HTML, MarkwonArtifact.IMAGE},
  tags = {Tag.image, Tag.rendering, Tag.html}
)
public class HtmlDetailsSample extends MarkwonSample {

  private Context context;
  private ViewGroup content;

  @Override
  protected int getLayoutResId() {
    return R.layout.sample_html_details;
  }

  @Override
  public void onViewCreated(@NotNull View view) {
    context = view.getContext();
    content = view.findViewById(R.id.content);
    render();
  }

  private void render() {
    final String md = "# Hello\n\n<details>\n" +
      "  <summary>stuff with \n\n*mark* **down**\n\n</summary>\n" +
      "  <p>\n\n" +
      "<!-- the above p cannot start right at the beginning of the line and is mandatory for everything else to work -->\n" +
      "## *formatted* **heading** with [a](link)\n" +
      "```java\n" +
      "code block\n" +
      "```\n" +
      "\n" +
      "  <details>\n" +
      "    <summary><small>nested</small> stuff</summary><p>\n" +
      "<!-- alternative placement of p shown above -->\n" +
      "\n" +
      "* list\n" +
      "* with\n" +
      "\n\n" +
      "![img](" + BuildConfig.GIT_REPOSITORY + "/raw/master/art/markwon_logo.png)\n\n" +
      "" +
      " 1. nested\n" +
      " 1. items\n" +
      "\n" +
      "    ```java\n" +
      "    // including code\n" +
      "    ```\n" +
      " 1. blocks\n" +
      "\n" +
      "<details><summary>The 3rd!</summary>\n\n" +
      "**bold** _em_\n</details>" +
      "  </p></details>\n" +
      "</p></details>\n\n" +
      "and **this** *is* how...";

    final Markwon markwon = Markwon.builder(context)
      .usePlugin(HtmlPlugin.create(plugin ->
        plugin.addHandler(new DetailsTagHandler())))
      .usePlugin(ImagesPlugin.create())
      .build();

    final Spanned spanned = markwon.toMarkdown(md);
    final DetailsParsingSpan[] spans = spanned.getSpans(0, spanned.length(), DetailsParsingSpan.class);

    // if we have no details, proceed as usual (single text-view)
    if (spans == null || spans.length == 0) {
      // no details
      final TextView textView = appendTextView();
      markwon.setParsedMarkdown(textView, spanned);
      return;
    }

    final List<DetailsElement> list = new ArrayList<>();

    for (DetailsParsingSpan span : spans) {
      final DetailsElement e = settle(new DetailsElement(spanned.getSpanStart(span), spanned.getSpanEnd(span), span.summary), list);
      if (e != null) {
        list.add(e);
      }
    }

    for (DetailsElement element : list) {
      initDetails(element, spanned);
    }

    sort(list);


    TextView textView;
    int start = 0;

    for (DetailsElement element : list) {

      if (element.start != start) {
        // subSequence and add new TextView
        textView = appendTextView();
        textView.setText(subSequenceTrimmed(spanned, start, element.start));
      }

      // now add details TextView
      textView = appendTextView();
      initDetailsTextView(markwon, textView, element);

      start = element.end;
    }

    if (start != spanned.length()) {
      // another textView with rest content
      textView = appendTextView();
      textView.setText(subSequenceTrimmed(spanned, start, spanned.length()));
    }
  }

  @NonNull
  private TextView appendTextView() {
    final View view = LayoutInflater.from(context)
      .inflate(R.layout.view_html_details_text_view, content, false);
    final TextView textView = view.findViewById(R.id.text_view);
    content.addView(view);
    return textView;
  }

  private void initDetailsTextView(
    @NonNull Markwon markwon,
    @NonNull TextView textView,
    @NonNull DetailsElement element) {

    // minor optimization
    textView.setSpannableFactory(NoCopySpannableFactory.getInstance());

    // so, each element with children is a details tag
    // there is a reason why we needed the SpannableBuilder in the first place -> we must revert spans
//        final SpannableStringBuilder builder = new SpannableStringBuilder();
    final SpannableBuilder builder = new SpannableBuilder();
    append(builder, markwon, textView, element, element);
    markwon.setParsedMarkdown(textView, builder.spannableStringBuilder());
  }

  private void append(
    @NonNull SpannableBuilder builder,
    @NonNull Markwon markwon,
    @NonNull TextView textView,
    @NonNull DetailsElement root,
    @NonNull DetailsElement element) {
    if (!element.children.isEmpty()) {

      final int start = builder.length();

//            builder.append(element.content);
      builder.append(subSequenceTrimmed(element.content, 0, element.content.length()));

      builder.setSpan(new ClickableSpan() {
        @Override
        public void onClick(@NonNull View widget) {
          element.expanded = !element.expanded;

          initDetailsTextView(markwon, textView, root);
        }
      }, start, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

      if (element.expanded) {
        for (DetailsElement child : element.children) {
          append(builder, markwon, textView, root, child);
        }
      }

      builder.setSpan(new DetailsSpan(markwon.configuration().theme(), element), start);

    } else {
      builder.append(element.content);
    }
  }

  // if null -> remove from where it was processed,
  // else replace from where it was processed with a new one (can become expandable)
  @Nullable
  private static DetailsElement settle(
    @NonNull DetailsElement element,
    @NonNull List<? extends DetailsElement> elements) {
    for (DetailsElement e : elements) {
      if (element.start > e.start && element.end <= e.end) {
        final DetailsElement settled = settle(element, e.children);
        if (settled != null) {

          // the thing is we must balance children if done like this
          // let's just create a tree actually, so we are easier to modify
          final Iterator<DetailsElement> iterator = e.children.iterator();
          while (iterator.hasNext()) {
            final DetailsElement balanced = settle(iterator.next(), Collections.singletonList(element));
            if (balanced == null) {
              iterator.remove();
            }
          }

          // add to our children
          e.children.add(element);
        }
        return null;
      }
    }
    return element;
  }

  private static void initDetails(@NonNull DetailsElement element, @NonNull Spanned spanned) {
    int end = element.end;
    for (int i = element.children.size() - 1; i >= 0; i--) {
      final DetailsElement child = element.children.get(i);
      if (child.end < end) {
        element.children.add(new DetailsElement(child.end, end, spanned.subSequence(child.end, end)));
      }
      initDetails(child, spanned);
      end = child.start;
    }

    final int start = (element.start + element.content.length());
    if (end != start) {
      element.children.add(new DetailsElement(start, end, spanned.subSequence(start, end)));
    }
  }

  private static void sort(@NonNull List<DetailsElement> elements) {
    Collections.sort(elements, (o1, o2) -> Integer.compare(o1.start, o2.start));
    for (DetailsElement element : elements) {
      sort(element.children);
    }
  }

  @NonNull
  private static CharSequence subSequenceTrimmed(@NonNull CharSequence cs, int start, int end) {

    while (start < end) {

      final boolean isStartEmpty = Character.isWhitespace(cs.charAt(start));
      final boolean isEndEmpty = Character.isWhitespace(cs.charAt(end - 1));

      if (!isStartEmpty && !isEndEmpty) {
        break;
      }

      if (isStartEmpty) {
        start += 1;
      }
      if (isEndEmpty) {
        end -= 1;
      }
    }

    return cs.subSequence(start, end);
  }

  private static class DetailsElement {

    final int start;
    final int end;
    final CharSequence content;
    final List<DetailsElement> children = new ArrayList<>(0);

    boolean expanded;

    DetailsElement(int start, int end, @NonNull CharSequence content) {
      this.start = start;
      this.end = end;
      this.content = content;
    }

    @Override
    @NonNull
    public String toString() {
      return "DetailsElement{" +
        "start=" + start +
        ", end=" + end +
        ", content=" + toStringContent(content) +
        ", children=" + children +
        ", expanded=" + expanded +
        '}';
    }

    @NonNull
    private static String toStringContent(@NonNull CharSequence cs) {
      return cs.toString().replaceAll("\n", "\\n");
    }
  }

  private static class DetailsTagHandler extends TagHandler {

    @Override
    public void handle(
      @NonNull MarkwonVisitor visitor,
      @NonNull MarkwonHtmlRenderer renderer,
      @NonNull HtmlTag tag) {

      int summaryEnd = -1;

      for (HtmlTag child : tag.getAsBlock().children()) {

        if (!child.isClosed()) {
          continue;
        }

        if ("summary".equals(child.name())) {
          summaryEnd = child.end();
        }

        final TagHandler tagHandler = renderer.tagHandler(child.name());
        if (tagHandler != null) {
          tagHandler.handle(visitor, renderer, child);
        } else if (child.isBlock()) {
          visitChildren(visitor, renderer, child.getAsBlock());
        }
      }

      if (summaryEnd > -1) {
        visitor.builder().setSpan(new DetailsParsingSpan(
          subSequenceTrimmed(visitor.builder(), tag.start(), summaryEnd)
        ), tag.start(), tag.end());
      }
    }

    @NonNull
    @Override
    public Collection<String> supportedTags() {
      return Collections.singleton("details");
    }
  }

  private static class DetailsParsingSpan {

    final CharSequence summary;

    DetailsParsingSpan(@NonNull CharSequence summary) {
      this.summary = summary;
    }
  }

  private static class DetailsSpan implements LeadingMarginSpan {

    private final DetailsElement element;
    private final int blockMargin;
    private final int blockQuoteWidth;

    private final Rect rect = new Rect();
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    DetailsSpan(@NonNull MarkwonTheme theme, @NonNull DetailsElement element) {
      this.element = element;
      this.blockMargin = theme.getBlockMargin();
      this.blockQuoteWidth = theme.getBlockQuoteWidth();
      this.paint.setStyle(Paint.Style.FILL);
    }

    @Override
    public int getLeadingMargin(boolean first) {
      return blockMargin;
    }

    @Override
    public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline, int bottom, CharSequence text, int start, int end, boolean first, Layout layout) {

      if (LeadingMarginUtils.selfStart(start, text, this)) {
        rect.set(x, top, x + blockMargin, bottom);
        if (element.expanded) {
          paint.setColor(Color.GREEN);
        } else {
          paint.setColor(Color.RED);
        }
        paint.setStyle(Paint.Style.FILL);
        c.drawRect(rect, paint);

      } else {

        if (element.expanded) {
          final int l = (blockMargin - blockQuoteWidth) / 2;
          rect.set(x + l, top, x + l + blockQuoteWidth, bottom);
          paint.setStyle(Paint.Style.FILL);
          paint.setColor(Color.GRAY);
          c.drawRect(rect, paint);
        }
      }
    }
  }
}
