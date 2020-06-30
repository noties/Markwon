package io.noties.markwon.app.samples.plugins;

import android.view.View;
import android.widget.ScrollView;

import org.jetbrains.annotations.NotNull;

import io.noties.markwon.Markwon;
import io.noties.markwon.app.R;
import io.noties.markwon.app.sample.Tags;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.app.samples.plugins.shared.AnchorHeadingPlugin;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;

@MarkwonSampleInfo(
  id = "202006181130728",
  title = "Anchor plugin",
  description = "HTML-like anchor links plugin, which scrolls to clicked anchor",
  artifacts = MarkwonArtifact.CORE,
  tags = {Tags.links, Tags.anchor, Tags.plugin}
)
public class AnchorSample extends MarkwonTextViewSample {

  private ScrollView scrollView;

  @Override
  public void onViewCreated(@NotNull View view) {
    scrollView = view.findViewById(R.id.scroll_view);
    super.onViewCreated(view);
  }

  @Override
  public void render() {

    final String lorem = context.getString(R.string.lorem);
    final String md = "" +
      "Hello [there](#there)!\n\n\n" +
      lorem + "\n\n" +
      "# There!\n\n" +
      lorem;

    final Markwon markwon = Markwon.builder(context)
      .usePlugin(new AnchorHeadingPlugin((view, top) -> scrollView.smoothScrollTo(0, top)))
      .build();

    markwon.setMarkdown(textView, md);
  }
}

