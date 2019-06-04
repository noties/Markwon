package ru.noties.markwon.html;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.commonmark.node.HtmlBlock;
import org.commonmark.node.HtmlInline;
import org.commonmark.node.Node;

import ru.noties.markwon.AbstractMarkwonPlugin;
import ru.noties.markwon.MarkwonConfiguration;
import ru.noties.markwon.MarkwonVisitor;

/**
 * @since 3.0.0
 */
public class HtmlPlugin extends AbstractMarkwonPlugin {

    @NonNull
    public static HtmlPlugin create() {
        return new HtmlPlugin();
    }

    public static final float SCRIPT_DEF_TEXT_SIZE_RATIO = .75F;

    private final MarkwonHtmlRendererImpl.Builder builder;

    @SuppressWarnings("WeakerAccess")
    HtmlPlugin() {
        this.builder = new MarkwonHtmlRendererImpl.Builder();
    }

    /**
     * @param allowNonClosedTags whether or not non-closed tags should be closed
     *                           at the document end. By default `false`
     * @since 4.0.0-SNAPSHOT
     */
    @NonNull
    public HtmlPlugin allowNonClosedTags(boolean allowNonClosedTags) {
        builder.allowNonClosedTags(allowNonClosedTags);
        return this;
    }

    /**
     * @since 4.0.0-SNAPSHOT
     */
    @NonNull
    public HtmlPlugin addHandler(@NonNull TagHandler tagHandler) {
        builder.addHandler(tagHandler);
        return this;
    }

    /**
     * @since 4.0.0-SNAPSHOT
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
     * @since 4.0.0-SNAPSHOT
     */
    @NonNull
    public HtmlPlugin excludeDefaults(boolean excludeDefaults) {
        builder.excludeDefaults(excludeDefaults);
        return this;
    }

    @Override
    public void configureConfiguration(@NonNull MarkwonConfiguration.Builder builder) {
        builder
                .htmlRenderer(this.builder.build())
                .htmlParser(MarkwonHtmlParserImpl.create());
    }

    @Override
    public void afterRender(@NonNull Node node, @NonNull MarkwonVisitor visitor) {
        final MarkwonConfiguration configuration = visitor.configuration();
        configuration.htmlRenderer().render(visitor, configuration.htmlParser());
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
            visitor.configuration().htmlParser().processFragment(visitor.builder(), html);
        }
    }
}
