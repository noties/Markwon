package io.noties.markwon.app.samples.editor;

import android.text.style.ForegroundColorSpan;

import io.noties.markwon.Markwon;
import io.noties.markwon.app.sample.Tags;
import io.noties.markwon.app.sample.ui.MarkwonEditTextSample;
import io.noties.markwon.editor.MarkwonEditor;
import io.noties.markwon.editor.MarkwonEditorTextWatcher;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;

@MarkwonSampleInfo(
  id = "202006181164627",
  title = "Custom punctuation span",
  description = "Custom span for punctuation in editor",
  artifacts = {MarkwonArtifact.EDITOR, MarkwonArtifact.INLINE_PARSER},
  tags = {Tags.editor, Tags.span}
)
public class EditorCustomPunctuationSample extends MarkwonEditTextSample {
  @Override
  public void render() {
    // Use own punctuation span

    final MarkwonEditor editor = MarkwonEditor.builder(Markwon.create(context))
      .punctuationSpan(CustomPunctuationSpan.class, CustomPunctuationSpan::new)
      .build();

    editText.addTextChangedListener(MarkwonEditorTextWatcher.withProcess(editor));
  }
}

class CustomPunctuationSpan extends ForegroundColorSpan {
  CustomPunctuationSpan() {
    super(0xFFFF0000); // RED
  }
}
