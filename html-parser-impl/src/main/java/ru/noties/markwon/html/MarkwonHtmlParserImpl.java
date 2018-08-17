package ru.noties.markwon.html;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import ru.noties.markwon.html.HtmlTag.Block;
import ru.noties.markwon.html.HtmlTag.Inline;
import ru.noties.markwon.html.HtmlTagImpl.BlockImpl;
import ru.noties.markwon.html.HtmlTagImpl.InlineImpl;
import ru.noties.markwon.html.jsoup.nodes.Attribute;
import ru.noties.markwon.html.jsoup.nodes.Attributes;
import ru.noties.markwon.html.jsoup.parser.CharacterReader;
import ru.noties.markwon.html.jsoup.parser.ParseErrorList;
import ru.noties.markwon.html.jsoup.parser.Token;
import ru.noties.markwon.html.jsoup.parser.Tokeniser;

public class MarkwonHtmlParserImpl extends MarkwonHtmlParser {

    @NonNull
    public static MarkwonHtmlParserImpl create() {
        return create(HtmlEmptyTagReplacement.create());
    }

    @NonNull
    public static MarkwonHtmlParserImpl create(@NonNull HtmlEmptyTagReplacement inlineTagReplacement) {
        return new MarkwonHtmlParserImpl(inlineTagReplacement);
    }

    // https://developer.mozilla.org/en-US/docs/Web/HTML/Inline_elements
    private static final Set<String> INLINE_TAGS;

    private static final Set<String> VOID_TAGS;

    // these are the tags that are considered _block_ ones
    // this parser will ensure that these blocks are started on a new line
    // other tags that are NOT inline are considered as block tags, but won't have new line
    // inserted before them
    // https://developer.mozilla.org/en-US/docs/Web/HTML/Block-level_elements
    private static final Set<String> BLOCK_TAGS;

    private static final String TAG_PARAGRAPH = "p";
    private static final String TAG_LIST_ITEM = "li";

    // todo: make it configurable
//    private static final String IMG_REPLACEMENT = "\uFFFC";

    static {
        INLINE_TAGS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
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
        )));
        VOID_TAGS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
                "area",
                "base", "br",
                "col",
                "embed",
                "hr",
                "img", "input",
                "keygen",
                "link",
                "meta",
                "param",
                "source",
                "track",
                "wbr"
        )));
        BLOCK_TAGS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
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
        )));
    }

    private final HtmlEmptyTagReplacement emptyTagReplacement;

    private final List<InlineImpl> inlineTags = new ArrayList<>(0);

    private BlockImpl currentBlock = BlockImpl.root();

    MarkwonHtmlParserImpl(@NonNull HtmlEmptyTagReplacement replacement) {
        this.emptyTagReplacement = replacement;
    }


    @Override
    public <T extends Appendable & CharSequence> void processFragment(
            @NonNull T output,
            @NonNull String htmlFragment) {

        // todo: maybe there is a way to reuse tokeniser...
        final Tokeniser tokeniser = new Tokeniser(new CharacterReader(htmlFragment), ParseErrorList.noTracking());

        while (true) {

            final Token token = tokeniser.read();
            final Token.TokenType tokenType = token.type;

            if (Token.TokenType.EOF == tokenType) {
                break;
            }

            switch (tokenType) {

                case StartTag: {

                    final Token.StartTag startTag = (Token.StartTag) token;

                    if (isInlineTag(startTag.normalName)) {
                        processInlineTagStart(output, startTag);
                    } else {
                        processBlockTagStart(output, startTag);
                    }
                }
                break;

                case EndTag: {

                    final Token.EndTag endTag = (Token.EndTag) token;

                    if (isInlineTag(endTag.normalName)) {
                        processInlineTagEnd(output, endTag);
                    } else {
                        processBlockTagEnd(output, endTag);
                    }
                }
                break;

                case Character: {
                    processCharacter(output, ((Token.Character) token));
                }
                break;
            }

            // do not forget to reset processed token (even if it's not processed)
            token.reset();
        }
    }

    @Override
    public void flushInlineTags(int documentLength, @NonNull FlushAction<Inline> action) {
        if (inlineTags.size() > 0) {
            for (InlineImpl inline : inlineTags) {
                inline.closeAt(documentLength);
            }
            //noinspection unchecked
            action.apply(Collections.unmodifiableList((List<? extends Inline>) inlineTags));
            inlineTags.clear();
        }
    }

    @Override
    public void flushBlockTags(int documentLength, @NonNull FlushAction<Block> action) {

        BlockImpl block = currentBlock;
        while (!block.isRoot()) {
            block = block.parent;
        }

        block.closeAt(documentLength);

        final List<Block> children = block.children();
        if (children.size() > 0) {
            action.apply(children);
        }

        currentBlock = BlockImpl.root();
    }

    @Override
    public void reset() {
        inlineTags.clear();
        currentBlock = BlockImpl.root();
    }


    protected <T extends Appendable & CharSequence> void processInlineTagStart(
            @NonNull T output,
            @NonNull Token.StartTag startTag) {

        final String name = startTag.normalName;

        final InlineImpl inline = new InlineImpl(name, output.length(), extractAttributes(startTag));

        if (isVoidTag(name)
                || startTag.selfClosing) {

            final String replacement = emptyTagReplacement.replace(startTag);
            if (replacement != null
                    && replacement.length() > 0) {
                append(output, replacement);
            }

            // the thing is: we will keep this inline tag in the list,
            // but in case of void-tag that has no replacement, there will be no
            // possibility to set a span (requires at least one char)
            inline.closeAt(output.length());
        }

        inlineTags.add(inline);
    }

    protected <T extends Appendable & CharSequence> void processInlineTagEnd(
            @NonNull T output,
            @NonNull Token.EndTag endTag) {

        // try to find it, if none found -> ignore
        final InlineImpl openInline = findOpenInlineTag(endTag.normalName);
        if (openInline != null) {
            // close open inline tag
            openInline.closeAt(output.length());
        }
    }


    protected <T extends Appendable & CharSequence> void processBlockTagStart(
            @NonNull T output,
            @NonNull Token.StartTag startTag) {

        final String name = startTag.normalName;

        // block tags (all that are NOT inline -> blocks
        // there is only one strong rule -> paragraph cannot contain anything
        // except inline tags

        if (TAG_PARAGRAPH.equals(currentBlock.name)) {
            // it must be closed here not matter what we are as here we _assume_
            // that it's a block tag
            currentBlock.closeAt(output.length());
            append(output, "\n");
            currentBlock = currentBlock.parent;
        } else if (TAG_LIST_ITEM.equals(name)
                && TAG_LIST_ITEM.equals(currentBlock.name)) {
            // close previous list item if in the same parent
            currentBlock.closeAt(output.length());
            currentBlock = currentBlock.parent;
        }

        if (isBlockTag(name)) {
            ensureNewLine(output);
        }

        final int start = output.length();

        final BlockImpl block = BlockImpl.create(name, start, extractAttributes(startTag), currentBlock);

        final boolean isVoid = isVoidTag(name) || startTag.selfClosing;
        if (isVoid) {
            final String replacement = emptyTagReplacement.replace(startTag);
            if (replacement != null
                    && replacement.length() > 0) {
                append(output, replacement);
            }
            block.closeAt(output.length());
        }

        //noinspection ConstantConditions
        appendBlockChild(block.parent, block);

        // if not void start filling-in children
        if (!isVoid) {
            this.currentBlock = block;
        }
    }

    protected <T extends Appendable & CharSequence> void processBlockTagEnd(
            @NonNull T output,
            @NonNull Token.EndTag endTag) {

        final String name = endTag.normalName;

        final BlockImpl block = findOpenBlockTag(endTag.normalName);
        if (block != null) {

            block.closeAt(output.length());

            if (TAG_PARAGRAPH.equals(name)) {
                append(output, "\n");
            }

            this.currentBlock = block.parent;
        }
    }

    protected <T extends Appendable & CharSequence> void processCharacter(
            @NonNull T output,
            @NonNull Token.Character character) {

        // the thing here is: if it's a script tag that we are inside -> we must not treat this
        // as the text to append... should we even care about this? how many people are
        // going to include freaking script tags as html inline?
        //
        // so tags are: BUTTON, INPUT, SELECT, SCRIPT, TEXTAREA
        //
        // actually we must decide it here: should we append freaking characters for these _bad_
        // tags or not, as later we won't be able to change it and/or allow modification (as
        // all indexes will be affected with this)

        // for now: ignore the inline context
        append(output, character.getData());
    }

    protected void appendBlockChild(@NonNull BlockImpl parent, @NonNull BlockImpl child) {
        List<BlockImpl> children = parent.children;
        if (children == null) {
            children = new ArrayList<>(2);
            parent.children = children;
        }
        children.add(child);
    }

    @Nullable
    protected InlineImpl findOpenInlineTag(@NonNull String name) {

        InlineImpl inline;

        for (int i = inlineTags.size() - 1; i > -1; i--) {
            inline = inlineTags.get(i);
            if (name.equals(inline.name)
                    && inline.end < 0) {
                return inline;
            }
        }

        return null;
    }

    @Nullable
    protected BlockImpl findOpenBlockTag(@NonNull String name) {

        BlockImpl blockTag = currentBlock;

        while (blockTag != null
                && !name.equals(blockTag.name) && !blockTag.isClosed()) {
            blockTag = blockTag.parent;
        }

        return blockTag;
    }

    // name here must lower case
    protected static boolean isInlineTag(@NonNull String name) {
        return INLINE_TAGS.contains(name);
    }

    protected static boolean isVoidTag(@NonNull String name) {
        return VOID_TAGS.contains(name);
    }

    protected static boolean isBlockTag(@NonNull String name) {
        return BLOCK_TAGS.contains(name);
    }

    protected static void append(@NonNull Appendable appendable, @NonNull CharSequence text) {
        try {
            appendable.append(text);
        } catch (IOException e) {
            // _must_ not happen
            throw new RuntimeException(e);
        }
    }

    protected static <T extends Appendable & CharSequence> void ensureNewLine(@NonNull T output) {
        final int length = output.length();
        if (length > 0
                && '\n' != output.charAt(length - 1)) {
            append(output, "\n");
        }
    }

    @NonNull
    protected static Map<String, String> extractAttributes(@NonNull Token.StartTag startTag) {

        Map<String, String> map;

        final Attributes attributes = startTag.attributes;
        final int size = attributes.size();

        if (size > 0) {
            map = new HashMap<>(size);
            for (Attribute attribute : attributes) {
                map.put(attribute.getKey().toLowerCase(Locale.US), attribute.getValue());
            }
            map = Collections.unmodifiableMap(map);
        } else {
            map = Collections.emptyMap();
        }

        return map;
    }
}
