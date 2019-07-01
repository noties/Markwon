package io.noties.markwon.html;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import io.noties.markwon.html.jsoup.nodes.Attribute;
import io.noties.markwon.html.jsoup.nodes.Attributes;
import io.noties.markwon.html.jsoup.parser.CharacterReader;
import io.noties.markwon.html.jsoup.parser.ParseErrorList;
import io.noties.markwon.html.jsoup.parser.Token;
import io.noties.markwon.html.jsoup.parser.Tokeniser;

import static io.noties.markwon.html.AppendableUtils.appendQuietly;

/**
 * @since 2.0.0
 */
public class MarkwonHtmlParserImpl extends MarkwonHtmlParser {

    @NonNull
    public static MarkwonHtmlParserImpl create() {
        return create(HtmlEmptyTagReplacement.create());
    }

    @NonNull
    public static MarkwonHtmlParserImpl create(@NonNull HtmlEmptyTagReplacement inlineTagReplacement) {
        return new MarkwonHtmlParserImpl(inlineTagReplacement, TrimmingAppender.create());
    }

    // https://developer.mozilla.org/en-US/docs/Web/HTML/Inline_elements
    @VisibleForTesting
    static final Set<String> INLINE_TAGS;

    private static final Set<String> VOID_TAGS;

    // these are the tags that are considered _block_ ones
    // this parser will ensure that these blocks are started on a new line
    // other tags that are NOT inline are considered as block tags, but won't have new line
    // inserted before them
    // https://developer.mozilla.org/en-US/docs/Web/HTML/Block-level_elements
    private static final Set<String> BLOCK_TAGS;

    private static final String TAG_PARAGRAPH = "p";
    private static final String TAG_LIST_ITEM = "li";

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

    private final TrimmingAppender trimmingAppender;

    private final List<HtmlTagImpl.InlineImpl> inlineTags = new ArrayList<>(0);

    private HtmlTagImpl.BlockImpl currentBlock = HtmlTagImpl.BlockImpl.root();

    private boolean isInsidePreTag;

    // the thing is: we ensure a new line BEFORE block tag
    // but not after, so another tag will be placed on the same line (which is wrong)
    private boolean previousIsBlock;


    MarkwonHtmlParserImpl(
            @NonNull HtmlEmptyTagReplacement replacement,
            @NonNull TrimmingAppender trimmingAppender) {
        this.emptyTagReplacement = replacement;
        this.trimmingAppender = trimmingAppender;
    }

    @Override
    public <T extends Appendable & CharSequence> void processFragment(
            @NonNull T output,
            @NonNull String htmlFragment) {

        // we might want to reuse tokeniser (at least when the same output is involved)
        // as CharacterReader does a bit of initialization (cache etc) as it's
        // primary usage is parsing a document in one run (not parsing _fragments_)
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
    public void flushInlineTags(int documentLength, @NonNull FlushAction<HtmlTag.Inline> action) {
        if (inlineTags.size() > 0) {

            if (documentLength > HtmlTag.NO_END) {
                for (HtmlTagImpl.InlineImpl inline : inlineTags) {
                    inline.closeAt(documentLength);
                }
            }

            action.apply(Collections.unmodifiableList((List<? extends HtmlTag.Inline>) inlineTags));
            inlineTags.clear();
        } else {
            action.apply(Collections.<HtmlTag.Inline>emptyList());
        }
    }

    @Override
    public void flushBlockTags(int documentLength, @NonNull FlushAction<HtmlTag.Block> action) {

        HtmlTagImpl.BlockImpl block = currentBlock;
        while (block.parent != null) {
            block = block.parent;
        }

        if (documentLength > HtmlTag.NO_END) {
            block.closeAt(documentLength);
        }

        final List<HtmlTag.Block> children = block.children();
        if (children.size() > 0) {
            action.apply(children);
        } else {
            action.apply(Collections.<HtmlTag.Block>emptyList());
        }

        currentBlock = HtmlTagImpl.BlockImpl.root();
    }

    @Override
    public void reset() {
        inlineTags.clear();
        currentBlock = HtmlTagImpl.BlockImpl.root();
    }


    protected <T extends Appendable & CharSequence> void processInlineTagStart(
            @NonNull T output,
            @NonNull Token.StartTag startTag) {

        final String name = startTag.normalName;

        final HtmlTagImpl.InlineImpl inline = new HtmlTagImpl.InlineImpl(name, output.length(), extractAttributes(startTag));

        ensureNewLineIfPreviousWasBlock(output);

        if (isVoidTag(name)
                || startTag.selfClosing) {

            final String replacement = emptyTagReplacement.replace(inline);
            if (replacement != null
                    && replacement.length() > 0) {
                AppendableUtils.appendQuietly(output, replacement);
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
        final HtmlTagImpl.InlineImpl openInline = findOpenInlineTag(endTag.normalName);
        if (openInline != null) {

            // okay, if this tag is empty -> call replacement
            if (isEmpty(output, openInline)) {
                appendEmptyTagReplacement(output, openInline);
            }

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
            AppendableUtils.appendQuietly(output, '\n');
            currentBlock = currentBlock.parent;
        } else if (TAG_LIST_ITEM.equals(name)
                && TAG_LIST_ITEM.equals(currentBlock.name)) {
            // close previous list item if in the same parent
            currentBlock.closeAt(output.length());
            currentBlock = currentBlock.parent;
        }

        if (isBlockTag(name)) {
            isInsidePreTag = "pre".equals(name);
            ensureNewLine(output);
        } else {
            ensureNewLineIfPreviousWasBlock(output);
        }

        final int start = output.length();

        final HtmlTagImpl.BlockImpl block = HtmlTagImpl.BlockImpl.create(name, start, extractAttributes(startTag), currentBlock);

        final boolean isVoid = isVoidTag(name) || startTag.selfClosing;
        if (isVoid) {
            final String replacement = emptyTagReplacement.replace(block);
            if (replacement != null
                    && replacement.length() > 0) {
                AppendableUtils.appendQuietly(output, replacement);
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

        final HtmlTagImpl.BlockImpl block = findOpenBlockTag(endTag.normalName);
        if (block != null) {

            if ("pre".equals(name)) {
                isInsidePreTag = false;
            }

            // okay, if this tag is empty -> call replacement
            if (isEmpty(output, block)) {
                appendEmptyTagReplacement(output, block);
            }

            block.closeAt(output.length());

            // if it's empty -> we do no care about if it's block or not
            if (!block.isEmpty()) {
                previousIsBlock = isBlockTag(block.name);
            }

            if (TAG_PARAGRAPH.equals(name)) {
                AppendableUtils.appendQuietly(output, '\n');
            }

            this.currentBlock = block.parent;
        }
    }

    protected <T extends Appendable & CharSequence> void processCharacter(
            @NonNull T output,
            @NonNull Token.Character character) {

        // there are tags: BUTTON, INPUT, SELECT, SCRIPT, TEXTAREA, STYLE
        // that might have character data that we do not want to display

        if (isInsidePreTag) {
            appendQuietly(output, character.getData());
        } else {
            ensureNewLineIfPreviousWasBlock(output);
            trimmingAppender.append(output, character.getData());
        }
    }

    protected void appendBlockChild(@NonNull HtmlTagImpl.BlockImpl parent, @NonNull HtmlTagImpl.BlockImpl child) {
        List<HtmlTagImpl.BlockImpl> children = parent.children;
        if (children == null) {
            children = new ArrayList<>(2);
            parent.children = children;
        }
        children.add(child);
    }

    @Nullable
    protected HtmlTagImpl.InlineImpl findOpenInlineTag(@NonNull String name) {

        HtmlTagImpl.InlineImpl inline;

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
    protected HtmlTagImpl.BlockImpl findOpenBlockTag(@NonNull String name) {

        HtmlTagImpl.BlockImpl blockTag = currentBlock;

        while (blockTag != null
                && !name.equals(blockTag.name) && !blockTag.isClosed()) {
            blockTag = blockTag.parent;
        }

        return blockTag;
    }

    protected <T extends Appendable & CharSequence> void ensureNewLineIfPreviousWasBlock(@NonNull T output) {
        if (previousIsBlock) {
            ensureNewLine(output);
            previousIsBlock = false;
        }
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

    protected static <T extends Appendable & CharSequence> void ensureNewLine(@NonNull T output) {
        final int length = output.length();
        if (length > 0
                && '\n' != output.charAt(length - 1)) {
            AppendableUtils.appendQuietly(output, '\n');
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

    protected static <T extends Appendable & CharSequence> boolean isEmpty(
            @NonNull T output,
            @NonNull HtmlTagImpl tag) {
        return tag.start == output.length();
    }

    protected <T extends Appendable & CharSequence> void appendEmptyTagReplacement(
            @NonNull T output,
            @NonNull HtmlTagImpl tag) {
        final String replacement = emptyTagReplacement.replace(tag);
        if (replacement != null) {
            AppendableUtils.appendQuietly(output, replacement);
        }
    }
}
