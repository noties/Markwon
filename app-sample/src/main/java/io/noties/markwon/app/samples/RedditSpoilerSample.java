package io.noties.markwon.app.samples;

import android.graphics.Color;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;
import io.noties.markwon.sample.annotations.Tag;
import io.noties.markwon.utils.ColorUtils;

@MarkwonSampleInfo(
  id = "20200813145316",
  title = "Reddit spoiler",
  description = "An attempt to implement Reddit spoiler syntax `>! !<`",
  artifacts = MarkwonArtifact.CORE,
  tags = Tag.parsing
)
public class RedditSpoilerSample extends MarkwonTextViewSample {
  @Override
  public void render() {
    final String md = "" +
      "# Reddit spolier\n\n" +
      "Hello >!ugly so **ugly** !<, how are you?\n\n" +
      ">!a blockquote?!< should not be >!present!< yeah" +
      "";

    final Markwon markwon = Markwon.builder(context)
      .usePlugin(new RedditSpoilerPlugin())
      .build();

    markwon.setMarkdown(textView, md);
  }
}

class RedditSpoilerPlugin extends AbstractMarkwonPlugin {

  private static final Pattern RE = Pattern.compile(">!.+?!<");

  @NonNull
  @Override
  public String processMarkdown(@NonNull String markdown) {
    // replace all `>!` with `&gt;!` so no blockquote would be parsed (when spoiler starts at new line)
    return markdown.replaceAll(">!", "&gt;!");
  }

  @Override
  public void beforeSetText(@NonNull TextView textView, @NonNull Spanned markdown) {
    applySpoilerSpans((Spannable) markdown);
  }

  private static void applySpoilerSpans(@NonNull Spannable spannable) {
    final String text = spannable.toString();
    final Matcher matcher = RE.matcher(text);

    while (matcher.find()) {

      final RedditSpoilerSpan spoilerSpan = new RedditSpoilerSpan();
      final ClickableSpan clickableSpan = new ClickableSpan() {
        @Override
        public void onClick(@NonNull View widget) {
          spoilerSpan.setRevealed(true);
          widget.postInvalidateOnAnimation();
        }

        @Override
        public void updateDrawState(@NonNull TextPaint ds) {
          // no op
        }
      };

      final int s = matcher.start();
      final int e = matcher.end();
      spannable.setSpan(spoilerSpan, s, e, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
      spannable.setSpan(clickableSpan, s, e, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

      // we also can hide original syntax
      spannable.setSpan(new HideSpoilerSyntaxSpan(), s, s + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
      spannable.setSpan(new HideSpoilerSyntaxSpan(), e - 2, e, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
  }

  private static class RedditSpoilerSpan extends CharacterStyle {

    private boolean revealed;

    @Override
    public void updateDrawState(TextPaint tp) {
      if (!revealed) {
        // use the same text color
        tp.bgColor = Color.BLACK;
        tp.setColor(Color.BLACK);
      } else {
        // for example keep a bit of black background to remind that it is a spoiler
        tp.bgColor = ColorUtils.applyAlpha(Color.BLACK, 25);
      }
    }

    public void setRevealed(boolean revealed) {
      this.revealed = revealed;
    }
  }

  // we also could make text size smaller (but then MetricAffectingSpan should be used)
  private static class HideSpoilerSyntaxSpan extends CharacterStyle {

    @Override
    public void updateDrawState(TextPaint tp) {
      // set transparent color
      tp.setColor(0);
    }
  }
}