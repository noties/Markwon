package noties.ru.markwon_samplecustomextension;

import android.text.TextUtils;

import org.commonmark.node.Node;
import org.commonmark.node.Text;
import org.commonmark.parser.delimiter.DelimiterProcessor;
import org.commonmark.parser.delimiter.DelimiterRun;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IconProcessor implements DelimiterProcessor {

    private static final Pattern PATTERN = Pattern.compile("material-icon-(\\w+)-(\\w+)-(\\w+)");

    @Override
    public char getOpeningCharacter() {
        return IconNode.DELIMITER;
    }

    @Override
    public char getClosingCharacter() {
        return IconNode.DELIMITER;
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

        final IconGroupNode iconGroupNode = new IconGroupNode();

        final Node next = opener.getNext();

        boolean handled = false;

        // process only if we have exactly one Text node
        if (next instanceof Text && next.getNext() == closer) {

            final String text = ((Text) next).getLiteral();

            if (!TextUtils.isEmpty(text)) {

                // attempt to match
                final Matcher matcher = PATTERN.matcher(text);
                if (matcher.matches()) {
                    final IconNode iconNode = new IconNode(
                            matcher.group(1),
                            matcher.group(2),
                            matcher.group(3)
                    );
                    iconGroupNode.appendChild(iconNode);
                    next.unlink();
                    handled = true;
                }
            }
        }

        if (!handled) {

            // restore delimiters if we didn't match

            iconGroupNode.appendChild(new Text(IconNode.DELIMITER_STRING));

            Node node;
            for (Node tmp = opener.getNext(); tmp != null && tmp != closer; tmp = node) {
                node = tmp.getNext();
                // append a child anyway
                iconGroupNode.appendChild(tmp);
            }

            iconGroupNode.appendChild(new Text(IconNode.DELIMITER_STRING));
        }

        opener.insertBefore(iconGroupNode);
    }
}
