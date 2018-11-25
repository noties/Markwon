package ru.noties.markwon.renderer;

import org.junit.Test;

import ru.noties.markwon.MarkwonConfiguration;
import ru.noties.markwon.SpannableFactory;
import ru.noties.markwon.SyntaxHighlight;
import ru.noties.markwon.UrlProcessor;
import ru.noties.markwon.html.api.MarkwonHtmlParser;
import ru.noties.markwon.renderer.html2.MarkwonHtmlRenderer;
import ru.noties.markwon.image.AsyncDrawable;
import ru.noties.markwon.spans.LinkSpan;
import ru.noties.markwon.spans.MarkwonTheme;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
public class MarkwonConfigurationTest {

    @Test
    public void testNewBuilder() {
        final MarkwonConfiguration configuration = MarkwonConfiguration
                .builder(null)
                .theme(mock(MarkwonTheme.class))
                .asyncDrawableLoader(mock(AsyncDrawable.Loader.class))
                .syntaxHighlight(mock(SyntaxHighlight.class))
                .linkResolver(mock(LinkSpan.Resolver.class))
                .urlProcessor(mock(UrlProcessor.class))
                .imageSizeResolver(mock(ImageSizeResolver.class))
                .factory(mock(SpannableFactory.class))
                .softBreakAddsNewLine(true)
                .htmlParser(mock(MarkwonHtmlParser.class))
                .htmlRenderer(mock(MarkwonHtmlRenderer.class))
                .htmlAllowNonClosedTags(true)
                .build();

        final MarkwonConfiguration newConfiguration = configuration
                .newBuilder(null)
                .build();

        assertEquals(configuration.theme(), newConfiguration.theme());
        assertEquals(configuration.asyncDrawableLoader(), newConfiguration.asyncDrawableLoader());
        assertEquals(configuration.syntaxHighlight(), newConfiguration.syntaxHighlight());
        assertEquals(configuration.linkResolver(), newConfiguration.linkResolver());
        assertEquals(configuration.urlProcessor(), newConfiguration.urlProcessor());
        assertEquals(configuration.imageSizeResolver(), newConfiguration.imageSizeResolver());
        assertEquals(configuration.factory(), newConfiguration.factory());
        assertEquals(configuration.softBreakAddsNewLine(), newConfiguration.softBreakAddsNewLine());
        assertEquals(configuration.htmlParser(), newConfiguration.htmlParser());
        assertEquals(configuration.htmlRenderer(), newConfiguration.htmlRenderer());
        assertEquals(configuration.htmlAllowNonClosedTags(), newConfiguration.htmlAllowNonClosedTags());
    }
}
