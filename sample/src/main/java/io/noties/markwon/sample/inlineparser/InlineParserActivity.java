package io.noties.markwon.sample.inlineparser;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.commonmark.internal.inline.AsteriskDelimiterProcessor;
import org.commonmark.internal.inline.UnderscoreDelimiterProcessor;
import org.commonmark.node.Block;
import org.commonmark.node.BlockQuote;
import org.commonmark.node.FencedCodeBlock;
import org.commonmark.node.Heading;
import org.commonmark.node.HtmlBlock;
import org.commonmark.node.IndentedCodeBlock;
import org.commonmark.node.ListBlock;
import org.commonmark.node.ThematicBreak;
import org.commonmark.parser.InlineParserFactory;
import org.commonmark.parser.Parser;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.inlineparser.BackticksInlineProcessor;
import io.noties.markwon.inlineparser.BangInlineProcessor;
import io.noties.markwon.inlineparser.CloseBracketInlineProcessor;
import io.noties.markwon.inlineparser.HtmlInlineProcessor;
import io.noties.markwon.inlineparser.MarkwonInlineParser;
import io.noties.markwon.inlineparser.MarkwonInlineParserPlugin;
import io.noties.markwon.inlineparser.OpenBracketInlineProcessor;
import io.noties.markwon.sample.ActivityWithMenuOptions;
import io.noties.markwon.sample.MenuOptions;
import io.noties.markwon.sample.R;

public class InlineParserActivity extends ActivityWithMenuOptions {

    private TextView textView;

    @NonNull
    @Override
    public MenuOptions menuOptions() {
        return MenuOptions.create()
                .add("links_only", this::links_only)
                .add("disable_code", this::disable_code)
                .add("pluginWithDefaults", this::pluginWithDefaults)
                .add("pluginNoDefaults", this::pluginNoDefaults)
                .add("disableHtmlInlineParser", this::disableHtmlInlineParser)
                .add("disableHtmlSanitize", this::disableHtmlSanitize);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_view);

        textView = findViewById(R.id.text_view);

//        links_only();

        disable_code();
    }

    private void links_only() {

        // create an inline-parser-factory that will _ONLY_ parse links
        //  this would mean:
        //  * no emphasises (strong and regular aka bold and italics),
        //  * no images,
        //  * no code,
        //  * no HTML entities (&amp;)
        //  * no HTML tags
        // markdown blocks are still parsed
        final InlineParserFactory inlineParserFactory = MarkwonInlineParser.factoryBuilderNoDefaults()
                .referencesEnabled(true)
                .addInlineProcessor(new OpenBracketInlineProcessor())
                .addInlineProcessor(new CloseBracketInlineProcessor())
                .build();

        final Markwon markwon = Markwon.builder(this)
                .usePlugin(new AbstractMarkwonPlugin() {
                    @Override
                    public void configureParser(@NonNull Parser.Builder builder) {
                        builder.inlineParserFactory(inlineParserFactory);
                    }
                })
                .build();

        // note that image is considered a link now
        final String md = "**bold_bold-italic_** <u>html-u</u>, [link](#) ![alt](#image) `code`";
        markwon.setMarkdown(textView, md);
    }

    private void disable_code() {
        // parses all as usual, but ignores code (inline and block)

        final InlineParserFactory inlineParserFactory = MarkwonInlineParser.factoryBuilder()
                .excludeInlineProcessor(BackticksInlineProcessor.class)
                .build();

        // unfortunately there is no _exclude_ method for parser-builder
        final Set<Class<? extends Block>> enabledBlocks = new HashSet<Class<? extends Block>>() {{
            // IndentedCodeBlock.class and FencedCodeBlock.class are missing
            // this is full list (including above) that can be passed to `enabledBlockTypes` method
            addAll(Arrays.asList(
                    BlockQuote.class,
                    Heading.class,
                    HtmlBlock.class,
                    ThematicBreak.class,
                    ListBlock.class));
        }};

        final Markwon markwon = Markwon.builder(this)
                .usePlugin(new AbstractMarkwonPlugin() {
                    @Override
                    public void configureParser(@NonNull Parser.Builder builder) {
                        builder
                                .inlineParserFactory(inlineParserFactory)
                                .enabledBlockTypes(enabledBlocks);
                    }
                })
                .build();

        final String md = "# Head!\n\n" +
                "* one\n" +
                "+ two\n\n" +
                "and **bold** to `you`!\n\n" +
                "> a quote _em_\n\n" +
                "```java\n" +
                "final int i = 0;\n" +
                "```\n\n" +
                "**Good day!**";
        markwon.setMarkdown(textView, md);
    }

    private void pluginWithDefaults() {
        // a plugin with defaults registered

        final String md = "no [links](#) for **you** `code`!";

        final Markwon markwon = Markwon.builder(this)
                .usePlugin(MarkwonInlineParserPlugin.create())
                // the same as:
//                .usePlugin(MarkwonInlineParserPlugin.create(MarkwonInlineParser.factoryBuilder()))
                .usePlugin(new AbstractMarkwonPlugin() {
                    @Override
                    public void configure(@NonNull Registry registry) {
                        registry.require(MarkwonInlineParserPlugin.class, plugin -> {
                            plugin.factoryBuilder()
                                    .excludeInlineProcessor(OpenBracketInlineProcessor.class);
                        });
                    }
                })
                .build();

        markwon.setMarkdown(textView, md);
    }

    private void pluginNoDefaults() {
        // a plugin with NO defaults registered

        final String md = "no [links](#) for **you** `code`!";

        final Markwon markwon = Markwon.builder(this)
                // pass `MarkwonInlineParser.factoryBuilderNoDefaults()` no disable all
                .usePlugin(MarkwonInlineParserPlugin.create(MarkwonInlineParser.factoryBuilderNoDefaults()))
                .usePlugin(new AbstractMarkwonPlugin() {
                    @Override
                    public void configure(@NonNull Registry registry) {
                        registry.require(MarkwonInlineParserPlugin.class, plugin -> {
                            plugin.factoryBuilder()
                                    .addInlineProcessor(new BackticksInlineProcessor());
                        });
                    }
                })
                .build();

        markwon.setMarkdown(textView, md);
    }

    private void disableHtmlInlineParser() {
        final String md = "# Html <b>disabled</b>\n\n" +
                "<em>emphasis <strong>strong</strong>\n\n" +
                "<p>paragraph <img src='hey.jpg' /></p>\n\n" +
                "<test></test>\n\n" +
                "<test>";

        final Markwon markwon = Markwon.builder(this)
                .usePlugin(MarkwonInlineParserPlugin.create())
                .usePlugin(new AbstractMarkwonPlugin() {
                    @Override
                    public void configure(@NonNull Registry registry) {
                        // NB! `AsteriskDelimiterProcessor` and `UnderscoreDelimiterProcessor`
                        //  handles both emphasis and strong-emphasis nodes
                        registry.require(MarkwonInlineParserPlugin.class, plugin -> {
                            plugin.factoryBuilder()
                                    .excludeInlineProcessor(HtmlInlineProcessor.class)
                                    .excludeInlineProcessor(BangInlineProcessor.class)
                                    .excludeInlineProcessor(OpenBracketInlineProcessor.class)
                                    .excludeDelimiterProcessor(AsteriskDelimiterProcessor.class)
                                    .excludeDelimiterProcessor(UnderscoreDelimiterProcessor.class);
                        });
                    }

                    @Override
                    public void configureParser(@NonNull Parser.Builder builder) {
                        builder.enabledBlockTypes(new HashSet<>(Arrays.asList(
                                Heading.class,
//                        HtmlBlock.class,
                                ThematicBreak.class,
                                FencedCodeBlock.class,
                                IndentedCodeBlock.class,
                                BlockQuote.class,
                                ListBlock.class
                        )));
                    }
                })
                .build();

        markwon.setMarkdown(textView, md);
    }

    private void disableHtmlSanitize() {
        final String md = "# Html <b>disabled</b>\n\n" +
                "<em>emphasis <strong>strong</strong>\n\n" +
                "<p>paragraph <img src='hey.jpg' /></p>\n\n" +
                "<test></test>\n\n" +
                "<test>";

        final Markwon markwon = Markwon.builder(this)
                .usePlugin(new AbstractMarkwonPlugin() {
                    @NonNull
                    @Override
                    public String processMarkdown(@NonNull String markdown) {
                        return markdown
                                .replaceAll("<", "&lt;")
                                .replaceAll(">", "&gt;");
                    }
                })
                .build();

        markwon.setMarkdown(textView, md);
    }
}
