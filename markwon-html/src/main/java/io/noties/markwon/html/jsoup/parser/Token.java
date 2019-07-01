package io.noties.markwon.html.jsoup.parser;

import androidx.annotation.NonNull;

import io.noties.markwon.html.jsoup.helper.Validate;
import io.noties.markwon.html.jsoup.nodes.Attributes;

import static io.noties.markwon.html.jsoup.helper.Normalizer.lowerCase;

/**
 * Parse tokens for the Tokeniser.
 */
public abstract class Token {

    public final TokenType type;

    protected Token(@NonNull TokenType tokenType) {
        this.type = tokenType;
    }

//    String tokenType() {
//        return this.getClass().getSimpleName();
//    }

    /**
     * Reset the data represent by this token, for reuse. Prevents the need to create transfer objects for every
     * piece of data, which immediately get GCed.
     */
    public abstract Token reset();

    static void reset(StringBuilder sb) {
        if (sb != null) {
            sb.delete(0, sb.length());
        }
    }

    public static final class Doctype extends Token {
        final StringBuilder name = new StringBuilder();
        String pubSysKey = null;
        final StringBuilder publicIdentifier = new StringBuilder();
        final StringBuilder systemIdentifier = new StringBuilder();
        boolean forceQuirks = false;

        Doctype() {
            super(TokenType.Doctype);
        }

        @Override
        public Token reset() {
            reset(name);
            pubSysKey = null;
            reset(publicIdentifier);
            reset(systemIdentifier);
            forceQuirks = false;
            return this;
        }

        String getName() {
            return name.toString();
        }

        String getPubSysKey() {
            return pubSysKey;
        }

        String getPublicIdentifier() {
            return publicIdentifier.toString();
        }

        public String getSystemIdentifier() {
            return systemIdentifier.toString();
        }

        public boolean isForceQuirks() {
            return forceQuirks;
        }
    }

    public static abstract class Tag extends Token {

        public String tagName;
        public String normalName; // lc version of tag name, for case insensitive tree build
        private String pendingAttributeName; // attribute names are generally caught in one hop, not accumulated
        private StringBuilder pendingAttributeValue = new StringBuilder(); // but values are accumulated, from e.g. & in hrefs
        private String pendingAttributeValueS; // try to get attr vals in one shot, vs Builder
        private boolean hasEmptyAttributeValue = false; // distinguish boolean attribute from empty string value
        private boolean hasPendingAttributeValue = false;
        public boolean selfClosing = false;
        public Attributes attributes; // start tags get attributes on construction. End tags get attributes on first new attribute (but only for parser convenience, not used).

        protected Tag(@NonNull TokenType tokenType) {
            super(tokenType);
        }

        @Override
        public Tag reset() {
            tagName = null;
            normalName = null;
            pendingAttributeName = null;
            reset(pendingAttributeValue);
            pendingAttributeValueS = null;
            hasEmptyAttributeValue = false;
            hasPendingAttributeValue = false;
            selfClosing = false;
            attributes = null;
            return this;
        }

        final void newAttribute() {
            if (attributes == null)
                attributes = new Attributes();

            if (pendingAttributeName != null) {
                // the tokeniser has skipped whitespace control chars, but trimming could collapse to empty for other control codes, so verify here
                pendingAttributeName = pendingAttributeName.trim();
                if (pendingAttributeName.length() > 0) {
                    String value;
                    if (hasPendingAttributeValue)
                        value = pendingAttributeValue.length() > 0 ? pendingAttributeValue.toString() : pendingAttributeValueS;
                    else if (hasEmptyAttributeValue)
                        value = "";
                    else
                        value = null;
                    attributes.put(pendingAttributeName, value);
                }
            }
            pendingAttributeName = null;
            hasEmptyAttributeValue = false;
            hasPendingAttributeValue = false;
            reset(pendingAttributeValue);
            pendingAttributeValueS = null;
        }

        final void finaliseTag() {
            // finalises for emit
            if (pendingAttributeName != null) {
                // todo: check if attribute name exists; if so, drop and error
                newAttribute();
            }
        }

        final String name() { // preserves case, for input into Tag.valueOf (which may drop case)
            Validate.isFalse(tagName == null || tagName.length() == 0);
            return tagName;
        }

        final String normalName() { // loses case, used in tree building for working out where in tree it should go
            return normalName;
        }

        final Tag name(String name) {
            tagName = name;
            normalName = lowerCase(name);
            return this;
        }

        final boolean isSelfClosing() {
            return selfClosing;
        }

        @SuppressWarnings({"TypeMayBeWeakened"})
        final Attributes getAttributes() {
            return attributes;
        }

        // these appenders are rarely hit in not null state-- caused by null chars.
        final void appendTagName(String append) {
            tagName = tagName == null ? append : tagName.concat(append);
            normalName = lowerCase(tagName);
        }

        final void appendTagName(char append) {
            appendTagName(String.valueOf(append));
        }

        final void appendAttributeName(String append) {
            pendingAttributeName = pendingAttributeName == null ? append : pendingAttributeName.concat(append);
        }

        final void appendAttributeName(char append) {
            appendAttributeName(String.valueOf(append));
        }

        final void appendAttributeValue(String append) {
            ensureAttributeValue();
            if (pendingAttributeValue.length() == 0) {
                pendingAttributeValueS = append;
            } else {
                pendingAttributeValue.append(append);
            }
        }

        final void appendAttributeValue(char append) {
            ensureAttributeValue();
            pendingAttributeValue.append(append);
        }

        final void appendAttributeValue(char[] append) {
            ensureAttributeValue();
            pendingAttributeValue.append(append);
        }

        final void appendAttributeValue(int[] appendCodepoints) {
            ensureAttributeValue();
            for (int codepoint : appendCodepoints) {
                pendingAttributeValue.appendCodePoint(codepoint);
            }
        }

        final void setEmptyAttributeValue() {
            hasEmptyAttributeValue = true;
        }

        private void ensureAttributeValue() {
            hasPendingAttributeValue = true;
            // if on second hit, we'll need to move to the builder
            if (pendingAttributeValueS != null) {
                pendingAttributeValue.append(pendingAttributeValueS);
                pendingAttributeValueS = null;
            }
        }
    }

    public final static class StartTag extends Tag {
        public StartTag() {
            super(TokenType.StartTag);
            attributes = new Attributes();
        }

        @Override
        public Tag reset() {
            super.reset();
            attributes = new Attributes();
            // todo - would prefer these to be null, but need to check Element assertions
            return this;
        }

        StartTag nameAttr(String name, Attributes attributes) {
            this.tagName = name;
            this.attributes = attributes;
            normalName = lowerCase(tagName);
            return this;
        }

        @Override
        public String toString() {
            if (attributes != null && attributes.size() > 0)
                return "<" + name() + " " + attributes.toString() + ">";
            else
                return "<" + name() + ">";
        }
    }

    public final static class EndTag extends Tag {
        EndTag() {
            super(TokenType.EndTag);
        }

        @Override
        public String toString() {
            return "</" + name() + ">";
        }
    }

    public final static class Comment extends Token {
        final StringBuilder data = new StringBuilder();
        boolean bogus = false;

        @Override
        public Token reset() {
            reset(data);
            bogus = false;
            return this;
        }

        Comment() {
            super(TokenType.Comment);
        }

        String getData() {
            return data.toString();
        }

        @Override
        public String toString() {
            return "<!--" + getData() + "-->";
        }
    }

    public static class Character extends Token {
        private String data;

        Character() {
            super(TokenType.Character);
        }

        @Override
        public Token reset() {
            data = null;
            return this;
        }

        Character data(String data) {
            this.data = data;
            return this;
        }

        public String getData() {
            return data;
        }

        @Override
        public String toString() {
            return getData();
        }
    }

    public final static class CData extends Character {
        CData(String data) {
            super();
            this.data(data);
        }

        @Override
        public String toString() {
            return "<![CDATA[" + getData() + "]]>";
        }

    }

    public final static class EOF extends Token {
        EOF() {
            super(Token.TokenType.EOF);
        }

        @Override
        public Token reset() {
            return this;
        }
    }

//    final boolean isDoctype() {
//        return type == TokenType.Doctype;
//    }
//
//    final Doctype asDoctype() {
//        return (Doctype) this;
//    }
//
//    final boolean isStartTag() {
//        return type == TokenType.StartTag;
//    }
//
//    final StartTag asStartTag() {
//        return (StartTag) this;
//    }
//
//    final boolean isEndTag() {
//        return type == TokenType.EndTag;
//    }
//
//    final EndTag asEndTag() {
//        return (EndTag) this;
//    }
//
//    final boolean isComment() {
//        return type == TokenType.Comment;
//    }
//
//    final Comment asComment() {
//        return (Comment) this;
//    }
//
//    final boolean isCharacter() {
//        return type == TokenType.Character;
//    }
//
//    final boolean isCData() {
//        return this instanceof CData;
//    }
//
//    final Character asCharacter() {
//        return (Character) this;
//    }
//
//    final boolean isEOF() {
//        return type == TokenType.EOF;
//    }

    public enum TokenType {
        Doctype,
        StartTag,
        EndTag,
        Comment,
        Character, // note no CData - treated in builder as an extension of Character
        EOF
    }
}
