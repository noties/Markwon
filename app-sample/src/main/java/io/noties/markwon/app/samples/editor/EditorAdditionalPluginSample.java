package io.noties.markwon.app.samples.editor;

import io.noties.markwon.Markwon;
import io.noties.markwon.app.samples.editor.shared.MarkwonEditTextSample;
import io.noties.markwon.editor.MarkwonEditor;
import io.noties.markwon.editor.MarkwonEditorTextWatcher;
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;
import io.noties.markwon.sample.annotations.Tag;

@MarkwonSampleInfo(
  id = "20200629165347",
  title = "Additional plugin",
  description = "Additional plugin for editor",
  artifacts = {MarkwonArtifact.EDITOR, MarkwonArtifact.INLINE_PARSER, MarkwonArtifact.EXT_STRIKETHROUGH},
  tags = {Tag.editor}
)
public class EditorAdditionalPluginSample extends MarkwonEditTextSample {
  @Override
  public void render() {
    // As highlight works based on text-diff, everything that is present in input,
    // but missing in resulting markdown is considered to be punctuation, this is why
    // additional plugins do not need special handling

    final Markwon markwon = Markwon.builder(context)
      .usePlugin(StrikethroughPlugin.create())
      .build();

    final MarkwonEditor editor = MarkwonEditor.create(markwon);

    editText.addTextChangedListener(MarkwonEditorTextWatcher.withProcess(editor));
  }
}
