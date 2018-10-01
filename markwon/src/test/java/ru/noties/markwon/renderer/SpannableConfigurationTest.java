package ru.noties.markwon.renderer;

import org.junit.Test;

import ru.noties.markwon.SpannableConfiguration;
import ru.noties.markwon.SpannableFactory;
import ru.noties.markwon.SyntaxHighlight;
import ru.noties.markwon.UrlProcessor;
import ru.noties.markwon.html.api.MarkwonHtmlParser;
import ru.noties.markwon.renderer.html2.MarkwonHtmlRenderer;
import ru.noties.markwon.spans.AsyncDrawable;
import ru.noties.markwon.spans.LinkSpan;
import ru.noties.markwon.spans.SpannableTheme;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
public class SpannableConfigurationTest {

    @Test
    public void testNewBuilder() {
        final SpannableConfiguration configuration = SpannableConfiguration
                .builder(null)
                .theme(mock(SpannableTheme.class))
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

        final SpannableConfiguration newConfiguration = configuration
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
