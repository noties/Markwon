package io.noties.markwon.app.samples.editor;

import java.util.concurrent.Executors;

import io.noties.markwon.Markwon;
import io.noties.markwon.app.samples.editor.shared.MarkwonEditTextSample;
import io.noties.markwon.editor.MarkwonEditor;
import io.noties.markwon.editor.MarkwonEditorTextWatcher;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;
import io.noties.markwon.sample.annotations.Tag;

@MarkwonSampleInfo(
  id = "20200629164422",
  title = "Editor with pre-render (async)",
  description = "Editor functionality with highlight " +
    "taking place in another thread",
  artifacts = {MarkwonArtifact.EDITOR, MarkwonArtifact.INLINE_PARSER},
  tags = {Tag.editor}
)
public class EditorPreRenderSample extends MarkwonEditTextSample {
  @Override
  public void render() {
    // Process highlight in background thread

    final Markwon markwon = Markwon.create(context);
    final MarkwonEditor editor = MarkwonEditor.create(markwon);

    editText.addTextChangedListener(MarkwonEditorTextWatcher.withPreRender(
      editor,
      Executors.newCachedThreadPool(),
      editText));
  }
}
