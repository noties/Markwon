package ru.noties.markwon.html.impl;

import org.junit.Before;
import org.junit.Test;

import ru.noties.markwon.html.impl.jsoup.nodes.Attributes;
import ru.noties.markwon.html.impl.jsoup.parser.Token;

import static org.junit.Assert.assertEquals;

public class HtmlEmptyTagReplacementTest {

    private HtmlEmptyTagReplacement replacement;

    @Before
    public void before() {
        replacement = HtmlEmptyTagReplacement.create();
    }

    @Test
    public void imageReplacementNoAlt() {
        final Token.StartTag startTag = new Token.StartTag();
        startTag.normalName = "img";
        assertEquals("\uFFFC", replacement.replace(startTag));
    }

    @Test
    public void imageReplacementAlt() {
        final Token.StartTag startTag = new Token.StartTag();
        startTag.normalName = "img";
        startTag.attributes = new Attributes().put("alt", "alternative27");
        assertEquals("alternative27", replacement.replace(startTag));
    }

    @Test
    public void brAddsNewLine() {
        final Token.StartTag startTag = new Token.StartTag();
        startTag.normalName = "br";
        startTag.selfClosing = true;
        assertEquals("\n", replacement.replace(startTag));
    }
}