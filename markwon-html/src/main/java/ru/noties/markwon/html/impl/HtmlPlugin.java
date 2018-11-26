package ru.noties.markwon.html.impl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.commonmark.node.Document;
import org.commonmark.node.HtmlBlock;
import org.commonmark.node.HtmlInline;

import ru.noties.markwon.AbstractMarkwonPlugin;
import ru.noties.markwon.MarkwonConfiguration;
import ru.noties.markwon.MarkwonVisitor;
import ru.noties.markwon.html.MarkwonHtmlParser;
import ru.noties.markwon.html.MarkwonHtmlRenderer;

public class HtmlPlugin extends AbstractMarkwonPlugin {

    @NonNull
    public static HtmlPlugin create() {
        return create(MarkwonHtmlRendererImpl.create(), MarkwonHtmlParserImpl.create());
    }

    @NonNull
    public static HtmlPlugin create(@NonNull MarkwonHtmlRenderer renderer) {
        return create(renderer, MarkwonHtmlParserImpl.create());
    }

    @NonNull
    public static HtmlPlugin create(@NonNull MarkwonHtmlParser parser) {
        return create(MarkwonHtmlRendererImpl.create(), parser);
    }

    @NonNull
    public static HtmlPlugin create(@NonNull MarkwonHtmlRenderer renderer, @NonNull MarkwonHtmlParser parser) {
        return new HtmlPlugin(renderer, parser);
    }

    private final MarkwonHtmlRenderer renderer;
    private final MarkwonHtmlParser parser;

    public HtmlPlugin(@NonNull MarkwonHtmlRenderer renderer, @NonNull MarkwonHtmlParser parser) {
        this.renderer = renderer;
        this.parser = parser;
    }

    @Override
    public void configureConfiguration(@NonNull MarkwonConfiguration.Builder builder) {
        builder
                .htmlParser(parser)
                .htmlRenderer(renderer);
    }

    @Override
    public void configureVisitor(@NonNull MarkwonVisitor.Builder builder) {
        builder
                .on(Document.class, new MarkwonVisitor.NodeVisitor<Document>() {
                    @Override
                    public void visit(@NonNull MarkwonVisitor visitor, @NonNull Document document) {

                        visitor.visitChildren(document);

                        final MarkwonConfiguration configuration = visitor.configuration();
                        configuration.htmlRenderer().render(configuration, visitor.builder(), configuration.htmlParser());
                    }
                })
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
