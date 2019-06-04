package io.noties.markwon.html;

import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import io.noties.markwon.html.HtmlTagImpl.InlineImpl;

import static org.junit.Assert.assertEquals;

public class HtmlEmptyTagReplacementTest {

    private HtmlEmptyTagReplacement replacement;

    @Before
    public void before() {
        replacement = HtmlEmptyTagReplacement.create();
    }

    @Test
    public void imageReplacementNoAlt() {
        final HtmlTag.Inline img = new InlineImpl("img", -1, Collections.<String, String>emptyMap());
        assertEquals("\uFFFC", replacement.replace(img));
    }

    @Test
    public void imageReplacementAlt() {
        final HtmlTag.Inline img = new InlineImpl(
                "img",
                -1,
                Collections.singletonMap("alt", "alternative27")
        );
        assertEquals("alternative27", replacement.replace(img));
    }

    @Test
    public void brAddsNewLine() {
        final HtmlTag.Inline br = new InlineImpl(
                "br",
                -1,
                Collections.<String, String>emptyMap()
        );
        assertEquals("\n", replacement.replace(br));
    }
}