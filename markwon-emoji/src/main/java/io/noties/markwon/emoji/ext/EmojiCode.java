package io.noties.markwon.emoji.ext;

import java.util.List;

public class EmojiCode {

    private String unicode;
    private String unicode_alternates;
    private String name;
    private String shortname;
    private String category;
    private String emoji_order;
    private List<Object> aliases;
    private List<Object> aliases_ascii;
    private List<String> keywords;

    public enum EMOJI_SIZE{
        SMALL, MEDIDUM, LARGE;
    }

    public String getImageName(){
        String code = unicode.replace("-", "_");
        code = "tw_" + code;
        return code;
    }

    public String getUnicode() {
        return unicode;
    }

    public String getUnicode_alternates() {
        return unicode_alternates;
    }

    public String getName() {
        return name;
    }

    public String getShortname() {
        return shortname;
    }

    public String getCategory() {
        return category;
    }

    public String getEmoji_order() {
        return emoji_order;
    }

    public List<Object> getAliases() {
        return aliases;
    }

    public List<Object> getAliases_ascii() {
        return aliases_ascii;
    }

    public List<String> getKeywords() {
        return keywords;
    }
}
