package io.noties.markwon.app.samples;

import android.text.Spanned;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.commonmark.node.Link;

import java.util.Locale;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.LinkResolver;
import io.noties.markwon.Markwon;
import io.noties.markwon.MarkwonSpansFactory;
import io.noties.markwon.app.sample.Tags;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.core.CoreProps;
import io.noties.markwon.core.MarkwonTheme;
import io.noties.markwon.core.spans.LinkSpan;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;

@MarkwonSampleInfo(
  id = "202006181122230",
  title = "Obtain link title",
  description = "Obtain title (text) of clicked link, `[title](#destination)`",
  artifacts = {MarkwonArtifact.CORE},
  tags = {Tags.links, Tags.span}
)
public class LinkTitleSample extends MarkwonTextViewSample {
  @Override
  public void render() {
    final String md = "" +
      "# Links\n\n" +
      "[link title](#)";

    final Markwon markwon = Markwon.builder(context)
      .usePlugin(new AbstractMarkwonPlugin() {
        @Override
        public void configureSpansFactory(@NonNull MarkwonSpansFactory.Builder builder) {
          builder.setFactory(Link.class, (configuration, props) ->
            // create a subclass of markwon LinkSpan
            new ClickSelfSpan(
              configuration.theme(),
              CoreProps.LINK_DESTINATION.require(props),
              configuration.linkResolver()
            )
          );
        }
      })
      .build();

    markwon.setMarkdown(textView, md);
  }
}

class ClickSelfSpan extends LinkSpan {

  ClickSelfSpan(
    @NonNull MarkwonTheme theme,
    @NonNull String link,
    @NonNull LinkResolver resolver) {
    super(theme, link, resolver);
  }

  @Override
  public void onClick(View widget) {
    Toast.makeText(
      widget.getContext(),
      String.format(Locale.ROOT, "clicked link title: '%s'", linkTitle(widget)),
      Toast.LENGTH_LONG
    ).show();
    super.onClick(widget);
  }

  @Nullable
  private CharSequence linkTitle(@NonNull View widget) {

    if (!(widget instanceof TextView)) {
      return null;
    }

    final Spanned spanned = (Spanned) ((TextView) widget).getText();
    final int start = spanned.getSpanStart(this);
    final int end = spanned.getSpanEnd(this);

    if (start < 0 || end < 0) {
      return null;
    }

    return spanned.subSequence(start, end);
  }
}
