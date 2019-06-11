package io.noties.markwon.html;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class CssInlineStyleParser {

    @NonNull
    public abstract Iterable<CssProperty> parse(@NonNull String inlineStyle);

    @NonNull
    public static CssInlineStyleParser create() {
        return new Impl();
    }

    static class Impl extends CssInlineStyleParser {

        @NonNull
        @Override
        public Iterable<CssProperty> parse(@NonNull String inlineStyle) {
            return new CssIterable(inlineStyle);
        }

        private static class CssIterable implements Iterable<CssProperty> {

            private final String input;

            CssIterable(@NonNull String input) {
                this.input = input;
            }

            @NonNull
            @Override
            public Iterator<CssProperty> iterator() {
                return new CssIterator();
            }

            private class CssIterator implements Iterator<CssProperty> {

                private final CssProperty cssProperty = new CssProperty();

                private final StringBuilder builder = new StringBuilder();

                private final int length = input.length();

                private int index;

                @Override
                public boolean hasNext() {

                    prepareNext();

                    return hasNextPrepared();
                }

                @Override
                public CssProperty next() {
                    if (!hasNextPrepared()) {
                        throw new NoSuchElementException();
                    }
                    return cssProperty;
                }

                private void prepareNext() {

                    // clear first
                    cssProperty.set("", "");

                    builder.setLength(0);

                    String key = null;
                    String value = null;

                    char c;

                    boolean keyHasWhiteSpace = false;

                    for (int i = index; i < length; i++) {

                        c = input.charAt(i);

                        // if we are building KEY, then when we encounter WS (white-space) we finish
                        // KEY and wait for the ':', if we do not find it and we find EOF or ';'
                        // we start creating KEY again after the ';'

                        if (key == null) {

                            if (':' == c) {

                                // we have no key yet, but we might have started creating it already
                                if (builder.length() > 0) {
                                    key = builder.toString().trim();
                                }

                                builder.setLength(0);

                            } else {
                                // if by any chance we have here the ';' -> reset key and try to match next
                                if (';' == c) {
                                    builder.setLength(0);
                                } else {

                                    // key cannot have WS gaps (but leading and trailing are OK)
                                    if (Character.isWhitespace(c)) {
                                        if (builder.length() > 0) {
                                            keyHasWhiteSpace = true;
                                        }
                                    } else {
                                        // if not a WS and we have found WS before, start a-new
                                        // else append
                                        if (keyHasWhiteSpace) {
                                            // start new filling
                                            builder.setLength(0);
                                            builder.append(c);
                                            // clear this flag
                                            keyHasWhiteSpace = false;
                                        } else {
                                            builder.append(c);
                                        }
                                    }
                                }
                            }
                        } else if (value == null) {

                            if (Character.isWhitespace(c)) {
                                if (builder.length() > 0) {
                                    builder.append(c);
                                }
                            } else if (';' == c) {

                                value = builder.toString().trim();
                                builder.setLength(0);

                                // check if we have valid values -> if yes -> return it
                                if (hasValues(key, value)) {
                                    index = i + 1;
                                    cssProperty.set(key, value);
                                    return;
                                }

                            } else {
                                builder.append(c);
                            }
                        }
                    }

                    // here we must additionally check for EOF (we might be tracking value here)
                    if (key != null
                            && builder.length() > 0) {
                        value = builder.toString().trim();
                        cssProperty.set(key, value);
                        index = length;
                    }
                }

                private boolean hasNextPrepared() {
                    return hasValues(cssProperty.key(), cssProperty.value());
                }

                private boolean hasValues(@Nullable String key, @Nullable String value) {
                    return !TextUtils.isEmpty(key)
                            && !TextUtils.isEmpty(value);
                }
            }
        }
    }
}
