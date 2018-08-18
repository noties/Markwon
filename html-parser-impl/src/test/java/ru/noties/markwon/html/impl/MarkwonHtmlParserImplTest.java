package ru.noties.markwon.html.impl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ru.noties.markwon.html.api.HtmlTag;
import ru.noties.markwon.html.api.MarkwonHtmlParser;
import ru.noties.markwon.html.impl.HtmlEmptyTagReplacement;
import ru.noties.markwon.html.impl.MarkwonHtmlParserImpl;
import ru.noties.markwon.html.impl.jsoup.parser.Token;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class MarkwonHtmlParserImplTest {

    @Test
    public void inlineTags() {

        // all inline tags are correctly parsed

        // a simple replacement that will return tag name as replacement (for this test purposes)
        final MarkwonHtmlParserImpl impl = new MarkwonHtmlParserImpl(new HtmlEmptyTagReplacement() {
            @Nullable
            @Override
            public String replace(@NonNull Token.StartTag startTag) {
                return startTag.normalName;
            }
        });

        // all inline tags are parsed as ones
        final List<String> tags = Arrays.asList(
                "a", "abbr", "acronym",
                "b", "bdo", "big", "br", "button",
                "cite", "code",
                "dfn",
                "em",
                "i", "img", "input",
                "kbd",
                "label",
                "map",
                "object",
                "q",
                "samp", "script", "select", "small", "span", "strong", "sub", "sup",
                "textarea", "time", "tt",
                "var"
        );

        final StringBuilder html = new StringBuilder();
        for (String tag : tags) {
            html.append('<')
                    .append(tag)
                    .append('>')
                    .append(tag)
                    .append("</")
                    .append(tag)
                    .append('>');
        }

        final StringBuilder output = new StringBuilder();

        impl.processFragment(output, html.toString());

        final CaptureInlineTagsAction action = new CaptureInlineTagsAction();

        impl.flushInlineTags(output.length(), action);

        assertTrue(action.called);

        final List<HtmlTag.Inline> inlines = action.tags;

        if (tags.size() != inlines.size()) {
            final Set<String> missing = new HashSet<>(tags);
            for (HtmlTag.Inline inline : inlines) {
                missing.remove(inline.name());
            }
            assertTrue("Missing inline tags: " + missing, false);
        }

        final Set<String> set = new HashSet<>(tags);

        for (HtmlTag.Inline inline : inlines) {
            assertTrue(set.remove(inline.name()));
            assertEquals(inline.name(), output.substring(inline.start(), inline.end()));
        }

        assertEquals(0, set.size());
    }

    @Test
    public void inlineVoidTags() {

        // all inline void tags are correctly parsed

        final List<String> tags = Arrays.asList(
                "br",
                "img", "input"
        );

        final MarkwonHtmlParserImpl impl = new MarkwonHtmlParserImpl(new HtmlEmptyTagReplacement() {
            @Nullable
            @Override
            public String replace(@NonNull Token.StartTag startTag) {
                return null;
            }
        });

        final StringBuilder html = new StringBuilder();
        for (String tag : tags) {
            html.append('<')
                    .append(tag)
                    .append('>');
        }

        final StringBuilder output = new StringBuilder();

        impl.processFragment(output, html.toString());

        assertEquals(0, output.length());

        final CaptureInlineTagsAction action = new CaptureInlineTagsAction();

        impl.flushInlineTags(output.length(), action);

        assertTrue(action.called);

        final List<HtmlTag.Inline> inlines = action.tags;

        assertEquals(inlines.toString(), tags.size(), inlines.size());

        final Set<String> set = new HashSet<>(tags);

        for (HtmlTag.Inline inline : inlines) {
            assertEquals(inline.name(), inline.start(), inline.end());
            assertTrue(inline.name(), inline.isEmpty());
            assertTrue(set.remove(inline.name()));
        }

        assertEquals(set.toString(), 0, set.size());
    }

    @Test
    public void blockVoidTags() {

        final MarkwonHtmlParserImpl impl = new MarkwonHtmlParserImpl(new HtmlEmptyTagReplacement() {
            @Nullable
            @Override
            public String replace(@NonNull Token.StartTag startTag) {
                return null;
            }
        });

        final List<String> tags = Arrays.asList(
                "area",
                "base",
                "col",
                "embed",
                "hr",
                "keygen",
                "link",
                "meta",
                "param",
                "source",
                "track",
                "wbr"
        );

        final StringBuilder html = new StringBuilder();
        for (String tag : tags) {
            html.append('<')
                    .append(tag)
                    .append('>');
        }

        final StringBuilder output = new StringBuilder();

        impl.processFragment(output, html.toString());

        assertEquals(0, output.length());

        final CaptureBlockTagsAction action = new CaptureBlockTagsAction();
        impl.flushBlockTags(output.length(), action);

        assertTrue(action.called);

        final List<HtmlTag.Block> blocks = action.tags;

        assertEquals(blocks.toString(), tags.size(), blocks.size());

        final Set<String> set = new HashSet<>(tags);

        for (HtmlTag.Block block : blocks) {
            assertEquals(block.name(), block.start(), block.end());
            assertTrue(block.name(), block.isEmpty());
            assertTrue(set.remove(block.name()));
        }

        assertEquals(set.toString(), 0, set.size());
    }

    @Test
    public void selfClosingTags() {

        // self-closing tags (grammatically) must be replaced (no checks for real html)

        final List<String> tags = Arrays.asList(
                "one",
                "two-two",
                "three-three-three",
                "four-four-four-four",
                "FiveFiveFiveFiveFive"
        );

        final MarkwonHtmlParserImpl impl = new MarkwonHtmlParserImpl(new HtmlEmptyTagReplacement() {
            @Nullable
            @Override
            public String replace(@NonNull Token.StartTag startTag) {
                return null;
            }
        });

        final StringBuilder html = new StringBuilder();
        for (String tag : tags) {
            html.append('<')
                    .append(tag)
                    .append(" />");
        }

        final StringBuilder output = new StringBuilder();

        impl.processFragment(output, html.toString());

        assertEquals(output.toString(), 0, output.length());

        final CaptureBlockTagsAction action = new CaptureBlockTagsAction();

        impl.flushBlockTags(output.length(), action);

        assertTrue(action.called);

        final List<HtmlTag.Block> blocks = action.tags;

        assertEquals(blocks.toString(), tags.size(), blocks.size());

        // tag names must be lower cased
        final Set<String> set = new HashSet<>(tags.size());
        for (String tag: tags) {
            set.add(tag.toLowerCase());
        }

        for (HtmlTag.Block block: blocks) {
            assertTrue(block.name(), block.isEmpty());
            assertTrue(set.remove(block.name()));
        }

        assertEquals(set.toString(), 0, set.size());
    }

    @Test
    public void blockTags() {

        // the tags that will require a new line before them

        final List<String> tags = Arrays.asList(
                "address", "article", "aside",
                "blockquote",
                "canvas",
                "dd", "div", "dl", "dt",
                "fieldset", "figcaption", "figure", "footer", "form",
                "h1", "h2", "h3", "h4", "h5", "h6", "header", "hgroup", "hr",
                "li",
                "main",
                "nav", "noscript",
                "ol", "output",
                "p", "pre",
                "section",
                "table", "tfoot",
                "ul",
                "video"
        );

        final MarkwonHtmlParserImpl impl = new MarkwonHtmlParserImpl(new HtmlEmptyTagReplacement() {
            @Nullable
            @Override
            public String replace(@NonNull Token.StartTag startTag) {
                return startTag.normalName;
            }
        });

        final StringBuilder html = new StringBuilder();
        for (String tag: tags) {
            html.append('<')
                    .append(tag)
                    .append('>')
                    .append(tag)
                    .append("</")
                    .append(tag)
                    .append('>');
        }

        final StringBuilder output = new StringBuilder();

        impl.processFragment(output, html.toString());

        final CaptureBlockTagsAction action = new CaptureBlockTagsAction();

        impl.flushBlockTags(output.length(), action);

        assertTrue(action.called);

        final List<HtmlTag.Block> blocks = action.tags;
        assertEquals(blocks.toString(), tags.size(), blocks.size());

        final Set<String> set = new HashSet<>(tags);

        boolean first = true;
        for (HtmlTag.Block block: blocks) {
            assertEquals(block.name(), block.name(), output.substring(block.start(), block.end()));
            if (first) {
                first = false;
            } else {
                assertEquals('\n', output.charAt(block.start() - 1));
            }
            assertTrue(set.remove(block.name()));
        }

        assertEquals(set.toString(), 0, set.size());
    }

    @Test
    public void multipleFragmentsContinuation() {
        throw new RuntimeException();
    }

    @Test
    public void paragraphCannotContainAnythingButInlines() {
        throw new RuntimeException();
    }

    // move to htmlInlineTagreplacement test class
    @Test
    public void imageReplacementNoAlt() {
        throw new RuntimeException();
    }

    @Test
    public void brAddsNewLine() {
        throw new RuntimeException();
    }

    @Test
    public void imageReplacementAlt() {
        throw new RuntimeException();
    }

    @Test
    public void blockCloseClosesChildren() {
        throw new RuntimeException();
    }

    @Test
    public void allReturnedTagsAreClosed() {
        throw new RuntimeException();
    }

    @Test
    public void allTagsAreLowerCase() {
        throw new RuntimeException();
    }

    @Test
    public void previousListItemClosed() {
        throw new RuntimeException();
    }

    @Test
    public void nestedBlocks() {
        throw new RuntimeException();
    }

    @Test
    public void attributes() {
        throw new RuntimeException();
    }

    private static class CaptureTagsAction<T> implements MarkwonHtmlParser.FlushAction<T> {

        boolean called;
        List<T> tags;

        @Override
        public void apply(@NonNull List<T> tags) {
            this.called = true;
            this.tags = new ArrayList<>(tags);
        }
    }

    private static class CaptureInlineTagsAction extends CaptureTagsAction<HtmlTag.Inline> {
    }

    private static class CaptureBlockTagsAction extends CaptureTagsAction<HtmlTag.Block> {
    }
}