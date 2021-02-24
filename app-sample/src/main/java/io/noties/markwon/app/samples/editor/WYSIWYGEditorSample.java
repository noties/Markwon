package io.noties.markwon.app.samples.editor;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.method.LinkMovementMethod;
import android.text.style.ReplacementSpan;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.noties.debug.Debug;
import io.noties.markwon.Markwon;
import io.noties.markwon.SoftBreakAddsNewLinePlugin;
import io.noties.markwon.app.samples.editor.shared.BlockQuoteEditHandler;
import io.noties.markwon.app.samples.editor.shared.CodeEditHandler;
import io.noties.markwon.app.samples.editor.shared.HeadingEditHandler;
import io.noties.markwon.app.samples.editor.shared.LinkEditHandler;
import io.noties.markwon.app.samples.editor.shared.MarkwonEditTextSample;
import io.noties.markwon.app.samples.editor.shared.StrikethroughEditHandler;
import io.noties.markwon.editor.MarkwonEditor;
import io.noties.markwon.editor.MarkwonEditorTextWatcher;
import io.noties.markwon.editor.PersistedSpans;
import io.noties.markwon.editor.handler.EmphasisEditHandler;
import io.noties.markwon.editor.handler.StrongEmphasisEditHandler;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;
import io.noties.markwon.sample.annotations.Tag;

@MarkwonSampleInfo(
  id = "20200908133515",
  title = "WYSIWG editor",
  description = "A possible direction to implement what-you-see-is-what-you-get editor",
  artifacts = MarkwonArtifact.EDITOR,
  tags = Tag.rendering
)
public class WYSIWYGEditorSample extends MarkwonEditTextSample {
  @Override
  public void render() {

    // when automatic line break is inserted and text is inside margin span (blockquote, list, etc)
    //  be prepared to encounter selection bugs (selection would be drawn at the place as is no margin
    //  span is present)

    final Markwon markwon = Markwon.builder(context)
      .usePlugin(SoftBreakAddsNewLinePlugin.create())
      .build();

    final MarkwonEditor editor = MarkwonEditor.builder(markwon)
      .punctuationSpan(HidePunctuationSpan.class, new PersistedSpans.SpanFactory<HidePunctuationSpan>() {
        @NonNull
        @Override
        public HidePunctuationSpan create() {
          return new HidePunctuationSpan();
        }
      })
      .useEditHandler(new EmphasisEditHandler())
      .useEditHandler(new StrongEmphasisEditHandler())
      .useEditHandler(new StrikethroughEditHandler())
      .useEditHandler(new CodeEditHandler())
      .useEditHandler(new BlockQuoteEditHandler())
      .useEditHandler(new LinkEditHandler(new LinkEditHandler.OnClick() {
        @Override
        public void onClick(@NonNull View widget, @NonNull String link) {
          Debug.e("clicked: %s", link);
        }
      }))
      .useEditHandler(new HeadingEditHandler())
      .build();

    // for links to be clickable
    //   NB! markwon MovementMethodPlugin cannot be used here as editor do not execute `beforeSetText`)
    editText.setMovementMethod(LinkMovementMethod.getInstance());

    editText.addTextChangedListener(MarkwonEditorTextWatcher.withProcess(editor));
  }

  private static class HidePunctuationSpan extends ReplacementSpan {

    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, @Nullable Paint.FontMetricsInt fm) {
      // last space (which is swallowed until next non-space character appears)
      // block quote
      // code tick

//      Debug.i("text: '%s', %d-%d (%d)", text.subSequence(start, end), start, end, text.length());
      if (end == text.length()) {
        // TODO: find first non-space character (not just first one because commonmark allows
        //  arbitrary (0-3) white spaces before content starts

        //  TODO: if all white space - render?
        final char c = text.charAt(start);
        if ('#' == c
          || '>' == c
          || '-' == c // TODO: not thematic break
          || '+' == c
          // `*` is fine but only for a list
          || isBulletList(text, c, start, end)
          || Character.isDigit(c) // assuming ordered list (replacement should only happen for ordered lists)
          || Character.isWhitespace(c)) {
          return (int) (paint.measureText(text, start, end) + 0.5F);
        }
      }
      return 0;
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
      // will be called only when getSize is not 0 (and if it was once reported as 0...)
      if (end == text.length()) {

        // if first non-space is `*` then check for is bullet
        //  else `**` would be still rendered at the end of the emphasis
        if (text.charAt(start) == '*'
          && !isBulletList(text, '*', start, end)) {
          return;
        }

        // TODO: inline code last tick received here, handle it (do not highlight)
        //  why can't we have reported width in this method for supplied text?

        // let's use color to make it distinct from the rest of the text for demonstration purposes
        paint.setColor(0xFFff0000);

        canvas.drawText(text, start, end, x, y, paint);
      }
    }

    private static boolean isBulletList(@NonNull CharSequence text, char firstChar, int start, int end) {
      return '*' == firstChar
        && ((end - start == 1) || (Character.isWhitespace(text.charAt(start + 1))));
    }
  }
}
