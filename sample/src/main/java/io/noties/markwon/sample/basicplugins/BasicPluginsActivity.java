package io.noties.markwon.sample.basicplugins;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.commonmark.node.Heading;
import org.commonmark.node.Node;
import org.commonmark.node.Paragraph;

import java.util.Collection;
import java.util.Collections;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.BlockHandlerDef;
import io.noties.markwon.Markwon;
import io.noties.markwon.MarkwonConfiguration;
import io.noties.markwon.MarkwonSpansFactory;
import io.noties.markwon.MarkwonVisitor;
import io.noties.markwon.SoftBreakAddsNewLinePlugin;
import io.noties.markwon.core.CoreProps;
import io.noties.markwon.core.MarkwonTheme;
import io.noties.markwon.core.spans.LastLineSpacingSpan;
import io.noties.markwon.image.ImageItem;
import io.noties.markwon.image.ImagesPlugin;
import io.noties.markwon.image.SchemeHandler;
import io.noties.markwon.image.network.NetworkSchemeHandler;
import io.noties.markwon.movement.MovementMethodPlugin;
import io.noties.markwon.sample.ActivityWithMenuOptions;
import io.noties.markwon.sample.MenuOptions;
import io.noties.markwon.sample.R;

public class BasicPluginsActivity extends ActivityWithMenuOptions {

    private TextView textView;
    private ScrollView scrollView;

    @NonNull
    @Override
    public MenuOptions menuOptions() {
        return MenuOptions.create()
                .add("paragraphSpan", this::paragraphSpan)
                .add("disableNode", this::disableNode)
                .add("customizeTheme", this::customizeTheme)
                .add("linkWithMovementMethod", this::linkWithMovementMethod)
                .add("imagesPlugin", this::imagesPlugin)
                .add("softBreakAddsSpace", this::softBreakAddsSpace)
                .add("softBreakAddsNewLine", this::softBreakAddsNewLine)
                .add("additionalSpacing", this::additionalSpacing)
                .add("headingNoSpace", this::headingNoSpace)
                .add("headingNoSpaceBlockHandler", this::headingNoSpaceBlockHandler)
                .add("allBlocksNoForcedLine", this::allBlocksNoForcedLine)
                .add("anchor", this::anchor)
                .add("letterOrderedList", this::letterOrderedList);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_view);

        textView = findViewById(R.id.text_view);
        scrollView = findViewById(R.id.scroll_view);

        paragraphSpan();
//
//        disableNode();
//
//        customizeTheme();
//
//        linkWithMovementMethod();
//
//        imagesPlugin();
    }

    /**
     * In order to apply paragraph spans a custom plugin should be created (CorePlugin will take care
     * of everything else).
     */
    private void paragraphSpan() {

        final String markdown = "# Hello!\n\nA paragraph?\n\nIt should be!";

        final Markwon markwon = Markwon.builder(this)
                .usePlugin(new AbstractMarkwonPlugin() {
                    @Override
                    public void configureSpansFactory(@NonNull MarkwonSpansFactory.Builder builder) {
                        builder.setFactory(Paragraph.class, (configuration, props) ->
                                new ForegroundColorSpan(Color.GREEN));
                    }
                })
                .build();

        markwon.setMarkdown(textView, markdown);
    }

    /**
     * To disable some nodes from rendering another custom plugin can be used
     */
    private void disableNode() {

        final String markdown = "# Heading 1\n\n## Heading 2\n\n**other** content [here](#)";

        final Markwon markwon = Markwon.builder(this)
                .usePlugin(new AbstractMarkwonPlugin() {
                    @Override
                    public void configureVisitor(@NonNull MarkwonVisitor.Builder builder) {

                        // for example to disable rendering of heading:
                        // try commenting this out to see that otherwise headings will be rendered
                        builder.on(Heading.class, null);

                        // same method can be used to override existing visitor by specifying
                        // a new NodeVisitor instance
                    }
                })
                .build();

        markwon.setMarkdown(textView, markdown);
    }

    /**
     * To customize core theme plugin can be used again
     */
    private void customizeTheme() {

        final String markdown = "`A code` that is rendered differently\n\n```\nHello!\n```";

        final Markwon markwon = Markwon.builder(this)
                .usePlugin(new AbstractMarkwonPlugin() {
                    @Override
                    public void configureTheme(@NonNull MarkwonTheme.Builder builder) {
                        builder
                                .codeBackgroundColor(Color.BLACK)
                                .codeTextColor(Color.RED);
                    }
                })
                .build();

        markwon.setMarkdown(textView, markdown);
    }

    /**
     * MarkwonConfiguration contains these <em>utilities</em>:
     * <ul>
     * <li>SyntaxHighlight</li>
     * <li>LinkSpan.Resolver</li>
     * <li>UrlProcessor</li>
     * <li>ImageSizeResolver</li>
     * </ul>
     * <p>
     * In order to customize them a custom plugin should be used
     */
    private void linkWithMovementMethod() {

        final String markdown = "[a link without scheme](github.com)";

        final Markwon markwon = Markwon.builder(this)
                // please note that Markwon does not handle MovementMethod,
                // so if your markdown has links your should apply MovementMethod manually
                // or use MovementMethodPlugin (which uses system LinkMovementMethod by default)
                .usePlugin(MovementMethodPlugin.create())
                .usePlugin(new AbstractMarkwonPlugin() {
                    @Override
                    public void configureConfiguration(@NonNull MarkwonConfiguration.Builder builder) {
                        // for example if specified destination has no scheme info, we will
                        // _assume_ that it's network request and append HTTPS scheme
                        builder.urlProcessor(destination -> {
                            final Uri uri = Uri.parse(destination);
                            if (TextUtils.isEmpty(uri.getScheme())) {
                                return "https://" + destination;
                            }
                            return destination;
                        });
                    }
                })
                .build();

        markwon.setMarkdown(textView, markdown);
    }

    /**
     * Images configuration. Can be used with (or without) ImagesPlugin, which does some basic
     * images handling (parsing markdown containing images, obtain an image from network
     * file system or assets). Please note that
     */
    private void imagesPlugin() {

        final String markdown = "![image](myownscheme://en.wikipedia.org/static/images/project-logos/enwiki-2x.png)";

        final Markwon markwon = Markwon.builder(this)
                .usePlugin(ImagesPlugin.create())
                .usePlugin(new AbstractMarkwonPlugin() {
                    @Override
                    public void configure(@NonNull Registry registry) {

                        // use registry.require to obtain a plugin, does also
                        // a runtime validation if this plugin is registered
                        registry.require(ImagesPlugin.class, plugin -> plugin.addSchemeHandler(new SchemeHandler() {

                            // it's a sample only, most likely you won't need to
                            // use existing scheme-handler, this for demonstration purposes only
                            final NetworkSchemeHandler handler = NetworkSchemeHandler.create();

                            @NonNull
                            @Override
                            public ImageItem handle(@NonNull String raw, @NonNull Uri uri) {
                                final String url = raw.replace("myownscheme", "https");
                                return handler.handle(url, Uri.parse(url));
                            }

                            @NonNull
                            @Override
                            public Collection<String> supportedSchemes() {
                                return Collections.singleton("myownscheme");
                            }
                        }));
                    }
                })
                // or we can init plugin with this factory method
//                .usePlugin(ImagesPlugin.create(plugin -> {
//                    plugin.addSchemeHandler(/**/)
//                }))
                .build();

        markwon.setMarkdown(textView, markdown);
    }

    private void softBreakAddsSpace() {
        // default behavior

        final String md = "" +
                "Hello there ->(line)\n(break)<- going on and on";

        Markwon.create(this).setMarkdown(textView, md);
    }

    private void softBreakAddsNewLine() {
        // insert a new line when markdown has a soft break

        final Markwon markwon = Markwon.builder(this)
                .usePlugin(SoftBreakAddsNewLinePlugin.create())
                .build();

        final String md = "" +
                "Hello there ->(line)\n(break)<- going on and on";

        markwon.setMarkdown(textView, md);
    }

    private void additionalSpacing() {

        // please note that bottom line (after 1 & 2 levels) will be drawn _AFTER_ padding
        final int spacing = (int) (128 * getResources().getDisplayMetrics().density + .5F);

        final Markwon markwon = Markwon.builder(this)
                .usePlugin(new AbstractMarkwonPlugin() {
                    @Override
                    public void configureTheme(@NonNull MarkwonTheme.Builder builder) {
                        builder.headingBreakHeight(0);
                    }

                    @Override
                    public void configureSpansFactory(@NonNull MarkwonSpansFactory.Builder builder) {
                        builder.appendFactory(
                                Heading.class,
                                (configuration, props) -> new LastLineSpacingSpan(spacing));
                    }
                })
                .build();

        final String md = "" +
                "# Title title title title title title title title title title \n\ntext text text text";

        markwon.setMarkdown(textView, md);
    }

    private void headingNoSpace() {
        final Markwon markwon = Markwon.builder(this)
                .usePlugin(new AbstractMarkwonPlugin() {
                    @Override
                    public void configureTheme(@NonNull MarkwonTheme.Builder builder) {
                        builder.headingBreakHeight(0);
                    }

                    @Override
                    public void configureVisitor(@NonNull MarkwonVisitor.Builder builder) {
                        builder.on(Heading.class, (visitor, heading) -> {

                            visitor.ensureNewLine();

                            final int length = visitor.length();
                            visitor.visitChildren(heading);

                            CoreProps.HEADING_LEVEL.set(visitor.renderProps(), heading.getLevel());

                            visitor.setSpansForNodeOptional(heading, length);

                            if (visitor.hasNext(heading)) {
                                visitor.ensureNewLine();
//                                visitor.forceNewLine();
                            }
                        });
                    }
                })
                .build();

        final String md = "" +
                "# Title title title title title title title title title title \n\ntext text text text";

        markwon.setMarkdown(textView, md);
    }

    private void headingNoSpaceBlockHandler() {
        final Markwon markwon = Markwon.builder(this)
                .usePlugin(new AbstractMarkwonPlugin() {
                    @Override
                    public void configureVisitor(@NonNull MarkwonVisitor.Builder builder) {
                        builder.blockHandler(new BlockHandlerDef() {
                            @Override
                            public void blockEnd(@NonNull MarkwonVisitor visitor, @NonNull Node node) {
                                if (node instanceof Heading) {
                                    if (visitor.hasNext(node)) {
                                        visitor.ensureNewLine();
                                        // ensure new line but do not force insert one
                                    }
                                } else {
                                    super.blockEnd(visitor, node);
                                }
                            }
                        });
                    }
                })
                .build();

        final String md = "" +
                "# Title title title title title title title title title title \n\ntext text text text";

        markwon.setMarkdown(textView, md);
    }

    private void allBlocksNoForcedLine() {
        final MarkwonVisitor.BlockHandler blockHandler = new BlockHandlerDef() {
            @Override
            public void blockEnd(@NonNull MarkwonVisitor visitor, @NonNull Node node) {
                if (visitor.hasNext(node)) {
                    visitor.ensureNewLine();
                }
            }
        };

        final Markwon markwon = Markwon.builder(this)
                .usePlugin(new AbstractMarkwonPlugin() {
                    @Override
                    public void configureVisitor(@NonNull MarkwonVisitor.Builder builder) {
                        builder.blockHandler(blockHandler);
                    }
                })
                .build();

        final String md = "" +
                "# Hello there!\n\n" +
                "* a first\n" +
                "* second\n" +
                "- third\n" +
                "* * nested one\n\n" +
                "> block quote\n\n" +
                "> > and nested one\n\n" +
                "```java\n" +
                "final int i = 0;\n" +
                "```\n\n";

        markwon.setMarkdown(textView, md);
    }

    private void anchor() {
        final String lorem = getString(R.string.lorem);
        final String md = "" +
                "Hello [there](#there)!\n\n\n" +
                lorem + "\n\n" +
                "# There!\n\n" +
                lorem;

        final Markwon markwon = Markwon.builder(this)
                .usePlugin(new AnchorHeadingPlugin((view, top) -> scrollView.smoothScrollTo(0, top)))
                .build();

        markwon.setMarkdown(textView, md);
    }

    private void letterOrderedList() {
        // bullet list nested in ordered list renders letters instead of bullets
        final String md = "" +
                "1. Hello there!\n" +
                "1. And here is how:\n" +
                "   - First\n" +
                "   - Second\n" +
                "   - Third\n" +
                "      1. And first here\n\n";

        final Markwon markwon = Markwon.builder(this)
                .usePlugin(new BulletListIsOrderedWithLettersWhenNestedPlugin())
                .build();

        markwon.setMarkdown(textView, md);
    }
}
