package ru.noties.markwon.html;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.commonmark.node.HtmlBlock;
import org.commonmark.node.HtmlInline;
import org.commonmark.node.Node;

import ru.noties.markwon.AbstractMarkwonPlugin;
import ru.noties.markwon.MarkwonConfiguration;
import ru.noties.markwon.MarkwonVisitor;
import ru.noties.markwon.html.tag.BlockquoteHandler;
import ru.noties.markwon.html.tag.EmphasisHandler;
import ru.noties.markwon.html.tag.HeadingHandler;
import ru.noties.markwon.html.tag.ImageHandler;
import ru.noties.markwon.html.tag.LinkHandler;
import ru.noties.markwon.html.tag.ListHandler;
import ru.noties.markwon.html.tag.StrikeHandler;
import ru.noties.markwon.html.tag.StrongEmphasisHandler;
import ru.noties.markwon.html.tag.SubScriptHandler;
import ru.noties.markwon.html.tag.SuperScriptHandler;
import ru.noties.markwon.html.tag.UnderlineHandler;

/**
 * @since 3.0.0
 */
public class HtmlPlugin extends AbstractMarkwonPlugin {

    @NonNull
    public static HtmlPlugin create() {
        return new HtmlPlugin();
    }

    public static final float SCRIPT_DEF_TEXT_SIZE_RATIO = .75F;

    @Override
    public void configureConfiguration(@NonNull MarkwonConfiguration.Builder builder) {
        builder.htmlParser(MarkwonHtmlParserImpl.create());
    }

    @Override
    public void configureHtmlRenderer(@NonNull MarkwonHtmlRenderer.Builder builder) {
        builder
                .addHandler(new EmphasisHandler(), "i", "em", "cite", "dfn")
                .addHandler(new StrongEmphasisHandler(), "b", "strong")
                .addHandler(new SuperScriptHandler(), "sup")
                .addHandler(new SubScriptHandler(), "sub")
                .addHandler(new UnderlineHandler(), "u", "ins")
                .addHandler(new StrikeHandler(), "s", "del")
                .addHandler(new LinkHandler(), "a")
                .addHandler(new ListHandler(), "ul", "ol")
                .addHandler(ImageHandler.create(), "img")
                .addHandler(new BlockquoteHandler(), "blockquote")
                .addHandler(new HeadingHandler(), "h1", "h2", "h3", "h4", "h5", "h6");
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
