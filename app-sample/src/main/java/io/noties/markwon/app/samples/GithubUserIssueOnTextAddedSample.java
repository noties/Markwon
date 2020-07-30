package io.noties.markwon.app.samples;

import androidx.annotation.NonNull;

import org.commonmark.node.Link;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.MarkwonConfiguration;
import io.noties.markwon.MarkwonVisitor;
import io.noties.markwon.RenderProps;
import io.noties.markwon.SpannableBuilder;
import io.noties.markwon.app.BuildConfig;
import io.noties.markwon.app.sample.Tags;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.core.CorePlugin;
import io.noties.markwon.core.CoreProps;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;

@MarkwonSampleInfo(
  id = "20200629162024",
  title = "User mention and issue (via text)",
  description = "Github-like user mention and issue " +
    "rendering via `CorePlugin.OnTextAddedListener`",
  artifacts = MarkwonArtifact.CORE,
  tags = {Tags.parsing, Tags.textAddedListener, Tags.rendering}
)
public class GithubUserIssueOnTextAddedSample extends MarkwonTextViewSample {
  @Override
  public void render() {
    final String md = "" +
      "# Custom Extension 2\n" +
      "\n" +
      "This is an issue #1\n" +
      "Done by @noties";

    final Markwon markwon = Markwon.builder(context)
      .usePlugin(new AbstractMarkwonPlugin() {
        @Override
        public void configure(@NonNull Registry registry) {
          registry.require(CorePlugin.class, corePlugin ->
            corePlugin.addOnTextAddedListener(new GithubLinkifyRegexTextAddedListener()));
        }
      })
      .build();

    markwon.setMarkdown(textView, md);
  }
}

class GithubLinkifyRegexTextAddedListener implements CorePlugin.OnTextAddedListener {

  private static final Pattern PATTERN = Pattern.compile("((#\\d+)|(@\\w+))", Pattern.MULTILINE);

  @Override
  public void onTextAdded(@NonNull MarkwonVisitor visitor, @NonNull String text, int start) {

    final Matcher matcher = PATTERN.matcher(text);

    String value;
    String url;
    int index;

    while (matcher.find()) {

      value = matcher.group(1);

      // detect which one it is
      if ('#' == value.charAt(0)) {
        url = createIssueOrPullRequestLink(value.substring(1));
      } else {
        url = createUserLink(value.substring(1));
      }

      // it's important to use `start` value (represents start-index of `text` in the visitor)
      index = start + matcher.start();

      setLink(visitor, url, index, index + value.length());
    }
  }

  @NonNull
  private String createIssueOrPullRequestLink(@NonNull String number) {
    // issues and pull-requests on github follow the same pattern and we
    // cannot know for sure which one it is, but if we use issues for all types,
    // github will automatically redirect to pull-request if it's the one which is opened
    return BuildConfig.GIT_REPOSITORY + "/issues/" + number;
  }

  @NonNull
  private String createUserLink(@NonNull String user) {
    return "https://github.com/" + user;
  }

  private void setLink(@NonNull MarkwonVisitor visitor, @NonNull String destination, int start, int end) {

    // might a simpler one, but it doesn't respect possible changes to links
//            visitor.builder().setSpan(
//                    new LinkSpan(visitor.configuration().theme(), destination, visitor.configuration().linkResolver()),
//                    start,
//                    end
//            );

    // use default handlers for links
    final MarkwonConfiguration configuration = visitor.configuration();
    final RenderProps renderProps = visitor.renderProps();

    CoreProps.LINK_DESTINATION.set(renderProps, destination);

    SpannableBuilder.setSpans(
      visitor.builder(),
      configuration.spansFactory().require(Link.class).getSpans(configuration, renderProps),
      start,
      end
    );
  }
}
