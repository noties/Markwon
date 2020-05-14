package io.noties.markwon.html;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.commonmark.node.HtmlBlock;
import org.commonmark.node.HtmlInline;
import org.commonmark.node.Node;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.MarkwonConfiguration;
import io.noties.markwon.MarkwonVisitor;
import io.noties.markwon.html.tag.BlockquoteHandler;
import io.noties.markwon.html.tag.EmphasisHandler;
import io.noties.markwon.html.tag.HeadingHandler;
import io.noties.markwon.html.tag.ImageHandler;
import io.noties.markwon.html.tag.LinkHandler;
import io.noties.markwon.html.tag.ListHandler;
import io.noties.markwon.html.tag.StrikeHandler;
import io.noties.markwon.html.tag.StrongEmphasisHandler;
import io.noties.markwon.html.tag.SubScriptHandler;
import io.noties.markwon.html.tag.SuperScriptHandler;
import io.noties.markwon.html.tag.UnderlineHandler;

/**
 * @since 3.0.0
 */
public class HtmlPlugin extends AbstractMarkwonPlugin {

    /**
     * @see #create(HtmlConfigure)
     * @since 4.0.0
     */
    public interface HtmlConfigure {
        void configureHtml(@NonNull HtmlPlugin plugin);
    }

    @NonNull
    public static HtmlPlugin create() {
        return new HtmlPlugin();
    }

    /**
     * @since 4.0.0
     */
    @NonNull
    public static HtmlPlugin create(@NonNull HtmlConfigure configure) {
        final HtmlPlugin plugin = create();
        configure.configureHtml(plugin);
        return plugin;
    }

    public static final float SCRIPT_DEF_TEXT_SIZE_RATIO = .75F;

    private final MarkwonHtmlRendererImpl.Builder builder;

    private MarkwonHtmlParser htmlParser;
    private MarkwonHtmlRenderer htmlRenderer;

    // @since 4.4.0
    private HtmlEmptyTagReplacement emptyTagReplacement = new HtmlEmptyTagReplacement();

    @SuppressWarnings("WeakerAccess")
    HtmlPlugin() {
        this.builder = new MarkwonHtmlRendererImpl.Builder();
    }

    /**
     * @param allowNonClosedTags whether or not non-closed tags should be closed
     *                           at the document end. By default `false`
     * @since 4.0.0
     */
    @NonNull
    public HtmlPlugin allowNonClosedTags(boolean allowNonClosedTags) {
        builder.allowNonClosedTags(allowNonClosedTags);
        return this;
    }

    /**
     * @since 4.0.0
     */
    @NonNull
    public HtmlPlugin addHandler(@NonNull TagHandler tagHandler) {
        builder.addHandler(tagHandler);
        return this;
    }

    /**
     * @since 4.0.0
     */
    @Nullable
    public TagHandler getHandler(@NonNull String tagName) {
        return builder.getHandler(tagName);
    }

    /**
     * Indicate if HtmlPlugin should register default HTML tag handlers. Pass `true` to <strong>not</strong>
     * include default handlers. By default default handlers are included. You can use
     * {@link TagHandlerNoOp} to no-op certain default tags.
     *
     * @see TagHandlerNoOp
     * @since 4.0.0
     */
    @NonNull
    public HtmlPlugin excludeDefaults(boolean excludeDefaults) {
        builder.excludeDefaults(excludeDefaults);
        return this;
    }

    /**
     * @param emptyTagReplacement {@link HtmlEmptyTagReplacement}
     * @since 4.4.0
     */
    @NonNull
    public HtmlPlugin emptyTagReplacement(@NonNull HtmlEmptyTagReplacement emptyTagReplacement) {
        this.emptyTagReplacement = emptyTagReplacement;
        return this;
    }

    @Override
    public void configureConfiguration(@NonNull MarkwonConfiguration.Builder configurationBuilder) {

        // @since 4.0.0 we init internal html-renderer here (marks the end of configuration)

        final MarkwonHtmlRendererImpl.Builder builder = this.builder;

        if (!builder.excludeDefaults()) {
            // please note that it's better to not checkState for
            // this method call (minor optimization), final `build` method call
            // will check for the state and throw an exception if applicable
            builder.addDefaultTagHandler(ImageHandler.create());
            builder.addDefaultTagHandler(new LinkHandler());
            builder.addDefaultTagHandler(new BlockquoteHandler());
            builder.addDefaultTagHandler(new SubScriptHandler());
            builder.addDefaultTagHandler(new SuperScriptHandler());
            builder.addDefaultTagHandler(new StrongEmphasisHandler());
            builder.addDefaultTagHandler(new StrikeHandler());
            builder.addDefaultTagHandler(new UnderlineHandler());
            builder.addDefaultTagHandler(new ListHandler());
            builder.addDefaultTagHandler(new EmphasisHandler());
            builder.addDefaultTagHandler(new HeadingHandler());
        }

        htmlParser = MarkwonHtmlParserImpl.create(emptyTagReplacement);
        htmlRenderer = builder.build();
    }

    @Override
    public void afterRender(@NonNull Node node, @NonNull MarkwonVisitor visitor) {
        final MarkwonHtmlRenderer htmlRenderer = this.htmlRenderer;
        if (htmlRenderer != null) {
            htmlRenderer.render(visitor, htmlParser);
        } else {
            throw new IllegalStateException("Unexpected state, html-renderer is not defined");
        }
    }

    @Override
    public void configureVisitor(@NonNull MarkwonVisitor.Builder builder) {
        builder
                .on(HtmlBlock.class, new MarkwonVisitor.NodeVisitor<HtmlBlock>() {
                    @Override
                    public void visit(@NonNull MarkwonVisitor visitor, @NonNull HtmlBlock htmlBlock) {
                        visitHtml(visitor, htmlBlock.getLiteral());
                    }
                })
                .on(HtmlInline.class, new MarkwonVisitor.NodeVisitor<HtmlInline>() {
                    @Override
                    public void visit(@NonNull MarkwonVisitor visitor, @NonNull HtmlInline htmlInline) {
                        visitHtml(visitor, htmlInline.getLiteral());
                    }
                });
    }

    private void visitHtml(@NonNull MarkwonVisitor visitor, @Nullable String html) {
        if (html != null) {
            htmlParser.processFragment(visitor.builder(), html);
        }
    }
}
