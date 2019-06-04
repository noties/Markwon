package ru.noties.markwon.sample.basicplugins;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import org.commonmark.node.Heading;
import org.commonmark.node.Paragraph;

import ru.noties.markwon.AbstractMarkwonPlugin;
import ru.noties.markwon.Markwon;
import ru.noties.markwon.MarkwonConfiguration;
import ru.noties.markwon.MarkwonPlugin;
import ru.noties.markwon.MarkwonSpansFactory;
import ru.noties.markwon.MarkwonVisitor;
import ru.noties.markwon.movement.MovementMethodPlugin;
import ru.noties.markwon.core.MarkwonTheme;
import ru.noties.markwon.image.AsyncDrawableLoader;
import ru.noties.markwon.image.ImageItem;
import ru.noties.markwon.image.ImagesPlugin;
import ru.noties.markwon.image.SchemeHandler;
import ru.noties.markwon.image.network.NetworkSchemeHandler;

public class BasicPluginsActivity extends Activity {

    private TextView textView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        textView = new TextView(this);
        setContentView(textView);

        step_1();

        step_2();

        step_3();

        step_4();

        step_5();
    }

    /**
     * In order to apply paragraph spans a custom plugin should be created (CorePlugin will take care
     * of everything else).
     * <p>
     * Please note that when a plugin is registered and it <em>depends</em> on CorePlugin, there is no
     * need to explicitly specify it. By default all plugins that extend AbstractMarkwonPlugin do declare
     * it\'s dependency on CorePlugin ({@link MarkwonPlugin#priority()}).
     * <p>
     * Order in which plugins are specified to the builder is of little importance as long as each
     * plugin clearly states what dependencies it has
     */
    private void step_1() {

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
    private void step_2() {

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
    private void step_3() {

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
    private void step_4() {

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
    private void step_5() {

        final String markdown = "![image](myownscheme://en.wikipedia.org/static/images/project-logos/enwiki-2x.png)";

        final Markwon markwon = Markwon.builder(this)
                .usePlugin(ImagesPlugin.create(this))
                .usePlugin(new AbstractMarkwonPlugin() {
                    @Override
                    public void configureImages(@NonNull AsyncDrawableLoader.Builder builder) {
                        // we can have a custom SchemeHandler
                        // here we will just use networkSchemeHandler to redirect call
                        builder.addSchemeHandler("myownscheme", new SchemeHandler() {

                            final NetworkSchemeHandler networkSchemeHandler = NetworkSchemeHandler.create();

                            @Nullable
                            @Override
                            public ImageItem handle(@NonNull String raw, @NonNull Uri uri) {
                                raw = raw.replace("myownscheme", "https");
                                return networkSchemeHandler.handle(raw, Uri.parse(raw));
                            }
                        });
                    }
                })
                .build();

        markwon.setMarkdown(textView, markdown);
    }

    // text lifecycle (after/before)
    // rendering lifecycle (before/after)
    // renderProps
    // process
    // priority
}
