package io.noties.markwon.ext.inline;

import android.text.TextUtils;
import android.util.Patterns;

import androidx.annotation.NonNull;

import org.commonmark.node.Node;
import org.commonmark.node.Text;
import org.commonmark.parser.delimiter.DelimiterProcessor;
import org.commonmark.parser.delimiter.DelimiterRun;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InLineLinkProcessor implements DelimiterProcessor {

    private static final String TO_FIND = InLineLinkNode.DELIMITER_STRING;
    private static final Pattern PATTERN = Pattern.compile("#[0-9]{1,10}");

    @NonNull
    public static InLineLinkProcessor create() {
        return new InLineLinkProcessor();
    }


    @NonNull
    public static String prepare(@NonNull String input) {
        final StringBuilder builder = new StringBuilder(input);
        prepare(builder);
        return builder.toString();
    }

    public static void prepare(@NonNull StringBuilder builder) {

        int start = builder.indexOf(TO_FIND);
        int end;

        while (start > -1) {

            end = inLineDefinitionEnd(start + TO_FIND.length(), builder);
            if (iconDefinitionValid(builder.subSequence(start, end))) {
                builder.insert(end, '#');
            }
            // move to next
            start = builder.indexOf(TO_FIND, end);
        }
    }

    private static int inLineDefinitionEnd(int index, @NonNull StringBuilder builder) {

        // all spaces, new lines, non-words or digits,

        char c;

        int end = -1;
        for (int i = index; i < builder.length(); i++) {
            c = builder.charAt(i);
            if (Character.isWhitespace(c) || !Character.isDigit(c)) {
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

    @Override
    public char getOpeningCharacter() {
        return '#';
    }

    @Override
    public char getClosingCharacter() {
        return '#';
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

        final InLineLinkGroupNode inLineLinkGroupNode = new InLineLinkGroupNode();

        final Node next = opener.getNext();

        boolean handled = false;

        // process only if we have exactly one Text node

        if (next instanceof Text) {
            final String text = ((Text) next).getLiteral();

            if (!TextUtils.isEmpty(text)) {
                // attempt to match
                InLineLinkNode iconNode = new InLineLinkNode(InLineLinkNode.DELIMITER_STRING + text);
                inLineLinkGroupNode.appendChild(iconNode);
                next.unlink();
                handled = true;
            }
        }


        if (!handled) {

            // restore delimiters if we didn't match

            inLineLinkGroupNode.appendChild(new Text(InLineLinkNode.DELIMITER_STRING));

            Node node;
            for (Node tmp = opener.getNext(); tmp != null && tmp != closer; tmp = node) {
                node = tmp.getNext();
                // append a child anyway
                inLineLinkGroupNode.appendChild(tmp);
            }

            inLineLinkGroupNode.appendChild(new Text(InLineLinkNode.DELIMITER_STRING));
        }
        opener.insertBefore(inLineLinkGroupNode);

    }
}
