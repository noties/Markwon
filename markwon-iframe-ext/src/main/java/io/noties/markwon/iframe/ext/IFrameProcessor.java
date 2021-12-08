package io.noties.markwon.iframe.ext;

import static io.noties.markwon.iframe.ext.IFrameUtils.getDesmosId;
import static io.noties.markwon.iframe.ext.IFrameUtils.getVimeoVideoId;
import static io.noties.markwon.iframe.ext.IFrameUtils.getYoutubeVideoId;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import org.commonmark.node.Node;
import org.commonmark.node.Text;
import org.commonmark.parser.delimiter.DelimiterProcessor;
import org.commonmark.parser.delimiter.DelimiterRun;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IFrameProcessor implements DelimiterProcessor {

    private static final String TO_FIND_STRING = IFrameNode.DELIMITER_STRING;
    private static final String CLOSE_BRACKET = "]";
    public static final String REPLACE_STRING = "^";
    public static final char REPLACE_CHAR = '^';
    public static final String OPEN_STRING = "(";
    public static final String SEPARATOR_STRING = REPLACE_STRING + OPEN_STRING;
    public static final String CLOSE_STRING = ")";
    public static final char CLOSE_CHAR = ')';
    private static final Pattern PATTERN = Pattern.compile("!\\[[^\\]]*\\]\\([^)]+\\)");
    @NonNull
    public static IFrameProcessor create() {
        return new IFrameProcessor();
    }


    @NonNull
    public static String prepare(@NonNull String input) {
        final StringBuilder builder = new StringBuilder(input);
        if (builder.toString().toLowerCase().indexOf(TO_FIND_STRING) > -1) {
            prepare(builder, TO_FIND_STRING);
        }
        return builder.toString();
    }

    public static void prepare(@NonNull StringBuilder builder, String finder) {

        int start = builder.indexOf(finder);
        int end;

        while (start > -1) {

            end = inLineDefinitionEnd(start + finder.length(), builder);
            if (iconDefinitionValid(builder.subSequence(start, end))) {
                int startEndBracket = builder.indexOf(CLOSE_BRACKET, start + 2);
                if (checkingURLValidate(builder.substring(startEndBracket + 2, end - 1))) {
                    builder.replace(start, start + 2, REPLACE_STRING);
                    builder.replace(startEndBracket - 1, startEndBracket, REPLACE_STRING);
                }
            }
            // move to next
            start = builder.indexOf(finder, end);
        }
    }

    private static int inLineDefinitionEnd(int startIndex, @NonNull StringBuilder builder) {

        // all spaces, new lines, non-words or digits,

        char c;

        int end = -1;
        for (int i = startIndex; i < builder.length(); i++) {
            c = builder.charAt(i);
            if (c == ')') {
                end = i + 1;
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

    private static boolean checkingURLValidate(@NonNull String cs) {
        if (getYoutubeVideoId(cs) != null && !getYoutubeVideoId(cs).isEmpty()) {
            return true;
        }
        if (getVimeoVideoId(cs) != null && !getVimeoVideoId(cs).isEmpty()) {
            return true;
        }
        if (getDesmosId(cs) != null && !getDesmosId(cs).isEmpty()) {
            return true;
        }
        return  false;
    }

    @Override
    public char getOpeningCharacter() {
        return REPLACE_CHAR;
    }

    @Override
    public char getClosingCharacter() {
        return CLOSE_CHAR;
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

        final IFrameGroupNode iFrameGroupNode = new IFrameGroupNode();

        final Node next = opener.getNext();

        boolean handled = false;

        // process only if we have exactly one Text node


        final String text = ((Text) next).getLiteral();

        if (!TextUtils.isEmpty(text)) {
            // attempt to match
            int start_link_index = text.indexOf(OPEN_STRING);
            if (start_link_index >= 0) {
                String link = text.substring(start_link_index + OPEN_STRING.length());
                IFrameNode iFrameNode = new IFrameNode(link);
                iFrameGroupNode.appendChild(iFrameNode);
                next.unlink();
                handled = true;
            }
        }

        if (!handled) {
            iFrameGroupNode.appendChild(new Text(REPLACE_STRING));
            Node node;
            for (Node tmp = opener.getNext(); tmp != null && tmp != closer; tmp = node) {
                node = tmp.getNext();
                // append a child anyway
                iFrameGroupNode.appendChild(tmp);
            }

            iFrameGroupNode.appendChild(new Text(CLOSE_STRING));
        }
        opener.setLiteral("");
        closer.setLiteral("");
        opener.insertBefore(iFrameGroupNode);
    }
}
