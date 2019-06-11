package io.noties.markwon.html;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class MarkwonHtmlParserImplTest {

    @Test
    public void inlineTags() {

        // all inline tags are correctly parsed

        // a simple replacement that will return tag name as replacement (for this test purposes)
        final MarkwonHtmlParserImpl impl = MarkwonHtmlParserImpl.create(new HtmlEmptyTagReplacement() {
            @Nullable
            @Override
            public String replace(@NonNull HtmlTag tag) {
                return tag.name();
            }
        });

        // all inline tags are parsed as ones
        final List<String> tags = new ArrayList<>(MarkwonHtmlParserImpl.INLINE_TAGS);

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

        final MarkwonHtmlParserImpl impl = MarkwonHtmlParserImpl.create(new HtmlEmptyTagReplacement() {
            @Nullable
            @Override
            public String replace(@NonNull HtmlTag tag) {
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

        final MarkwonHtmlParserImpl impl = MarkwonHtmlParserImpl.create(new HtmlEmptyTagReplacement() {
            @Nullable
            @Override
            public String replace(@NonNull HtmlTag tag) {
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

        final MarkwonHtmlParserImpl impl = MarkwonHtmlParserImpl.create(new HtmlEmptyTagReplacement() {
            @Nullable
            @Override
            public String replace(@NonNull HtmlTag tag) {
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
        for (String tag : tags) {
            set.add(tag.toLowerCase());
        }

        for (HtmlTag.Block block : blocks) {
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

        final MarkwonHtmlParserImpl impl = MarkwonHtmlParserImpl.create(new HtmlEmptyTagReplacement() {
            @Override
            public String replace(@NonNull HtmlTag tag) {
                return tag.name();
            }
        });

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

        final CaptureBlockTagsAction action = new CaptureBlockTagsAction();

        impl.flushBlockTags(output.length(), action);

        assertTrue(action.called);

        final List<HtmlTag.Block> blocks = action.tags;
        assertEquals(blocks.toString(), tags.size(), blocks.size());

        final Set<String> set = new HashSet<>(tags);

        boolean first = true;
        for (HtmlTag.Block block : blocks) {
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

        final MarkwonHtmlParserImpl impl = MarkwonHtmlParserImpl.create(new HtmlEmptyTagReplacement());

        final StringBuilder output = new StringBuilder();

        impl.processFragment(output, "<i>");
        output.append("italic ");
        impl.processFragment(output, "</i>");

        final CaptureInlineTagsAction action = new CaptureInlineTagsAction();
        impl.flushInlineTags(output.length(), action);

        assertTrue(action.called);

        final List<HtmlTag.Inline> inlines = action.tags;
        assertEquals(inlines.toString(), 1, inlines.size());

        final HtmlTag.Inline inline = inlines.get(0);
        assertEquals("i", inline.name());
        assertEquals(0, inline.start());
        assertEquals(output.length(), inline.end());
        assertEquals("italic ", output.toString());
    }

    @Test
    public void paragraphCannotContainAnythingButInlines() {

        final MarkwonHtmlParserImpl impl = MarkwonHtmlParserImpl.create();

        final StringBuilder output = new StringBuilder();

        impl.processFragment(output, "<p><i>italic <b>bold italic <div>in-div</div>");

        final CaptureInlineTagsAction inlineTagsAction = new CaptureInlineTagsAction();
        final CaptureBlockTagsAction blockTagsAction = new CaptureBlockTagsAction();

        impl.flushInlineTags(output.length(), inlineTagsAction);
        impl.flushBlockTags(output.length(), blockTagsAction);

        assertTrue(inlineTagsAction.called);
        assertTrue(blockTagsAction.called);

        final List<HtmlTag.Inline> inlines = inlineTagsAction.tags;
        final List<HtmlTag.Block> blocks = blockTagsAction.tags;

        assertEquals(2, inlines.size());
        assertEquals(2, blocks.size());

        // inlines will be closed at the end of the document
        // P will be closed right before <div>

        with(inlines.get(0), new Action<HtmlTag.Inline>() {
            @Override
            public void apply(@NonNull HtmlTag.Inline inline) {
                assertEquals("i", inline.name());
                assertEquals(0, inline.start());
                assertEquals(output.length(), inline.end());
            }
        });

        with(inlines.get(1), new Action<HtmlTag.Inline>() {
            @Override
            public void apply(@NonNull HtmlTag.Inline inline) {
                assertEquals("b", inline.name());
                assertEquals("italic ".length(), inline.start());
                assertEquals(output.length(), inline.end());
            }
        });

        with(blocks.get(0), new Action<HtmlTag.Block>() {
            @Override
            public void apply(@NonNull HtmlTag.Block block) {
                assertEquals("p", block.name());
                assertEquals(0, block.start());
                assertEquals(output.indexOf("in-div") - 1, block.end());
            }
        });

        with(blocks.get(1), new Action<HtmlTag.Block>() {
            @Override
            public void apply(@NonNull HtmlTag.Block block) {
                assertEquals("div", block.name());
                assertEquals(output.indexOf("in-div"), block.start());
                assertEquals(output.length(), block.end());
            }
        });
    }

    @Test
    public void blockCloseClosesChildren() {

        final MarkwonHtmlParserImpl impl = MarkwonHtmlParserImpl.create();
        final StringBuilder output = new StringBuilder();

        final String html = "<div-1>1<div-2>2<div-3>hello!</div-1>";
        impl.processFragment(output, html);

        assertEquals(output.toString(), "12hello!", output.toString());

        final CaptureBlockTagsAction action = new CaptureBlockTagsAction();
        impl.flushBlockTags(output.length(), action);

        assertTrue(action.called);
        assertEquals(1, action.tags.size());

        with(action.tags.get(0), new Action<HtmlTag.Block>() {
            @Override
            public void apply(@NonNull HtmlTag.Block block) {

                final int end = output.length();

                assertEquals("div-1", block.name());
                assertEquals(0, block.start());
                assertEquals(end, block.end());
                assertEquals(1, block.children().size());

                with(block.children().get(0), new Action<HtmlTag.Block>() {
                    @Override
                    public void apply(@NonNull HtmlTag.Block block) {
                        assertEquals("div-2", block.name());
                        assertEquals(1, block.start());
                        assertEquals(end, block.end());
                        assertEquals(1, block.children().size());

                        with(block.children().get(0), new Action<HtmlTag.Block>() {
                            @Override
                            public void apply(@NonNull HtmlTag.Block block) {
                                assertEquals("div-3", block.name());
                                assertEquals(2, block.start());
                                assertEquals(end, block.end());
                                assertEquals(0, block.children().size());
                            }
                        });
                    }
                });
            }
        });
    }

    @Test
    public void allTagsAreLowerCase() {

        final MarkwonHtmlParserImpl impl = MarkwonHtmlParserImpl.create();
        final StringBuilder output = new StringBuilder();
        impl.processFragment(output, "<DiV><I>italic <eM>emphasis</Em> italic</i></dIv>");

        final CaptureInlineTagsAction inlineTagsAction = new CaptureInlineTagsAction();
        final CaptureBlockTagsAction blockTagsAction = new CaptureBlockTagsAction();

        impl.flushInlineTags(output.length(), inlineTagsAction);
        impl.flushBlockTags(output.length(), blockTagsAction);

        assertTrue(inlineTagsAction.called);
        assertTrue(blockTagsAction.called);

        with(inlineTagsAction.tags, new Action<List<HtmlTag.Inline>>() {
            @Override
            public void apply(@NonNull List<HtmlTag.Inline> inlines) {

                assertEquals(2, inlines.size());

                with(inlines.get(0), new Action<HtmlTag.Inline>() {
                    @Override
                    public void apply(@NonNull HtmlTag.Inline inline) {
                        assertEquals("i", inline.name());
                        assertEquals(0, inline.start());
                        assertEquals(output.length(), inline.end());
                    }
                });

                with(inlines.get(1), new Action<HtmlTag.Inline>() {
                    @Override
                    public void apply(@NonNull HtmlTag.Inline inline) {

                        assertEquals("em", inline.name());

                        final int start = "italic ".length();
                        assertEquals(start, inline.start());
                        assertEquals(start + ("emphasis".length()), inline.end());
                    }
                });
            }
        });

        assertEquals(1, blockTagsAction.tags.size());

        with(blockTagsAction.tags.get(0), new Action<HtmlTag.Block>() {
            @Override
            public void apply(@NonNull HtmlTag.Block block) {
                assertEquals("div", block.name());
                assertEquals(0, block.start());
                assertEquals(output.length(), block.end());
            }
        });
    }

    @Test
    public void previousListItemClosed() {

        final MarkwonHtmlParserImpl impl = MarkwonHtmlParserImpl.create();
        final StringBuilder output = new StringBuilder();

        final String html = "<ul><li>UL-First<li>UL-Second<ol><li>OL-First<li>OL-Second</ol><li>UL-Third";

        impl.processFragment(output, html);

        final CaptureBlockTagsAction action = new CaptureBlockTagsAction();
        impl.flushBlockTags(output.length(), action);

        assertTrue(action.called);
        assertEquals(1, action.tags.size());

        with(action.tags.get(0), new Action<HtmlTag.Block>() {
            @Override
            public void apply(@NonNull HtmlTag.Block block) {

                assertEquals("ul", block.name());
                assertEquals(3, block.children().size());

                with(block.children().get(0), new Action<HtmlTag.Block>() {
                    @Override
                    public void apply(@NonNull HtmlTag.Block block) {
                        assertEquals("li", block.name());
                        assertEquals("UL-First", output.substring(block.start(), block.end()));
                        assertEquals(0, block.children().size());
                    }
                });

                with(block.children().get(1), new Action<HtmlTag.Block>() {
                    @Override
                    public void apply(@NonNull HtmlTag.Block block) {
                        assertEquals("li", block.name());

                        // this block will contain nested block text also
                        assertEquals("UL-Second\nOL-First\nOL-Second", output.substring(block.start(), block.end()));
                        assertEquals(1, block.children().size());

                        with(block.children().get(0), new Action<HtmlTag.Block>() {
                            @Override
                            public void apply(@NonNull HtmlTag.Block block) {
                                assertEquals("ol", block.name());
                                assertEquals(2, block.children().size());

                                with(block.children().get(0), new Action<HtmlTag.Block>() {
                                    @Override
                                    public void apply(@NonNull HtmlTag.Block block) {
                                        assertEquals("li", block.name());
                                        assertEquals("OL-First", output.substring(block.start(), block.end()));
                                        assertEquals(0, block.children().size());
                                    }
                                });

                                with(block.children().get(1), new Action<HtmlTag.Block>() {
                                    @Override
                                    public void apply(@NonNull HtmlTag.Block block) {
                                        assertEquals("li", block.name());
                                        assertEquals("OL-Second", output.substring(block.start(), block.end()));
                                        assertEquals(0, block.children().size());
                                    }
                                });
                            }
                        });
                    }
                });

                with(block.children().get(2), new Action<HtmlTag.Block>() {
                    @Override
                    public void apply(@NonNull HtmlTag.Block block) {
                        assertEquals("li", block.name());
                        assertEquals("UL-Third", output.substring(block.start(), block.end()));
                        assertEquals(0, block.children().size());
                    }
                });
            }
        });
    }

    @Test
    public void attributes() {

        final MarkwonHtmlParserImpl impl = MarkwonHtmlParserImpl.create();
        final StringBuilder output = new StringBuilder();

        impl.processFragment(output, "<my-tag " +
                "name=no-name " +
                ":click='doSomething' " +
                "@focus=\"focus\" " +
                "@blur.native=\"blur\" " +
                "android:id=\"@id/id\">my-content</my-tag>");

        final CaptureBlockTagsAction action = new CaptureBlockTagsAction();
        impl.flushBlockTags(output.length(), action);

        assertTrue(action.called);
        assertEquals(1, action.tags.size());

        with(action.tags.get(0), new Action<HtmlTag.Block>() {
            @Override
            public void apply(@NonNull HtmlTag.Block block) {

                assertEquals("my-tag", block.name());

                with(block.attributes(), new Action<Map<String, String>>() {
                    @Override
                    public void apply(@NonNull Map<String, String> attributes) {
                        assertEquals(5, attributes.size());
                        assertEquals("no-name", attributes.get("name"));
                        assertEquals("doSomething", attributes.get(":click"));
                        assertEquals("focus", attributes.get("@focus"));
                        assertEquals("blur", attributes.get("@blur.native"));
                        assertEquals("@id/id", attributes.get("android:id"));
                    }
                });
            }
        });
    }

    @Test
    public void flushCloseTagsIfRequested() {

        final MarkwonHtmlParserImpl impl = MarkwonHtmlParserImpl.create();
        final StringBuilder output = new StringBuilder();

        impl.processFragment(output, "<div><i><b><em><strong>divibemstrong");

        final int end = output.length();

        final CaptureInlineTagsAction inlineTagsAction = new CaptureInlineTagsAction();
        final CaptureBlockTagsAction blockTagsAction = new CaptureBlockTagsAction();

        impl.flushInlineTags(end, inlineTagsAction);
        impl.flushBlockTags(end, blockTagsAction);

        assertTrue(inlineTagsAction.called);
        assertTrue(blockTagsAction.called);

        with(inlineTagsAction.tags, new Action<List<HtmlTag.Inline>>() {
            @Override
            public void apply(@NonNull List<HtmlTag.Inline> inlines) {
                assertEquals(4, inlines.size());
                for (HtmlTag.Inline inline : inlines) {
                    assertTrue(inline.isClosed());
                    assertEquals(end, inline.end());
                }
            }
        });

        assertEquals(1, blockTagsAction.tags.size());
        with(blockTagsAction.tags.get(0), new Action<HtmlTag.Block>() {
            @Override
            public void apply(@NonNull HtmlTag.Block block) {
                assertTrue(block.isClosed());
                assertEquals(end, block.end());
            }
        });
    }

    @Test
    public void flushDoesNotCloseTagsIfNoEndRequested() {

        final MarkwonHtmlParserImpl impl = MarkwonHtmlParserImpl.create();
        final StringBuilder output = new StringBuilder();

        impl.processFragment(output, "<div><i><b><em><strong>divibemstrong");

        final CaptureInlineTagsAction inlineTagsAction = new CaptureInlineTagsAction();
        final CaptureBlockTagsAction blockTagsAction = new CaptureBlockTagsAction();

        impl.flushInlineTags(HtmlTag.NO_END, inlineTagsAction);
        impl.flushBlockTags(HtmlTag.NO_END, blockTagsAction);

        assertTrue(inlineTagsAction.called);
        assertTrue(blockTagsAction.called);

        with(inlineTagsAction.tags, new Action<List<HtmlTag.Inline>>() {
            @Override
            public void apply(@NonNull List<HtmlTag.Inline> inlines) {
                assertEquals(4, inlines.size());
                for (HtmlTag.Inline inline : inlines) {
                    assertFalse(inline.isClosed());
                    assertEquals(HtmlTag.NO_END, inline.end());
                }
            }
        });

        assertEquals(1, blockTagsAction.tags.size());

        with(blockTagsAction.tags.get(0), new Action<HtmlTag.Block>() {
            @Override
            public void apply(@NonNull HtmlTag.Block block) {
                assertFalse(block.isClosed());
                assertEquals(HtmlTag.NO_END, block.end());
            }
        });
    }

    @Test
    public void flushClearsInternalState() {

        final MarkwonHtmlParserImpl impl = MarkwonHtmlParserImpl.create();
        final StringBuilder output = new StringBuilder();
        impl.processFragment(output, "<p><i>italic <b>bold italic</b></i></p><p>paragraph</p><div>and a div</div>");

        final CaptureInlineTagsAction inlineTagsAction = new CaptureInlineTagsAction();
        final CaptureBlockTagsAction blockTagsAction = new CaptureBlockTagsAction();

        impl.flushInlineTags(output.length(), inlineTagsAction);
        impl.flushBlockTags(output.length(), blockTagsAction);

        assertTrue(inlineTagsAction.called);
        assertTrue(blockTagsAction.called);

        assertEquals(2, inlineTagsAction.tags.size());
        assertEquals(3, blockTagsAction.tags.size());

        final CaptureInlineTagsAction captureInlineTagsAction = new CaptureInlineTagsAction();
        final CaptureBlockTagsAction captureBlockTagsAction = new CaptureBlockTagsAction();

        impl.flushInlineTags(output.length(), captureInlineTagsAction);
        impl.flushBlockTags(output.length(), captureBlockTagsAction);

        assertTrue(captureInlineTagsAction.called);
        assertTrue(captureBlockTagsAction.called);

        assertEquals(0, captureInlineTagsAction.tags.size());
        assertEquals(0, captureBlockTagsAction.tags.size());
    }

    @Test
    public void resetClearsBothInlinesAndBlocks() {

        final MarkwonHtmlParserImpl impl = MarkwonHtmlParserImpl.create();
        final StringBuilder output = new StringBuilder();

        impl.processFragment(output, "<p>paragraph <i>italic</i></p><div>div</div>");

        impl.reset();

        final CaptureInlineTagsAction inlineTagsAction = new CaptureInlineTagsAction();
        final CaptureBlockTagsAction blockTagsAction = new CaptureBlockTagsAction();

        impl.flushInlineTags(output.length(), inlineTagsAction);
        impl.flushBlockTags(output.length(), blockTagsAction);

        assertTrue(inlineTagsAction.called);
        assertTrue(blockTagsAction.called);

        assertEquals(0, inlineTagsAction.tags.size());
        assertEquals(0, blockTagsAction.tags.size());
    }

    @Test
    public void blockTagNewLine() {

        // we should make sure that a block tag will have a new line for it's
        // content (white spaces before should be ignored)

        final MarkwonHtmlParserImpl impl = MarkwonHtmlParserImpl.create();
        final String html = "<ul>" +
                "   <li>ul-first" +
                "   <li>ul-second" +
                "       <ol>" +
                "           <li>ol-first" +
                "           <li>ol-second" +
                "       </ol>" +
                "   <li>ul-third" +
                "</ul>";

        final StringBuilder output = new StringBuilder();
        impl.processFragment(output, html);

        final String[] split = output.toString().split("\n");
        assertEquals(Arrays.toString(split), 5, split.length);
    }

    @Test
    public void attributesAreLowerCase() {

        final MarkwonHtmlParserImpl impl = MarkwonHtmlParserImpl.create();
        final StringBuilder output = new StringBuilder();

        impl.processFragment(output, "<i CLASS=\"my-class\" dIsAbLeD @HeLLo=\"there\">");

        final CaptureInlineTagsAction action = new CaptureInlineTagsAction();
        impl.flushInlineTags(output.length(), action);

        assertTrue(action.called);
        assertEquals(1, action.tags.size());

        with(action.tags.get(0), new Action<HtmlTag.Inline>() {
            @Override
            public void apply(@NonNull HtmlTag.Inline inline) {

                assertEquals("i", inline.name());

                with(inline.attributes(), new Action<Map<String, String>>() {
                    @Override
                    public void apply(@NonNull Map<String, String> map) {
                        assertEquals(3, map.size());
                        assertEquals("my-class", map.get("class"));
                        assertEquals("", map.get("disabled"));
                        assertEquals("there", map.get("@hello"));
                    }
                });
            }
        });
    }

    @Test
    public void newLineAfterBlockTag() {

        final MarkwonHtmlParserImpl impl = MarkwonHtmlParserImpl.create();
        final StringBuilder output = new StringBuilder();

        final String[] fragments = {
                "<h1>head #1</h1>just text",
                "<h2>head #2</h2><span>in span tag</span>",
                "<h3>head #3</h3><custom-tag>in custom-tag</custom-tag>"
        };

        for (String fragment : fragments) {
            impl.processFragment(output, fragment);
        }

        final String expected = "" +
                "head #1\njust text\n" +
                "head #2\nin span tag\n" +
                "head #3\nin custom-tag";

        assertEquals(expected, output.toString());
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

    private interface Action<T> {
        void apply(@NonNull T t);
    }

    private static <T> void with(@NonNull T t, @NonNull Action<T> action) {
        action.apply(t);
    }
}