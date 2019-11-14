package io.noties.markwon.inlineparser;

import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.commonmark.testutil.SpecTestCase;
import org.commonmark.testutil.example.Example;

public class InlineParserSpecTest extends SpecTestCase {

    private static final Parser PARSER = Parser.builder()
            .inlineParserFactory(MarkwonInlineParser.factoryBuilder().build())
            .build();

    // The spec says URL-escaping is optional, but the examples assume that it's enabled.
    private static final HtmlRenderer RENDERER = HtmlRenderer.builder().percentEncodeUrls(true).build();

    public InlineParserSpecTest(Example example) {
        super(example);
    }

    @Override
    protected String render(String source) {
        return RENDERER.render(PARSER.parse(source));
    }
}
