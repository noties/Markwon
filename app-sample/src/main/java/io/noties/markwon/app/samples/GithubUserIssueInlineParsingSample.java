package io.noties.markwon.app.samples;

import androidx.annotation.NonNull;

import org.commonmark.node.Link;
import org.commonmark.node.Node;
import org.commonmark.parser.InlineParserFactory;
import org.commonmark.parser.Parser;

import java.util.regex.Pattern;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.app.BuildConfig;
import io.noties.markwon.app.sample.Tags;
import io.noties.markwon.app.sample.ui.MarkwonTextViewSample;
import io.noties.markwon.inlineparser.InlineProcessor;
import io.noties.markwon.inlineparser.MarkwonInlineParser;
import io.noties.markwon.sample.annotations.MarkwonArtifact;
import io.noties.markwon.sample.annotations.MarkwonSampleInfo;

@MarkwonSampleInfo(
  id = "20200629162023",
  title = "User mention and issue (via text)",
  description = "Github-like user mention and issue " +
    "rendering via `CorePlugin.OnTextAddedListener`",
  artifacts = {MarkwonArtifact.CORE, MarkwonArtifact.INLINE_PARSER},
  tags = {Tags.parsing, Tags.textAddedListener, Tags.rendering}
)
public class GithubUserIssueInlineParsingSample extends MarkwonTextViewSample {
  @Override
  public void render() {
    final String md = "" +
      "# Custom Extension 2\n" +
      "\n" +
      "This is an issue #1\n" +
      "Done by @noties";

    final InlineParserFactory inlineParserFactory = MarkwonInlineParser.factoryBuilder()
      // include all current defaults (otherwise will be empty - contain only our inline-processors)
      //  included by default, to create factory-builder without defaults call `factoryBuilderNoDefaults`
//                .includeDefaults()
      .addInlineProcessor(new IssueInlineProcessor())
      .addInlineProcessor(new UserInlineProcessor())
      .build();

    final Markwon markwon = Markwon.builder(context)
      .usePlugin(new AbstractMarkwonPlugin() {
        @Override
        public void configureParser(@NonNull Parser.Builder builder) {
          builder.inlineParserFactory(inlineParserFactory);
        }
      })
      .build();

    markwon.setMarkdown(textView, md);
  }
}

class IssueInlineProcessor extends InlineProcessor {

  private static final Pattern RE = Pattern.compile("\\d+");

  @Override
  public char specialCharacter() {
    return '#';
  }

  @Override
  protected Node parse() {
    final String id = match(RE);
    if (id != null) {
      final Link link = new Link(createIssueOrPullRequestLinkDestination(id), null);
      link.appendChild(text("#" + id));
      return link;
    }
    return null;
  }

  @NonNull
  private static String createIssueOrPullRequestLinkDestination(@NonNull String id) {
    return BuildConfig.GIT_REPOSITORY + "/issues/" + id;
  }
}

class UserInlineProcessor extends InlineProcessor {

  private static final Pattern RE = Pattern.compile("\\w+");

  @Override
  public char specialCharacter() {
    return '@';
  }

  @Override
  protected Node parse() {
    final String user = match(RE);
    if (user != null) {
      final Link link = new Link(createUserLinkDestination(user), null);
      link.appendChild(text("@" + user));
      return link;
    }
    return null;
  }

  @NonNull
  private static String createUserLinkDestination(@NonNull String user) {
    return "https://github.com/" + user;
  }
}