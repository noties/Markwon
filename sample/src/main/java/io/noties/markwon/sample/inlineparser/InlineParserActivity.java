package io.noties.markwon.sample.inlineparser;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.text.Layout;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.commonmark.internal.inline.AsteriskDelimiterProcessor;
import org.commonmark.internal.inline.UnderscoreDelimiterProcessor;
import org.commonmark.node.Block;
import org.commonmark.node.BlockQuote;
import org.commonmark.node.CustomNode;
import org.commonmark.node.FencedCodeBlock;
import org.commonmark.node.Heading;
import org.commonmark.node.HtmlBlock;
import org.commonmark.node.IndentedCodeBlock;
import org.commonmark.node.ListBlock;
import org.commonmark.node.Node;
import org.commonmark.node.ThematicBreak;
import org.commonmark.parser.InlineParserFactory;
import org.commonmark.parser.Parser;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.MarkwonVisitor;
import io.noties.markwon.inlineparser.BackticksInlineProcessor;
import io.noties.markwon.inlineparser.BangInlineProcessor;
import io.noties.markwon.inlineparser.CloseBracketInlineProcessor;
import io.noties.markwon.inlineparser.HtmlInlineProcessor;
import io.noties.markwon.inlineparser.InlineProcessor;
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
                .add("disableHtmlSanitize", this::disableHtmlSanitize)
                .add("tooltip", this::tooltip);
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

    private void tooltip() {
        // NB! tooltip contents cannot have new lines
        final String md = "" +
                "\n" +
                "\n" +
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi vitae enim ut sem aliquet ultrices. Nunc a accumsan orci. Suspendisse tortor ante, lacinia ac scelerisque sed, dictum eget metus. Morbi ante augue, tristique eget quam in, vestibulum rutrum lacus. Nulla aliquam auctor cursus. Nulla at lacus condimentum, viverra lacus eget, sollicitudin ex. Cras efficitur leo dui, sit amet rutrum tellus venenatis et. Sed in facilisis libero. Etiam ultricies, nulla ut venenatis tincidunt, tortor erat tristique ante, non aliquet massa arcu eget nisl. Etiam gravida erat ante, sit amet lobortis mauris commodo nec. Praesent vitae sodales quam. Vivamus condimentum porta suscipit. Donec posuere id felis ac scelerisque. Vestibulum lacinia et leo id lobortis. Sed vitae dolor nec ligula dapibus finibus vel eu libero. Nam tincidunt maximus elit, sit amet tincidunt lacus laoreet malesuada.\n" +
                "\n" +
                "Aenean at urna leo. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Nulla facilisi. Integer lectus elit, congue a orci sed, dignissim sagittis sem. Aenean et pretium magna, nec ornare justo. Sed quis nunc blandit, luctus justo eget, pellentesque arcu. Pellentesque porta semper tristique. Donec et odio arcu. Nullam ultrices gravida congue. Praesent vel leo sed orci tempor luctus. Vivamus eget tortor arcu. Nullam sapien nulla, iaculis sit amet semper in, mattis nec metus. In porttitor augue id elit euismod mattis. Ut est justo, dapibus suscipit erat eu, pellentesque porttitor magna.\n" +
                "\n" +
                "Nunc porta orci eget dictum malesuada. Donec vehicula felis sit amet leo tincidunt placerat. Cras quis elit faucibus, porta elit at, sodales tortor. Donec elit mi, eleifend et maximus vitae, pretium varius velit. Integer maximus egestas urna, at semper augue egestas vitae. Phasellus arcu tellus, tincidunt eget tellus nec, hendrerit mollis mauris. Pellentesque commodo urna quis nisi ultrices, quis vehicula felis ultricies. Vivamus eu feugiat leo.\n" +
                "\n" +
                "Etiam sit amet lorem et eros suscipit rhoncus a a tellus. Sed pharetra dui purus, quis molestie leo congue nec. Suspendisse sed scelerisque quam. Vestibulum non laoreet felis. Fusce interdum euismod purus at scelerisque. Vivamus tempus varius nibh, sed accumsan nisl interdum non. Pellentesque rutrum egestas eros sit amet sollicitudin. Vivamus ultrices est erat. Curabitur gravida justo non felis euismod mollis. Ut porta finibus nulla, sed pellentesque purus euismod ac.\n" +
                "\n" +
                "Aliquam erat volutpat. Nullam suscipit sit amet tortor vel fringilla. Nulla facilisi. Nullam lacinia ex lacus, sit amet scelerisque justo semper a. Nullam ullamcorper, erat ac malesuada porta, augue erat sagittis mi, in auctor turpis mauris nec orci. Nunc sit amet felis placerat, pharetra diam nec, dapibus metus. Proin nulla orci, iaculis vitae vulputate vel, placerat ac erat. Morbi sit amet blandit velit. Cras consectetur vehicula lacus vel sagittis. Nunc tincidunt lacus in blandit faucibus. Curabitur vestibulum auctor vehicula. Sed quis ligula sit amet quam venenatis venenatis eget id felis. Maecenas feugiat nisl elit, facilisis tempus risus malesuada quis. " +
                "# Hello tooltip!\n\n" +
                "This is the !{tooltip label}(and actual content comes here)\n\n" +
                "what if it is !{here}(The contents can be blocks, limited though) instead?\n\n" +
                "![image](#) anyway";

        final Markwon markwon = Markwon.builder(this)
                .usePlugin(MarkwonInlineParserPlugin.create(factoryBuilder ->
                        factoryBuilder.addInlineProcessor(new TooltipInlineProcessor())))
                .usePlugin(new AbstractMarkwonPlugin() {
                    @Override
                    public void configureVisitor(@NonNull MarkwonVisitor.Builder builder) {
                        builder.on(TooltipNode.class, (visitor, tooltipNode) -> {
                            final int start = visitor.length();
                            visitor.builder().append(tooltipNode.label);
                            visitor.setSpans(start, new TooltipSpan(tooltipNode.contents));
                        });
                    }
                })
                .build();

        markwon.setMarkdown(textView, md);
    }

    private static class TooltipInlineProcessor extends InlineProcessor {

        // NB! without bang
        // `\\{` is required (although marked as redundant), without it - runtime crash
        @SuppressWarnings("RegExpRedundantEscape")
        private static final Pattern RE = Pattern.compile("\\{(.+?)\\}\\((.+?)\\)");

        @Override
        public char specialCharacter() {
            return '!';
        }

        @Nullable
        @Override
        protected Node parse() {
            final String match = match(RE);
            if (match == null) {
                return null;
            }

            final Matcher matcher = RE.matcher(match);
            if (matcher.matches()) {
                final String label = matcher.group(1);
                final String contents = matcher.group(2);
                return new TooltipNode(label, contents);
            }

            return null;
        }
    }

    private static class TooltipNode extends CustomNode {
        final String label;
        final String contents;

        TooltipNode(@NonNull String label, @NonNull String contents) {
            this.label = label;
            this.contents = contents;
        }
    }

    private static class TooltipSpan extends ClickableSpan {
        final String contents;

        TooltipSpan(@NonNull String contents) {
            this.contents = contents;
        }

        @Override
        public void onClick(@NonNull View widget) {
            // just to be safe
            if (!(widget instanceof TextView)) {
                return;
            }

            final TextView textView = (TextView) widget;
            final Spannable spannable = (Spannable) textView.getText();

            // find self ending position (can also obtain start)
//            final int start = spannable.getSpanStart(this);
            final int end = spannable.getSpanEnd(this);

            // weird, didn't find self
            if (/*start < 0 ||*/ end < 0) {
                return;
            }

            final Layout layout = textView.getLayout();
            if (layout == null) {
                // also weird
                return;
            }

            final int line = layout.getLineForOffset(end);

            // position inside TextView, these values must also be adjusted to parent widget
            // also note that container can
            final int y = layout.getLineBottom(line);
            final int x = (int) (layout.getPrimaryHorizontal(end) + 0.5F);

            final Window window = ((Activity) widget.getContext()).getWindow();
            final View decor = window.getDecorView();
            final Point point = relativeTo(decor, widget);

//            new Tooltip.Builder(widget.getContext())
//                    .anchor(x + point.x, y + point.y)
//                    .text(contents)
//                    .create()
//                    .show(widget, Tooltip.Gravity.TOP, false);

            // Toast is not reliable when tried to position on the screen
            //  but anyway, this is to showcase only
            final Toast toast = Toast.makeText(widget.getContext(), contents, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP | Gravity.START, x + point.x, y + point.y);
            toast.show();
        }

        @Override
        public void updateDrawState(@NonNull TextPaint ds) {
            // can customize appearance here as spans will be rendered as links
            super.updateDrawState(ds);
        }

        @NonNull
        private static Point relativeTo(@NonNull View parent, @NonNull View who) {
            return relativeTo(parent, who, new Point());
        }

        @NonNull
        private static Point relativeTo(@NonNull View parent, @NonNull View who, @NonNull Point point) {
            // NB! the scroll adjustments (we are interested in screen position,
            //  not real position inside parent)
            point.x += who.getLeft();
            point.y += who.getTop();
            point.x -= who.getScrollX();
            point.y -= who.getScrollY();
            if (who != parent
                    && who.getParent() instanceof View) {
                relativeTo(parent, (View) who.getParent(), point);
            }
            return point;
        }
    }
}
