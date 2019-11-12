package io.noties.markwon.sample.editor.inline;

import androidx.annotation.NonNull;

import org.commonmark.internal.Bracket;
import org.commonmark.internal.Delimiter;
import org.commonmark.internal.util.Escaping;
import org.commonmark.node.Link;
import org.commonmark.node.Node;
import org.commonmark.node.Text;
import org.commonmark.parser.delimiter.DelimiterProcessor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Inline {

    private static final String ESCAPED_CHAR = "\\\\" + Escaping.ESCAPABLE;

    protected static final Pattern ESCAPABLE = Pattern.compile('^' + Escaping.ESCAPABLE);

    protected static final Pattern WHITESPACE = Pattern.compile("\\s+");

    protected static final Pattern SPNL = Pattern.compile("^ *(?:\n *)?");

    protected static final Pattern LINK_TITLE = Pattern.compile(
            "^(?:\"(" + ESCAPED_CHAR + "|[^\"\\x00])*\"" +
                    '|' +
                    "'(" + ESCAPED_CHAR + "|[^'\\x00])*'" +
                    '|' +
                    "\\((" + ESCAPED_CHAR + "|[^)\\x00])*\\))");

    protected static final Pattern LINK_DESTINATION_BRACES = Pattern.compile("^(?:[<](?:[^<> \\t\\n\\\\]|\\\\.)*[>])");

    protected static final Pattern LINK_LABEL = Pattern.compile("^\\[(?:[^\\\\\\[\\]]|\\\\.)*\\]");


    protected InlineContext context;
    protected Node block;
    protected int index;
    protected String input;

    protected void bind(
            @NonNull InlineContext context,
            @NonNull Node block,
            @NonNull String input,
            int index) {
        this.context = context;
        this.block = block;
        this.input = input;
        this.index = index;
    }

    @NonNull
    public abstract Collection<Character> characters();

    public abstract boolean parse();

    /**
     * If RE matches at current index in the input, advance index and return the match; otherwise return null.
     */
    protected String match(Pattern re) {
        if (index >= input.length()) {
            return null;
        }
        Matcher matcher = re.matcher(input);
        matcher.region(index, input.length());
        boolean m = matcher.find();
        if (m) {
            index = matcher.end();
            return matcher.group();
        } else {
            return null;
        }
    }

    protected void appendNode(Node node) {
        block.appendChild(node);
    }

    protected Text appendText(CharSequence text, int beginIndex, int endIndex) {
        return appendText(text.subSequence(beginIndex, endIndex));
    }

    protected Text appendText(CharSequence text) {
        Text node = new Text(text.toString());
        appendNode(node);
        return node;
    }

    /**
     * Returns the char at the current input index, or {@code '\0'} in case there are no more characters.
     */
    protected char peek() {
        if (index < input.length()) {
            return input.charAt(index);
        } else {
            return '\0';
        }
    }

    protected void addBracket(Bracket bracket) {
        final Bracket lastBracket = context.lastBracket();
        if (lastBracket != null) {
            lastBracket.bracketAfter = true;
        }
        context.lastBracket(bracket);
    }

    protected void removeLastBracket() {
        final InlineContext context = this.context;
        context.lastBracket(context.lastBracket().previous);
    }

    protected Bracket lastBracket() {
        return context.lastBracket();
    }

    protected Delimiter lastDelimiter() {
        return context.lastDelimiter();
    }

    protected Map<String, Link> referenceMap() {
        return context.referenceMap();
    }

    protected Map<Character, DelimiterProcessor> delimiterProcessors() {
        return context.delimiterProcessors();
    }

    /**
     * Parse zero or more space characters, including at most one newline.
     */
    protected boolean spnl() {
        match(SPNL);
        return true;
    }

    /**
     * Attempt to parse link destination, returning the string or null if no match.
     */
    protected String parseLinkDestination() {
        String res = match(LINK_DESTINATION_BRACES);
        if (res != null) { // chop off surrounding <..>:
            if (res.length() == 2) {
                return "";
            } else {
                return Escaping.unescapeString(res.substring(1, res.length() - 1));
            }
        } else {
            int startIndex = index;
            parseLinkDestinationWithBalancedParens();
            return Escaping.unescapeString(input.substring(startIndex, index));
        }
    }

    protected void parseLinkDestinationWithBalancedParens() {
        int parens = 0;
        while (true) {
            char c = peek();
            switch (c) {
                case '\0':
                    return;
                case '\\':
                    // check if we have an escapable character
                    if (index + 1 < input.length() && ESCAPABLE.matcher(input.substring(index + 1, index + 2)).matches()) {
                        // skip over the escaped character (after switch)
                        index++;
                        break;
                    }
                    // otherwise, we treat this as a literal backslash
                    break;
                case '(':
                    parens++;
                    break;
                case ')':
                    if (parens == 0) {
                        return;
                    } else {
                        parens--;
                    }
                    break;
                case ' ':
                    // ASCII space
                    return;
                default:
                    // or control character
                    if (Character.isISOControl(c)) {
                        return;
                    }
            }
            index++;
        }
    }

    /**
     * Attempt to parse link title (sans quotes), returning the string or null if no match.
     */
    protected String parseLinkTitle() {
        String title = match(LINK_TITLE);
        if (title != null) {
            // chop off quotes from title and unescape:
            return Escaping.unescapeString(title.substring(1, title.length() - 1));
        } else {
            return null;
        }
    }

    /**
     * Attempt to parse a link label, returning number of characters parsed.
     */
    protected int parseLinkLabel() {
        String m = match(LINK_LABEL);
        // Spec says "A link label can have at most 999 characters inside the square brackets"
        if (m == null || m.length() > 1001) {
            return 0;
        } else {
            return m.length();
        }
    }

    protected void processDelimiters(Delimiter stackBottom) {

        Map<Character, Delimiter> openersBottom = new HashMap<>();

        // find first closer above stackBottom:
        Delimiter closer = lastDelimiter();
        while (closer != null && closer.previous != stackBottom) {
            closer = closer.previous;
        }
        // move forward, looking for closers, and handling each
        while (closer != null) {
            char delimiterChar = closer.delimiterChar;

            DelimiterProcessor delimiterProcessor = delimiterProcessors().get(delimiterChar);
            if (!closer.canClose || delimiterProcessor == null) {
                closer = closer.next;
                continue;
            }

            char openingDelimiterChar = delimiterProcessor.getOpeningCharacter();

            // Found delimiter closer. Now look back for first matching opener.
            int useDelims = 0;
            boolean openerFound = false;
            boolean potentialOpenerFound = false;
            Delimiter opener = closer.previous;
            while (opener != null && opener != stackBottom && opener != openersBottom.get(delimiterChar)) {
                if (opener.canOpen && opener.delimiterChar == openingDelimiterChar) {
                    potentialOpenerFound = true;
                    useDelims = delimiterProcessor.getDelimiterUse(opener, closer);
                    if (useDelims > 0) {
                        openerFound = true;
                        break;
                    }
                }
                opener = opener.previous;
            }

            if (!openerFound) {
                if (!potentialOpenerFound) {
                    // Set lower bound for future searches for openers.
                    // Only do this when we didn't even have a potential
                    // opener (one that matches the character and can open).
                    // If an opener was rejected because of the number of
                    // delimiters (e.g. because of the "multiple of 3" rule),
                    // we want to consider it next time because the number
                    // of delimiters can change as we continue processing.
                    openersBottom.put(delimiterChar, closer.previous);
                    if (!closer.canOpen) {
                        // We can remove a closer that can't be an opener,
                        // once we've seen there's no matching opener:
                        removeDelimiterKeepNode(closer);
                    }
                }
                closer = closer.next;
                continue;
            }

            Text openerNode = opener.node;
            Text closerNode = closer.node;

            // Remove number of used delimiters from stack and inline nodes.
            opener.length -= useDelims;
            closer.length -= useDelims;
            openerNode.setLiteral(
                    openerNode.getLiteral().substring(0,
                            openerNode.getLiteral().length() - useDelims));
            closerNode.setLiteral(
                    closerNode.getLiteral().substring(0,
                            closerNode.getLiteral().length() - useDelims));

            removeDelimitersBetween(opener, closer);
            // The delimiter processor can re-parent the nodes between opener and closer,
            // so make sure they're contiguous already. Exclusive because we want to keep opener/closer themselves.
            mergeTextNodesBetweenExclusive(openerNode, closerNode);
            delimiterProcessor.process(openerNode, closerNode, useDelims);

            // No delimiter characters left to process, so we can remove delimiter and the now empty node.
            if (opener.length == 0) {
                removeDelimiterAndNode(opener);
            }

            if (closer.length == 0) {
                Delimiter next = closer.next;
                removeDelimiterAndNode(closer);
                closer = next;
            }
        }

        // remove all delimiters
        Delimiter lastDelimiter;
        while ((lastDelimiter = lastDelimiter()) != null) {
            if (lastDelimiter != stackBottom) {
                removeDelimiterKeepNode(lastDelimiter);
            } else {
                break;
            }
        }
//        while (lastDelimiter != null && lastDelimiter != stackBottom) {
//            removeDelimiterKeepNode(lastDelimiter);
//        }
    }

    private void mergeTextNodesBetweenExclusive(Node fromNode, Node toNode) {
        // No nodes between them
        if (fromNode == toNode || fromNode.getNext() == toNode) {
            return;
        }

        mergeTextNodesInclusive(fromNode.getNext(), toNode.getPrevious());
    }

    protected void mergeChildTextNodes(Node node) {
        // No children or just one child node, no need for merging
        if (node.getFirstChild() == node.getLastChild()) {
            return;
        }

        mergeTextNodesInclusive(node.getFirstChild(), node.getLastChild());
    }

    protected void mergeTextNodesInclusive(Node fromNode, Node toNode) {
        Text first = null;
        Text last = null;
        int length = 0;

        Node node = fromNode;
        while (node != null) {
            if (node instanceof Text) {
                Text text = (Text) node;
                if (first == null) {
                    first = text;
                }
                length += text.getLiteral().length();
                last = text;
            } else {
                mergeIfNeeded(first, last, length);
                first = null;
                last = null;
                length = 0;
            }
            if (node == toNode) {
                break;
            }
            node = node.getNext();
        }

        mergeIfNeeded(first, last, length);
    }

    protected void mergeIfNeeded(Text first, Text last, int textLength) {
        if (first != null && last != null && first != last) {
            StringBuilder sb = new StringBuilder(textLength);
            sb.append(first.getLiteral());
            Node node = first.getNext();
            Node stop = last.getNext();
            while (node != stop) {
                sb.append(((Text) node).getLiteral());
                Node unlink = node;
                node = node.getNext();
                unlink.unlink();
            }
            String literal = sb.toString();
            first.setLiteral(literal);
        }
    }

    protected void removeDelimitersBetween(Delimiter opener, Delimiter closer) {
        Delimiter delimiter = closer.previous;
        while (delimiter != null && delimiter != opener) {
            Delimiter previousDelimiter = delimiter.previous;
            removeDelimiterKeepNode(delimiter);
            delimiter = previousDelimiter;
        }
    }

    /**
     * Remove the delimiter and the corresponding text node. For used delimiters, e.g. `*` in `*foo*`.
     */
    protected void removeDelimiterAndNode(Delimiter delim) {
        Text node = delim.node;
        node.unlink();
        removeDelimiter(delim);
    }

    /**
     * Remove the delimiter but keep the corresponding node as text. For unused delimiters such as `_` in `foo_bar`.
     */
    protected void removeDelimiterKeepNode(Delimiter delim) {
        removeDelimiter(delim);
    }

    protected void removeDelimiter(Delimiter delim) {
        if (delim.previous != null) {
            delim.previous.next = delim.next;
        }
        if (delim.next == null) {
            // top of stack
//            lastDelimiter = delim.previous;
            context.lastDelimiter(delim.previous);
        } else {
            delim.next.previous = delim.previous;
        }
    }
}
