package io.noties.markwon.app.samples.editor;

import java.util.concurrent.Executors;

import io.noties.markwon.Markwon;
import io.noties.markwon.app.samples.editor.shared.HeadingEditHandler;
import io.noties.markwon.app.samples.editor.shared.MarkwonEditTextSample;
import io.noties.markwon.editor.MarkwonEditor;
import io.noties.markwon.editor.MarkwonEditorTextWatcher;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;
import io.noties.markwon.sample.annotations.Tag;

@MarkwonSampleInfo(
  id = "20200630113954",
  title = "Heading edit handler",
  description = "Handling of heading node in editor",
  artifacts = {MarkwonArtifact.EDITOR, MarkwonArtifact.INLINE_PARSER},
  tags = {Tag.editor}
)
public class EditorHeadingSample extends MarkwonEditTextSample {
  @Override
  public void render() {
    final Markwon markwon = Markwon.create(context);
    final MarkwonEditor editor = MarkwonEditor.builder(markwon)
      .useEditHandler(new HeadingEditHandler())
      .build();

    editText.addTextChangedListener(MarkwonEditorTextWatcher.withPreRender(
      editor, Executors.newSingleThreadExecutor(), editText));
  }
}
