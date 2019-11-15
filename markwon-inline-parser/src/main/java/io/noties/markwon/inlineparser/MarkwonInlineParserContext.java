package io.noties.markwon.inlineparser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.commonmark.internal.Bracket;
import org.commonmark.internal.Delimiter;
import org.commonmark.node.Link;
import org.commonmark.node.LinkReferenceDefinition;
import org.commonmark.node.Node;
import org.commonmark.node.Text;

import java.util.Map;
import java.util.regex.Pattern;

public interface MarkwonInlineParserContext {

    @NonNull
    Node block();

    @NonNull
    String input();

    int index();

    void setIndex(int index);

    Bracket lastBracket();

    Delimiter lastDelimiter();

    void addBracket(Bracket bracket);

    void removeLastBracket();

    void spnl();

    /**
     * Returns the char at the current input index, or {@code '\0'} in case there are no more characters.
     */
    char peek();

    @Nullable
    String match(@NonNull Pattern re);

    @NonNull
    Text text(@NonNull String text);

    @NonNull
    Text text(@NonNull String text, int beginIndex, int endIndex);

    @Nullable
    LinkReferenceDefinition getLinkReferenceDefinition(String label);

    @Nullable
    String parseLinkDestination();

    @Nullable
    String parseLinkTitle();

    int parseLinkLabel();

    void processDelimiters(Delimiter stackBottom);
}
