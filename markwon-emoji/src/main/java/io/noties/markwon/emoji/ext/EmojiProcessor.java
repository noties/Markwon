package io.noties.markwon.emoji.ext;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import org.commonmark.node.Node;
import org.commonmark.node.Text;
import org.commonmark.parser.delimiter.DelimiterProcessor;
import org.commonmark.parser.delimiter.DelimiterRun;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("WeakerAccess")
public class EmojiProcessor implements DelimiterProcessor {

    @NonNull
    public static EmojiProcessor create() {
        return new EmojiProcessor();
    }

    // ic-home-black-24
    private static final Pattern PATTERN = Pattern.compile("[a-z]{2,50}|[a-z]{2,50}[_][a-z]{2,50}|[a-z]{2,50}[_][a-z]{2,50}[_][a-z]{2,50}]");

    private static final String TO_FIND = EmojiNode.DELIMITER_STRING;

    /**
     * Should be used when input string does not wrap icon definition with `@` from both ends.
     * So, `@ic-home-white-24` would become `@ic-home-white-24@`. This way parsing is easier
     * and more predictable (cannot specify multiple ending delimiters, as we would require them:
     * space, newline, end of a document, and a lot of more)
     *
     * @param input to process
     * @return processed string
     * @see #prepare(StringBuilder)
     */
    @NonNull
    public static String prepare(@NonNull String input) {
        final StringBuilder builder = new StringBuilder(input);
        prepare(builder);
        return builder.toString();
    }

    public static void prepare(@NonNull StringBuilder builder) {

//        int start = builder.indexOf(TO_FIND);
//        int end;
//
//        while (start > -1) {
//
//            end = iconDefinitionEnd(start + TO_FIND.length(), builder);
//
//            // if we match our pattern, append `@` else ignore
//            if (iconDefinitionValid(builder.subSequence(start + 1, end))) {
//                builder.insert(end, '@');
//            }
//
//            // move to next
//            start = builder.indexOf(TO_FIND, end);
//        }
    }

    @Override
    public char getOpeningCharacter() {
        return EmojiNode.DELIMITER;
    }

    @Override
    public char getClosingCharacter() {
        return EmojiNode.DELIMITER;
    }

    @Override
    public int getMinLength() {
        return 1;
    }

    @Override
    public int getDelimiterUse(DelimiterRun opener, DelimiterRun closer) {
        return opener.length() >= 1 && closer.length() >= 1 ? 1 : 0;
    }

    @Override
    public void process(Text opener, Text closer, int delimiterUse) {

        final EmojiGroupNode emojiGroupNode = new EmojiGroupNode();

        final Node next = opener.getNext();

        boolean handled = false;

        // process only if we have exactly one Text node
        if (next instanceof Text && next.getNext() == closer) {

            final String text = ((Text) next).getLiteral();

            if (!TextUtils.isEmpty(text)) {

                // attempt to match
                final Matcher matcher = PATTERN.matcher(text);
                if (matcher.matches()) {
                    final EmojiNode emojiNode = new EmojiNode(
                        text
                    );
                    emojiGroupNode.appendChild(emojiNode);
                    next.unlink();
                    handled = true;
                }
            }
        }

        if (!handled) {

            // restore delimiters if we didn't match

            emojiGroupNode.appendChild(new Text(EmojiNode.DELIMITER_STRING));

            Node node;
            for (Node tmp = opener.getNext(); tmp != null && tmp != closer; tmp = node) {
                node = tmp.getNext();
                // append a child anyway
                emojiGroupNode.appendChild(tmp);
            }

            emojiGroupNode.appendChild(new Text(EmojiNode.DELIMITER_STRING));
        }

        opener.insertBefore(emojiGroupNode);
    }

    private static int iconDefinitionEnd(int index, @NonNull StringBuilder builder) {

        // all spaces, new lines, non-words or digits,

        char c;

        int end = -1;
        for (int i = index; i < builder.length(); i++) {
            c = builder.charAt(i);
            if (Character.isWhitespace(c)
                    || !(Character.isLetterOrDigit(c) || c == '-' || c == '_')) {
                end = i;
                break;
            }
        }

        if (end == -1) {
            end = builder.length();
        }

        return end;
    }

    private static boolean iconDefinitionValid(@NonNull CharSequence cs) {
        final Matcher matcher = PATTERN.matcher(cs);
        return matcher.matches();
    }
}
