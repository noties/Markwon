package io.noties.markwon.app.samples.editor;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

import androidx.annotation.NonNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.noties.debug.Debug;
import io.noties.markwon.Markwon;
import io.noties.markwon.app.sample.Tags;
import io.noties.markwon.app.samples.editor.shared.MarkwonEditTextSample;
import io.noties.markwon.editor.MarkwonEditor;
import io.noties.markwon.editor.MarkwonEditorTextWatcher;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;

@MarkwonSampleInfo(
  id = "202006181170348",
  title = "Editor new line continuation",
  description = "Sample of how new line character can be handled " +
    "in order to add a _continuation_, for example adding a new " +
    "bullet list item if current line starts with one",
  artifacts = {MarkwonArtifact.EDITOR, MarkwonArtifact.INLINE_PARSER},
  tags = {Tags.editor}
)
public class EditorNewLineContinuationSample extends MarkwonEditTextSample {
  @Override
  public void render() {
    final Markwon markwon = Markwon.create(context);
    final MarkwonEditor editor = MarkwonEditor.create(markwon);

    final TextWatcher textWatcher = MarkdownNewLine
      .wrap(MarkwonEditorTextWatcher.withProcess(editor));

    editText.addTextChangedListener(textWatcher);
  }
}

class MarkdownNewLine {

  @NonNull
  static TextWatcher wrap(@NonNull TextWatcher textWatcher) {
    return new NewLineTextWatcher(textWatcher);
  }

  private MarkdownNewLine() {
  }

  private static class NewLineTextWatcher implements TextWatcher {

    // NB! matches only bullet lists
    private final Pattern RE = Pattern.compile("^( {0,3}[\\-+* ]+)(.+)*$");

    private final TextWatcher wrapped;

    private boolean selfChange;

    // this content is pending to be inserted at the beginning
    private String pendingNewLineContent;
    private int pendingNewLineIndex;

    // mark current edited line for removal (range start/end)
    private int clearLineStart;
    private int clearLineEnd;

    NewLineTextWatcher(@NonNull TextWatcher wrapped) {
      this.wrapped = wrapped;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      // no op
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
      if (selfChange) {
        return;
      }

      // just one new character added
      if (before == 0
        && count == 1
        && '\n' == s.charAt(start)) {
        int end = -1;
        for (int i = start - 1; i >= 0; i--) {
          if ('\n' == s.charAt(i)) {
            end = i + 1;
            break;
          }
        }

        // start at the very beginning
        if (end < 0) {
          end = 0;
        }

        final String pendingNewLineContent;

        final int clearLineStart;
        final int clearLineEnd;

        final Matcher matcher = RE.matcher(s.subSequence(end, start));
        if (matcher.matches()) {
          // if second group is empty -> remove new line
          final String content = matcher.group(2);
          Debug.e("new line, content: '%s'", content);
          if (TextUtils.isEmpty(content)) {
            // another empty new line, remove this start
            clearLineStart = end;
            clearLineEnd = start;
            pendingNewLineContent = null;
          } else {
            pendingNewLineContent = matcher.group(1);
            clearLineStart = clearLineEnd = 0;
          }
        } else {
          pendingNewLineContent = null;
          clearLineStart = clearLineEnd = 0;
        }
        this.pendingNewLineContent = pendingNewLineContent;
        this.pendingNewLineIndex = start + 1;
        this.clearLineStart = clearLineStart;
        this.clearLineEnd = clearLineEnd;
      }
    }

    @Override
    public void afterTextChanged(Editable s) {
      if (selfChange) {
        return;
      }

      if (pendingNewLineContent != null || clearLineStart < clearLineEnd) {
        selfChange = true;
        try {
          if (pendingNewLineContent != null) {
            s.insert(pendingNewLineIndex, pendingNewLineContent);
            pendingNewLineContent = null;
          } else {
            s.replace(clearLineStart, clearLineEnd, "");
            clearLineStart = clearLineEnd = 0;
          }
        } finally {
          selfChange = false;
        }
      }

      // NB, we assume MarkdownEditor text watcher that only listens for this event,
      // other text-watchers must be interested in other events also
      wrapped.afterTextChanged(s);
    }
  }
}
