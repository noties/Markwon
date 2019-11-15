package io.noties.markwon.sample.customextension2;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.commonmark.node.Link;
import org.commonmark.node.Node;
import org.commonmark.parser.InlineParserFactory;
import org.commonmark.parser.Parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.MarkwonConfiguration;
import io.noties.markwon.MarkwonVisitor;
import io.noties.markwon.RenderProps;
import io.noties.markwon.SpannableBuilder;
import io.noties.markwon.core.CorePlugin;
import io.noties.markwon.core.CoreProps;
import io.noties.markwon.inlineparser.InlineProcessor;
import io.noties.markwon.inlineparser.MarkwonInlineParser;
import io.noties.markwon.sample.R;

public class CustomExtensionActivity2 extends Activity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_view);

        final TextView textView = findViewById(R.id.text_view);

        // let's look for github special links:
        // * `#1` - an issue or a pull request
        // * `@user` link to a user


        final String md = "# Custom Extension 2\n" +
                "\n" +
                "This is an issue #1\n" +
                "Done by @noties";


//        inline_parsing(textView, md);

        text_added(textView, md);
    }

    private void text_added(@NonNull TextView textView, @NonNull String md) {

        final Markwon markwon = Markwon.builder(this)
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

    private void inline_parsing(@NonNull TextView textView, @NonNull String md) {

        final InlineParserFactory inlineParserFactory = MarkwonInlineParser.factoryBuilder()
                // include all current defaults (otherwise will be empty - contain only our inline-processors)
                //  included by default, to create factory-builder without defaults call `factoryBuilderNoDefaults`
//                .includeDefaults()
                .addInlineProcessor(new IssueInlineProcessor())
                .addInlineProcessor(new UserInlineProcessor())
                .build();

        final Markwon markwon = Markwon.builder(this)
                .usePlugin(new AbstractMarkwonPlugin() {
                    @Override
                    public void configureParser(@NonNull Parser.Builder builder) {
                        builder.inlineParserFactory(inlineParserFactory);
                    }
                })
                .build();

        markwon.setMarkdown(textView, md);
    }

    private static class IssueInlineProcessor extends InlineProcessor {

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
            return "https://github.com/noties/Markwon/issues/" + id;
        }
    }

    private static class UserInlineProcessor extends InlineProcessor {

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

    private static class GithubLinkifyRegexTextAddedListener implements CorePlugin.OnTextAddedListener {

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
            return "https://github.com/noties/Markwon/issues/" + number;
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
}
