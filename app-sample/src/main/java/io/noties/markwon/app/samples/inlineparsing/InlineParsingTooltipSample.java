package io.noties.markwon.app.samples.inlineparsing;

import android.app.Activity;
import android.graphics.Point;
import android.text.Layout;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.vladsch.flexmark.parser.InlineParser;
import com.vladsch.flexmark.parser.internal.InlineParserImpl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.MarkwonVisitor;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.image.ImagesPlugin;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;
import io.noties.markwon.sample.annotations.Tag;

@MarkwonSampleInfo(
  id = "20200630195409",
  title = "Tooltip with inline parser",
  artifacts = MarkwonArtifact.INLINE_PARSER,
  tags = {Tag.parsing, Tag.rendering}
)
public class InlineParsingTooltipSample extends MarkwonTextViewSample {
  @Override
  public void render() {
    // NB! tooltip contents cannot have new lines
    final String md = "" +
      "\n" +
      "\n" +
      "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi vitae enim ut sem aliquet ultrices. Nunc a accumsan orci. Suspendisse tortor ante, lacinia ac scelerisque sed, dictum eget metus. Morbi ante augue, tristique eget quam in, vestibulum rutrum lacus. Nulla aliquam auctor cursus. Nulla at lacus condimentum, viverra lacus eget, sollicitudin ex. Cras efficitur leo dui, sit amet rutrum tellus venenatis et. Sed in facilisis libero. Etiam ultricies, nulla ut venenatis tincidunt, tortor erat tristique ante, non aliquet massa arcu eget nisl. Etiam gravida erat ante, sit amet lobortis mauris commodo nec. Praesent vitae sodales quam. Vivamus condimentum porta suscipit. Donec posuere id felis ac scelerisque. Vestibulum lacinia et leo id lobortis. Sed vitae dolor nec ligula dapibus finibus vel eu libero. Nam tincidunt maximus elit, sit amet tincidunt lacus laoreet malesuada.\n" +
      "\n" +
      "Aenean at urna leo. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Nulla facilisi. Integer lectus elit, congue a orci sed, dignissim sagittis sem. Aenean et pretium magna, nec ornare justo. Sed quis nunc blandit, luctus justo eget, pellentesque arcu. Pellentesque porta semper tristique. Donec et odio arcu. Nullam ultrices gravida congue. Praesent vel leo sed orci tempor luctus. Vivamus eget tortor arcu. Nullam sapien nulla, iaculis sit amet semper in, mattis nec metus. In porttitor augue id elit euismod mattis. Ut est justo, dapibus suscipit erat eu, pellentesque porttitor magna.\n" +
      "\n" +
      "Nunc porta orci eget dictum malesuada. Donec vehicula felis sit amet leo tincidunt placerat. Cras quis elit faucibus, porta elit at, sodales tortor. Donec elit mi, eleifend et maximus vitae, pretium varius velit. Integer maximus egestas urna, at semper augue egestas vitae. Phasellus arcu tellus, tincidunt eget tellus nec, hendrerit mollis mauris. Pellentesque commodo urna quis nisi ultrices, quis vehicula felis ultricies. Vivamus eu feugiat leo.\n" +
      "\n" +
      "Etiam sit amet lorem et eros suscipit rhoncus a a tellus. Sed pharetra dui purus, quis molestie leo congue nec. Suspendisse sed scelerisque quam. Vestibulum non laoreet felis. Fusce interdum euismod purus at scelerisque. Vivamus tempus varius nibh, sed accumsan nisl interdum non. Pellentesque rutrum egestas eros sit amet sollicitudin. Vivamus ultrices est erat. Curabitur gravida justo non felis euismod mollis. Ut porta finibus nulla, sed pellentesque purus euismod ac.\n" +
      "\n" +
      "Aliquam erat volutpat. Nullam suscipit sit amet tortor vel fringilla. Nulla facilisi. Nullam lacinia ex lacus, sit amet scelerisque justo semper a. Nullam ullamcorper, erat ac malesuada porta, augue erat sagittis mi, in auctor turpis mauris nec orci. Nunc sit amet felis placerat, pharetra diam nec, dapibus metus. Proin nulla orci, iaculis vitae vulputate vel, placerat ac erat. Morbi sit amet blandit velit. Cras consectetur vehicula lacus vel sagittis. Nunc tincidunt lacus in blandit faucibus. Curabitur vestibulum auctor vehicula. Sed quis ligula sit amet quam venenatis venenatis eget id felis. Maecenas feugiat nisl elit, facilisis tempus risus malesuada quis. " +
      "# Hello tooltip!\n\n" +
      "This is the !{tooltip label}(and actual content comes here)\n\n" +
      "what if it is !{here}(The contents can be blocks, limited though) instead?\n\n" +
      "![image](https://github.com/dcurtis/markdown-mark/raw/master/png/208x128-solid.png) anyway";

    final Markwon markwon = Markwon.builder(context)
      .usePlugin(MarkwonInlineParserPlugin.create(factoryBuilder ->
        factoryBuilder.addInlineProcessor(new TooltipInlineProcessor())))
      .usePlugin(new AbstractMarkwonPlugin() {
        @Override
        public void configureVisitor(@NonNull MarkwonVisitor.Builder builder) {
          builder.on(TooltipNode.class, (visitor, tooltipNode) -> {
            final int start = visitor.length();
            visitor.builder().append(tooltipNode.label);
            visitor.setSpans(start, new TooltipSpan(tooltipNode.contents));
          });
        }
      })
      .usePlugin(ImagesPlugin.create())
      .build();

    markwon.setMarkdown(textView, md);
  }
}

class TooltipInlineProcessor extends InlineParserImpl {

  // NB! without bang
  // `\\{` is required (although marked as redundant), without it - runtime crash
  @SuppressWarnings("RegExpRedundantEscape")
  private static final Pattern RE = Pattern.compile("\\{(.+?)\\}\\((.+?)\\)");

  @Override
  public char specialCharacter() {
    return '!';
  }

  @Nullable
  @Override
  protected Node parse() {
    final String match = match(RE);
    if (match == null) {
      return null;
    }

    final Matcher matcher = RE.matcher(match);
    if (matcher.matches()) {
      final String label = matcher.group(1);
      final String contents = matcher.group(2);
      return new TooltipNode(label, contents);
    }

    return null;
  }
}

class TooltipNode extends CustomNode {
  final String label;
  final String contents;

  TooltipNode(@NonNull String label, @NonNull String contents) {
    this.label = label;
    this.contents = contents;
  }
}

class TooltipSpan extends ClickableSpan {
  final String contents;

  TooltipSpan(@NonNull String contents) {
    this.contents = contents;
  }

  @Override
  public void onClick(@NonNull View widget) {
    // just to be safe
    if (!(widget instanceof TextView)) {
      return;
    }

    final TextView textView = (TextView) widget;
    final Spannable spannable = (Spannable) textView.getText();

    // find self ending position (can also obtain start)
//    final int start = spannable.getSpanStart(this);
    final int end = spannable.getSpanEnd(this);

    // weird, didn't find self
    if (/*start < 0 ||*/ end < 0) {
      return;
    }

    final Layout layout = textView.getLayout();
    if (layout == null) {
      // also weird
      return;
    }

    final int line = layout.getLineForOffset(end);

    // position inside TextView, these values must also be adjusted to parent widget
    // also note that container can
    final int y = layout.getLineBottom(line);
    final int x = (int) (layout.getPrimaryHorizontal(end) + 0.5F);

    final Window window = ((Activity) widget.getContext()).getWindow();
    final View decor = window.getDecorView();
    final Point point = relativeTo(decor, widget);

//            new Tooltip.Builder(widget.getContext())
//                    .anchor(x + point.x, y + point.y)
//                    .text(contents)
//                    .create()
//                    .show(widget, Tooltip.Gravity.TOP, false);

    // Toast is not reliable when tried to position on the screen
    //  but anyway, this is to showcase only
    final Toast toast = Toast.makeText(widget.getContext(), contents, Toast.LENGTH_LONG);
    toast.setGravity(Gravity.TOP | Gravity.START, x + point.x, y + point.y);
    toast.show();
  }

  @Override
  public void updateDrawState(@NonNull TextPaint ds) {
    // can customize appearance here as spans will be rendered as links
    super.updateDrawState(ds);
  }

  @NonNull
  private static Point relativeTo(@NonNull View parent, @NonNull View who) {
    return relativeTo(parent, who, new Point());
  }

  @NonNull
  private static Point relativeTo(@NonNull View parent, @NonNull View who, @NonNull Point point) {
    // NB! the scroll adjustments (we are interested in screen position,
    //  not real position inside parent)
    point.x += who.getLeft();
    point.y += who.getTop();
    point.x -= who.getScrollX();
    point.y -= who.getScrollY();
    if (who != parent
      && who.getParent() instanceof View) {
      relativeTo(parent, (View) who.getParent(), point);
    }
    return point;
  }
}
